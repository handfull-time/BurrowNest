package com.utime.burrowNest.dropbox.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.utime.burrowNest.dropbox.service.DropboxOAuthService;
import com.utime.burrowNest.user.dao.UserDao;
import com.utime.burrowNest.user.vo.UserVo;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Dropbox OAuth 서비스 구현체
 * @link https://www.dropbox.com/oauth2/authorize
 * @link https://www.dropbox.com/developers/apps?_tk=pilot_lp&_ad=topbar4&_camp=myapps
 * 
 * # 1. 편집기 열기. Bash 사용 시: ~/.bashrc (또는 ~/.bash_profile)
nano ~/.bashrc

# 2. 맨 아래에 변수 추가
export DROPBOX_APP_KEY="8t2l5z89fbw2a30"
export DROPBOX_APP_SECRET="YOUR_SECRET_VALUE"

# 3. 저장 후 적용 (재로그인 하거나 아래 명령어 실행)
source ~/.bashrc
 */
@Slf4j
@Service
@RequiredArgsConstructor
class DropboxOAuthServiceImpl implements DropboxOAuthService {


    @Value("${dropbox.client-id}")
    private String clientId;

    @Value("${dropbox.client-secret}")
    private String clientSecret;

    @Value("${dropbox.redirect-uri}")
    private String redirectUri;

    private final UserDao repo;
    private final OAuthStateStore stateStore = new OAuthStateStore();
    
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    private final Map<String, ScheduledFuture<?>> scheduled = new ConcurrentHashMap<>();


    // 서버 시작 시 1회만: 기존 멤버들 예약 복구(심플)
    @PostConstruct
    public void init() {
        for (UserVo m : repo.findAllByExpiresAtIsNotNull()) {
            this.scheduleRefresh(m);
        }
    }

    // 외부에서 호출: 특정 멤버의 refresh 예약(기존 예약은 교체)
	public void scheduleRefresh(UserVo user) {
		final String userId = user.getId();
        this.cancel(userId);

        final long delayMs = computeDelayMs(user.getExpiresAt(), 120); // 만료 2분 전
        ScheduledFuture<?> f = executor.schedule(() -> this.runRefresh(user), delayMs, TimeUnit.MILLISECONDS);
        scheduled.put(userId, f);
    }

    public void cancel(String userId) {
        final ScheduledFuture<?> prev = scheduled.remove(userId);
        if (prev != null) 
        	prev.cancel(false);
    }

    private void runRefresh(UserVo user) {
        try {
            // 만료 임박이면 refresh
            this.refreshAccessTokenIfNeeded(user);

            if (user.getRefreshToken() != null && user.getExpiresAt() != null) {
                scheduleRefresh(user);
            }
        } catch (Exception e) {
            // refresh 실패 → 예약 중단하고 UI에서 "재연결 필요" 안내하는 게 안전
            // (원하면 needsReconnect 플래그를 DB에 저장)
            System.out.println("[DropboxRefreshFail] memberId=" + user.getId() + " err=" + e.getMessage());
            cancel(user.getId());
        }
    }

    private static long computeDelayMs(Instant expiresAt, int beforeSeconds) {
        Instant target = expiresAt.minusSeconds(beforeSeconds);
        long ms = Duration.between(Instant.now(), target).toMillis();
        // 이미 지났으면 즉시 실행(최소 0ms)
        return Math.max(ms, 0);
    }

    // 1) 멤버별 authorize URL 생성
    public String buildAuthorizeUrl(String memberId) {
    	// state 발급 및 저장
        final String state = stateStore.issueState(memberId);

        return UriComponentsBuilder
                .fromUriString("https://www.dropbox.com/oauth2/authorize")
                .queryParam("client_id", clientId)
                .queryParam("response_type", "code")
                .queryParam("redirect_uri", redirectUri)
                // refresh token까지 받기(offline access)
                .queryParam("token_access_type", "offline")
                .queryParam("state", state)
                .build(true)
                .toUriString();
    }

