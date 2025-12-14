package com.utime.burrowNest.common.jwt;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import com.utime.burrowNest.common.util.BurrowUtils;
import com.utime.burrowNest.common.vo.BurrowDefine;
import com.utime.burrowNest.common.vo.ReturnBasic;
import com.utime.burrowNest.user.dao.UserDao;
import com.utime.burrowNest.user.vo.ResUserVo;
import com.utime.burrowNest.user.vo.UserVo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtProvider {

    private static final long ONE_SECOND = 1000L;

    /** 15분 */
    private static final long ACCESS_EXP_MS  = 15L * 60L * ONE_SECOND;
    /** 1일 */
    private static final long PAGING_EXP_MS  = 1L * 24L * 60L * 60L * ONE_SECOND;
    /** 7일 */
    private static final long REFRESH_EXP_MS = 7L * 24L * 60L * 60L * ONE_SECOND;

    private static final String COOKIE_ACCESS  = "accessToken";
    private static final String COOKIE_PAGING  = "pagingToken";
    private static final String COOKIE_REFRESH = BurrowDefine.KeyRefreshToken;

    private static final String HDR_AUTH = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private static final String CLM_IP     = "ReqIp";
    private static final String CLM_AGENT  = "ReqAgent";
    private static final String CLM_USERNO = "userNo";
    private static final String CLM_SID    = "sid"; // 세션/디바이스 바인딩용(선택)

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey key;
    private final MacAlgorithm macAlgo = Jwts.SIG.HS256;

    @Autowired
    private UserDao userDao;

    /**
     * IP/UA 바인딩을 강제할지 여부.
     * 운영 환경에서 IP 변경이 잦으면 false 권장.
     */
    @Value("${jwt.bindRequest:false}")
    private boolean bindRequest;

    /**
     * HTTPS 환경이면 true 권장(운영 필수급).
     * request.isSecure() 기준으로 동적으로도 처리 가능하지만,
     * 프록시/로드밸런서 환경에서 오동작할 수 있어 설정으로 받는 편이 안전합니다.
     */
    @Value("${jwt.cookie.secure:true}")
    private boolean cookieSecure;

    @PostConstruct
    public void init() {
        final byte[] secretBytes = secret == null ? new byte[0] : secret.getBytes(StandardCharsets.UTF_8);

        // HS256은 충분한 키 길이가 필요합니다(너무 짧으면 런타임 예외/약한 키).
        if (secretBytes.length < 32) {
            // 운영에서 이 로그는 꼭 눈에 띄게 하는 게 좋습니다.
            log.warn("jwt.secret 길이가 짧습니다. HS256은 최소 32바이트 이상 권장입니다. (현재: {} bytes)", secretBytes.length);
        }

        this.key = Keys.hmacShaKeyFor(secretBytes);
    }

    /**
     * Authorization 헤더 또는 accessToken 쿠키에서 JWT 문자열을 가져옵니다.
     */
    private String resolveAccessToken(HttpServletRequest request) {
        final String header = request.getHeader(HDR_AUTH);
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        return getCookieValue(request, COOKIE_ACCESS);
    }

    private String getCookieValue(HttpServletRequest request, String name) {
        final Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        for (Cookie c : cookies) {
            if (name.equals(c.getName())) return c.getValue();
        }
        return null;
    }

    /**
     * JWT 파싱(서명/만료 검증 포함).
     * - 만료는 ExpiredJwtException으로 분리해서 핸들링 가능하게 함
     */
    private Claims parseClaims(String token) {
        if (token == null || token.isBlank()) return null;

        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            // 만료는 흔한 케이스라 error로 남기지 않는 편이 운영에 유리합니다.
            log.debug("JWT expired");
            return null;
        } catch (JwtException e) {
            log.warn("Invalid JWT: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 토큰 생성.
     */
    private String generateToken(UserVo user, Map<String, Object> claims, long expMs) {
        final Map<String, Object> safeClaims = (claims == null) ? new HashMap<>() : new HashMap<>(claims);
        safeClaims.put(CLM_USERNO, String.valueOf(user.getUserNo()));

        final Instant now = Instant.now();
        return Jwts.builder()
                .id(user.getId())
                .claims(safeClaims)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expMs)))
                .signWith(this.key, this.macAlgo)
                .compact();
    }

    /**
     * 쿠키 생성(응답 헤더로 SameSite까지 포함).
     */
    private void addTokenCookie(HttpServletRequest req, HttpServletResponse res,
                                String cookieName, String token, long expMs) {
        final String path = Optional.ofNullable(BurrowDefine.ContextPath).orElse("/");
        final String domain = req.getServerName(); // 필요 시 설정으로 분리 권장(서브도메인/localhost 이슈)

        final ResponseCookie cookie = ResponseCookie.from(cookieName, token)
                .httpOnly(true)
                .secure(cookieSecure)
                .path(path)
                .domain(domain)
                .maxAge(Duration.ofMillis(expMs))
                .sameSite("Lax") // 대부분의 로그인 쿠키 기본값으로 무난 (요구사항에 따라 Strict/None 조정)
                .build();

        res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    /**
     * 쿠키 삭제(표준: maxAge=0 + 빈 값).
     */
    private void deleteCookie(HttpServletRequest req, HttpServletResponse res, String cookieName) {
        final String path = Optional.ofNullable(BurrowDefine.ContextPath).orElse("/");
        final String domain = req.getServerName();

        final ResponseCookie cookie = ResponseCookie.from(cookieName, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path(path)
                .domain(domain)
                .maxAge(Duration.ZERO)
                .sameSite("Lax")
                .build();

        res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    /**
     * 요청 IP/UA 바인딩용 claims 생성(로그 최소화).
     */
    private Map<String, Object> createRequestBindClaims(HttpServletRequest request, String sid) {
        final Map<String, Object> claims = new HashMap<>();
        claims.put(CLM_IP, BurrowUtils.getRemoteAddress(request));
        claims.put(CLM_AGENT, request.getHeader(HttpHeaders.USER_AGENT));
        if (sid != null) claims.put(CLM_SID, sid);
        return claims;
    }

    /**
     * bindRequest=true일 때만 IP/UA 검증 수행.
     */
    private boolean validateRequestBinding(HttpServletRequest request, Claims claims) {
        if (!bindRequest) return true;

        final String tokenIp = claims.get(CLM_IP, String.class);
        final String reqIp = BurrowUtils.getRemoteAddress(request);
        if (tokenIp != null && !tokenIp.equals(reqIp)) {
            log.warn("Req IP mismatch. token={}, req={}", tokenIp, reqIp);
            return false;
        }

        final String tokenAgent = claims.get(CLM_AGENT, String.class);
        final String reqAgent = request.getHeader(HttpHeaders.USER_AGENT);
        if (tokenAgent != null && reqAgent != null && !tokenAgent.equals(reqAgent)) {
            log.warn("Req UA mismatch.");
            return false;
        }

        // 한쪽이 null인 경우는 운영 환경에서 종종 있어 “바로 실패”보다 통과가 안전할 때가 많습니다.
        return true;
    }

    /**
     * Claims 기반으로 사용자 로드 + 기본 정합성 체크
     */
    private UserVo extractUser(Claims claims) {
        final String id = claims.getId();
        if (id == null || id.isBlank()) return null;

        final UserVo user = userDao.getUserFormIdByProvider(id);
        if (user == null) return null;

        final int tokenUserNo = NumberUtils.toInt(claims.get(CLM_USERNO, String.class), -1);
        if (tokenUserNo < 0 || user.getUserNo() != tokenUserNo) {
            log.warn("User mismatch. tokenUserNo={}, dbUserNo={}", tokenUserNo, user.getUserNo());
            return null;
        }
        return user;
    }

    /**
     * 로그인: access/paging/refresh 발급
     */
    public ReturnBasic procLogin(HttpServletRequest request, HttpServletResponse response, UserVo user) {
        if (user == null) return new ReturnBasic("E", "사용자 정보 없음");

        // sid: access/paging/refresh 간 연결고리(원하면 더 적극적으로 검증에 사용 가능)
        final String sid = java.util.UUID.randomUUID().toString();

        final String access = generateToken(user, Map.of(CLM_SID, sid), ACCESS_EXP_MS);
        addTokenCookie(request, response, COOKIE_ACCESS, access, ACCESS_EXP_MS);

        final String paging = generateToken(user, createRequestBindClaims(request, sid), PAGING_EXP_MS);
        addTokenCookie(request, response, COOKIE_PAGING, paging, PAGING_EXP_MS);

        final String refresh = generateToken(user, createRequestBindClaims(request, sid), REFRESH_EXP_MS);
        addTokenCookie(request, response, COOKIE_REFRESH, refresh, REFRESH_EXP_MS);

        return new ReturnBasic();
    }

    /**
     * Access 만료/부재 시 pagingToken으로 access 재발급(세션 유지용)
     */
    public ResUserVo procPagingToken(HttpServletRequest request, HttpServletResponse response) {
        String token = resolveAccessToken(request);
        Claims claims = parseClaims(token);

        // access가 없거나 만료/파싱 실패면 paging으로 시도
        if (claims == null) {
            token = getCookieValue(request, COOKIE_PAGING);
            if (token == null) return new ResUserVo("E", "토큰 만료");

            claims = parseClaims(token);
            if (claims == null) return new ResUserVo("E", "토큰 만료");
        }

        if (!validateRequestBinding(request, claims)) {
            return new ResUserVo("E", "유효성 검사 실패");
        }

        final UserVo user = extractUser(claims);
        if (user == null) return new ResUserVo("E", "사용자 정보 불일치");

        // 재발급(rotate sid까지 하고 싶으면 procLogin으로 돌려도 됨)
        final String sid = claims.get(CLM_SID, String.class);
        final String newAccess = generateToken(user, sid == null ? null : Map.of(CLM_SID, sid), ACCESS_EXP_MS);
        addTokenCookie(request, response, COOKIE_ACCESS, newAccess, ACCESS_EXP_MS);

        final ResUserVo result = new ResUserVo();
        result.setUser(user);
        return result;
    }

    /**
     * Refresh로 재로그인(Access 재발급)
     */
    public ReturnBasic procRefresh(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        final Claims claims = parseClaims(refreshToken);
        if (claims == null) return new ReturnBasic("E", "토큰 만료");

        if (!validateRequestBinding(request, claims)) {
            return new ReturnBasic("E", "유효성 검사 실패");
        }

        final UserVo user = extractUser(claims);
        if (user == null) return new ReturnBasic("E", "사용자 정보 불일치");

        // refresh까지 같이 rotate하려면 procLogin 호출이 가장 단순
        return procLogin(request, response, user);
    }

    /**
     * 로그아웃: access/paging/refresh 모두 삭제
     */
    public void procLogout(HttpServletRequest request, HttpServletResponse response) {
        deleteCookie(request, response, COOKIE_ACCESS);
        deleteCookie(request, response, COOKIE_PAGING);
        deleteCookie(request, response, COOKIE_REFRESH);
    }
}