    // 2) callback에서 code 받아 token 교환
    @Override
    public void exchangeCodeAndSave(String state, String code) throws IOException {
    	
//    	final String userId = stateStore.consumeState(state);
//		if (userId == null) {
//			throw new IllegalArgumentException("Invalid member ID: " + userId);
//		}
    	final String userId = "admin";
		
    	final UserVo member = repo.getUserFormId(userId);
    	if( member == null ) {
			throw new IllegalArgumentException("Invalid member ID: " + userId);
		}

    	final String body = "code=" + url(code)
                + "&grant_type=authorization_code"
                + "&client_id=" + url(clientId)
                + "&client_secret=" + url(clientSecret)
                + "&redirect_uri=" + url(redirectUri);

//        final URL url = new URL("https://api.dropboxapi.com/oauth2/token");
//        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        conn.setRequestMethod("POST");
//        conn.setDoOutput(true);
//        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//
//        try (OutputStream os = conn.getOutputStream()) {
//            os.write(body.getBytes(StandardCharsets.UTF_8));
//        }
    	
    	
    	final URL url = new URL("https://api.dropboxapi.com/oauth2/token");
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        conn.setDoOutput(true);

    	final OutputStream os = conn.getOutputStream();
    	
    	final byte[] input = body.getBytes("UTF-8");
        os.write(input, 0, input.length);
        os.flush();

        log.info("Response Code: " + conn.getResponseCode());
        

        final int codeHttp = conn.getResponseCode();
        log.info("Dropbox token exchange HTTP code: {}", codeHttp);
        final String json = readAll((codeHttp >= 200 && codeHttp < 300) ? conn.getInputStream() : conn.getErrorStream());
        log.info("Dropbox token exchange response: {}", json);
        
        // 아주 심플 파싱(실무에선 Jackson 권장)
        final String accessToken = pickJson(json, "access_token");
        final String refreshToken = pickJson(json, "refresh_token");
        final String expiresIn = pickJson(json, "expires_in");

        member.setAccessToken(accessToken);
        if (refreshToken != null && !refreshToken.isEmpty()) {
            member.setRefreshToken(refreshToken);
        }
        if (expiresIn != null && !expiresIn.isEmpty()) {
            try {
                long sec = Long.parseLong(expiresIn);
                member.setExpiresAt(Instant.now().plusSeconds(sec));
            } catch (Exception ignored) {}
        }

        try {
			repo.saveDropboxToken(member);
		} catch (Exception e) {
			log.error("Failed to save Dropbox token for member ID: {}", userId, e);
		}
    }
    
    private void refreshAccessTokenIfNeeded(UserVo member) throws IOException {

        // refresh_token 없으면 자동갱신 불가 → 재연결 필요
        if (member.getRefreshToken() == null || member.getRefreshToken().trim().isEmpty()) {
            throw new IllegalStateException("refresh_token is missing. user must reconnect.");
        }

        // 만료 정보가 없으면, 안전하게 갱신하지 않고 그냥 사용(또는 바로 refresh 하도록 정책 선택 가능)
        if (member.getExpiresAt() != null) {
            // 만료 2분 전이면 갱신 (버퍼)
            Instant now = Instant.now();
            if (member.getExpiresAt().isAfter(now.plusSeconds(120))) {
                return; // 아직 충분히 유효
            }
        }

        // 실제 갱신
        this.refreshAccessToken(member);
    }

    /**
     * Dropbox access token 갱신
     * @param member
     * @throws IOException
     */
    private void refreshAccessToken(UserVo member) throws IOException {
        String body = "grant_type=refresh_token"
                + "&refresh_token=" + url(member.getRefreshToken())
                + "&client_id=" + url(clientId)
                + "&client_secret=" + url(clientSecret);

        URL url = new URL("https://api.dropboxapi.com/oauth2/token");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }

        int codeHttp = conn.getResponseCode();
        String json = readAll((codeHttp >= 200 && codeHttp < 300) ? conn.getInputStream() : conn.getErrorStream());

        if (codeHttp < 200 || codeHttp >= 300) {
            // 예: refresh_token 폐기/회수/앱 연결 해제 등
            throw new IllegalStateException("refresh failed: " + json);
        }

        String newAccessToken = pickJson(json, "access_token");
        String expiresIn = pickJson(json, "expires_in"); // 초 단위일 때가 많음

        if (newAccessToken == null || newAccessToken.trim().isEmpty()) {
            throw new IllegalStateException("refresh response missing access_token: " + json);
        }

        member.setAccessToken(newAccessToken);

        if (expiresIn != null && !expiresIn.isEmpty()) {
            try {
                long sec = Long.parseLong(expiresIn);
                member.setExpiresAt(Instant.now().plusSeconds(sec));
            } catch (Exception ignored) {
                // expires_in 파싱 실패하면 expiresAt은 유지/비움(정책 선택)
            }
        }

        try {
        	repo.saveDropboxToken(member);
        	this.scheduleRefresh(member);
		} catch (Exception e) {
			log.error("Failed to save refreshed Dropbox token for member ID: {}", member.getId(), e);
		}
    }


    private static String url(String v) throws UnsupportedEncodingException {
        return URLEncoder.encode(v, "UTF-8");
    }

    private static String readAll(InputStream is) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            return sb.toString();
        }
    }

    // 매우 단순 JSON 값 추출 (데모용)
    private static String pickJson(String json, String key) {
        String pat = "\"" + key + "\":";
        int i = json.indexOf(pat);
        if (i < 0) return null;
        int start = i + pat.length();
        while (start < json.length() && (json.charAt(start) == ' ')) start++;
        if (start < json.length() && json.charAt(start) == '"') {
            int s = start + 1;
            int e = json.indexOf('"', s);
            return e > s ? json.substring(s, e) : null;
        } else {
            int e = start;
            while (e < json.length() && "0123456789".indexOf(json.charAt(e)) >= 0) e++;
            return json.substring(start, e);
        }
    }

    // 간단 state 저장(메모리). 데모용이라 서버 재시작하면 날아갑니다.
    static class OAuthStateStore {
        private final java.util.Map<String, String> map = new java.util.concurrent.ConcurrentHashMap<>();

        String issueState(String memberId) {
            final String state = UUID.randomUUID().toString();
            map.put(state, memberId);
            return state;
        }

        String consumeState(String state) {
            return map.remove(state); // 1회성
        }
    }
}
