package com.utime.burrowNest.dropbox.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import com.dropbox.core.DbxException;
import com.dropbox.core.TokenAccessType;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DeleteErrorException;
import com.dropbox.core.v2.files.DeleteResult;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.teamlog.SfTeamGrantAccessType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utime.burrowNest.common.util.BurrowUtils;
import com.utime.burrowNest.common.vo.BurrowDefine;
import com.utime.burrowNest.common.vo.ReturnBasic;
import com.utime.burrowNest.dropbox.service.DropboxOAuthService;
import com.utime.burrowNest.dropbox.vo.DropboxTokenVO;
import com.utime.burrowNest.user.dao.UserDao;
import com.utime.burrowNest.user.vo.UserVo;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
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

	private final DropboxSdkClientFactory dropboxClientFactory;

    @Value("${dropbox.client-id}")
    private String appKey;

    @Value("${dropbox.client-secret}")
    private String appSecret;
    
    @Value("${dropbox.redirect-uri}")
    private String redirectUri;
    
    private final ObjectMapper objectMapper;

    private final UserDao userDao;
    
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    private final Map<String, ScheduledFuture<?>> scheduled = new ConcurrentHashMap<>();


    // 서버 시작 시 1회만: 기존 멤버들 예약 복구(심플)
    @PostConstruct
    private void init() {
        for (UserVo m : userDao.findAllByExpiresAtIsNotNull()) {
            this.scheduleRefresh(m);
        }
    }
    
    @PreDestroy
    private void destroy() {
    	BurrowUtils.shutdownAndAwait( this.executor, 5, TimeUnit.SECONDS );
    }
    
    private DbxClientV2 getDbxClient(String userId) {
        UserVo user = userDao.getUserFormId(userId);
        if (user == null) {
            throw new IllegalArgumentException("Invalid userId: " + userId);
        }

        // ✅ 여기서 refresh + DB 저장까지 자동 처리됨
        return this.getDbxClient(user);
    }
    
    private DbxClientV2 getDbxClient(UserVo user) {
        return dropboxClientFactory.getClient(user);
    }

    // 외부에서 호출: 특정 멤버의 refresh 예약(기존 예약은 교체)
	private void scheduleRefresh(UserVo user) {
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
            System.out.println("[DropboxRefreshFail] userId=" + user.getId() + " err=" + e.getMessage());
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
    public String buildAuthorizeUrl(String userId) throws IOException {
    	// state 발급 및 저장
    	UserVo user = userDao.getUserFormId(userId);
    	if( user == null ) {
    		throw new IllegalArgumentException("Invalid member ID: " + userId);
    	}
    	
        final String state = String.valueOf(user.getUserNo());

        return UriComponentsBuilder
                .fromUriString("https://www.dropbox.com/oauth2/authorize")
                .queryParam("client_id", appKey)
                .queryParam("response_type", "code")
                .queryParam("redirect_uri", redirectUri)
                // refresh token까지 받기(offline access)
                .queryParam("token_access_type", TokenAccessType.OFFLINE.toString())
                .queryParam("state", state)
                .build(true)
                .toUriString();
    }
    
    // 2) callback에서 code 받아 token 교환
    @Override
    public void exchangeCodeAndSave(String state, String code) throws IOException {
    	
    	final UserVo user = userDao.getUserFormUserNo(Long.valueOf(state));
    	if( user == null ) {
			throw new IllegalArgumentException("Invalid user ID: " + state);
		}

    	final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", appKey);
        params.add("redirect_uri", redirectUri);
        params.add("client_secret", appSecret);
        params.add("grant_type", "authorization_code");

        final String body = UriComponentsBuilder
                .newInstance()
                .queryParams(params)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUriString()
                .substring(1); // '?' 제거

    	final URL url = new URL("https://api.dropboxapi.com/oauth2/token");
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        conn.setDoOutput(true);

    	final OutputStream os = conn.getOutputStream();
    	
    	final byte[] input = body.getBytes(StandardCharsets.UTF_8);
        os.write(input, 0, input.length);
        os.flush();

        final int codeHttp = conn.getResponseCode();
        log.info("Dropbox token exchange HTTP code: {}", codeHttp);
        final String json = readAll((codeHttp >= 200 && codeHttp < 300) ? conn.getInputStream() : conn.getErrorStream());
        log.info("Dropbox token exchange response: {}", json);
        
        final DropboxTokenVO tokenVo = objectMapper.readValue(json, DropboxTokenVO.class);
        if( tokenVo.getError() != null ) {
			throw new IllegalStateException("Dropbox token exchange failed: " + json);
		}

        user.setAccessToken(tokenVo.getAccessToken());
        if ( ! BurrowUtils.isEmpty(tokenVo.getRefreshToken() )) {
            user.setRefreshToken(tokenVo.getRefreshToken());
        }
        user.setExpiresAt(Instant.now().plusSeconds(tokenVo.getExpiresIn()));
        
        try {
			userDao.saveDropboxToken(user);
		} catch (Exception e) {
			log.error("Failed to save Dropbox token for member ID: {}", user.getId(), e);
		}
    }
    
    @Override
    public ReturnBasic refreshMember(String userId) {
    	final UserVo user = userDao.getUserFormId(userId);
    	if( user == null ) {
			throw new IllegalArgumentException("Invalid member ID: " + userId);
		}

    	DbxClientV2 client = getDbxClient(user);
    	if( client == null ) {
    		return new ReturnBasic("E", "");
    	}
    	//return this.refreshAccessTokenIfNeeded(user);
    	return new ReturnBasic();
    }

    /**
	 * 필요 시 Dropbox access token 갱신
	 * @param user
	 */
    private ReturnBasic refreshAccessTokenIfNeeded(UserVo user) {

        if (user.getRefreshToken() == null || user.getRefreshToken().isEmpty()) {
            // refresh_token 자체가 없으면 이미 재연결 상태
            return new ReturnBasic("REAUTH", "Dropbox 재연결 필요 (refresh_token 없음)");
        }

        if (user.getExpiresAt() != null) {
            Instant now = Instant.now();
            if (user.getExpiresAt().isAfter(now.plusSeconds(120))) {
                long secondsLeft = Duration.between(now, user.getExpiresAt()).getSeconds();
                log.info("Dropbox access token still valid for member ID: {}, seconds left: {}", user.getId(), secondsLeft);
                return new ReturnBasic(BurrowDefine.ERROR_OK, String.format("아직 충분히 유효(%,d초 남음)", secondsLeft));
            }
        }

        try {
            return this.refreshAccessToken(user);
        } catch (ReauthRequiredException e) {
            // ✅ 재연결 필요를 명확히
            return new ReturnBasic("REAUTH", e.getMessage());
        } catch (IOException e) {
            log.error("Failed to refresh Dropbox access token for member ID: {}", user.getId(), e);
            return new ReturnBasic("E", "Failed to refresh access token: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("Refresh runtime error for member ID: {}", user.getId(), e);
            return new ReturnBasic("E", "Refresh failed: " + e.getMessage());
        }
    }


    /**
     * Dropbox access token 갱신
     * @param user
     * @throws IOException
     */
    private ReturnBasic refreshAccessToken(UserVo user) throws IOException {
    	
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", appKey);
        params.add("grant_type", "refresh_token");
        params.add("client_secret", appSecret);
        params.add("refresh_token", user.getRefreshToken());

        final String body = UriComponentsBuilder
                .newInstance()
                .queryParams(params)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUriString()
                .substring(1); // '?' 제거

        
    	final URL url = new URL("https://api.dropboxapi.com/oauth2/token");
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        conn.setDoOutput(true);
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);

    	final OutputStream os = conn.getOutputStream();
    	
    	final byte[] input = body.getBytes(StandardCharsets.UTF_8);
        os.write(input, 0, input.length);
        os.flush();

        final int codeHttp = conn.getResponseCode();
        final String json = readAll((codeHttp >= 200 && codeHttp < 300) ? conn.getInputStream() : conn.getErrorStream());
        log.info("Dropbox refresh HTTP code: {}", codeHttp);
        log.info("Dropbox refresh response: {}", json);

        if (codeHttp < 200 || codeHttp >= 300) {
            // ✅ 재인증 필요 케이스를 구분
            if (isReauthRequired(codeHttp, json)) {
                markReconnectRequired(user, "refresh_token invalid/revoked: " + json);
                throw new ReauthRequiredException("Dropbox 재연결 필요 (refresh_token 무효)");
            }
            // ✅ 일시 오류는 그대로 예외
            throw new IllegalStateException("refresh failed: " + json);
        }

        final DropboxTokenVO tokenVo = objectMapper.readValue(json, DropboxTokenVO.class);
        if (tokenVo.getError() != null) {
            // 성공(200)인데도 error가 있다면 방어
            throw new IllegalStateException("refresh response error: " + json);
        }

        final String newAccessToken = tokenVo.getAccessToken();
        if (BurrowUtils.isEmpty(newAccessToken)) {
            throw new IllegalStateException("refresh response missing access_token: " + json);
        }

        user.setAccessToken(newAccessToken);
        user.setExpiresAt(Instant.now().plusSeconds(tokenVo.getExpiresIn()));

        try {
            userDao.saveDropboxToken(user);
            this.scheduleRefresh(user);
        } catch (Exception e) {
            log.error("Failed to save refreshed Dropbox token for member ID: {}", user.getId(), e);
        }

        return new ReturnBasic();
    }
    
    /**
     * Dropbox 연결 해제(로그아웃)
     * - 서버에서 토큰을 revoke 해서 즉시 무효화
     * - 로컬(DB)에 저장된 access/refresh/expiresAt도 제거
     *
     * 주의: tokenRevoke()는 "그 호출에 사용한 토큰"을 무효화합니다. :contentReference[oaicite:2]{index=2}
     */
    @Override
    public ReturnBasic unlinkDropbox(String userId) {
    	UserVo user = userDao.getUserFormId(userId);
        if (user == null) throw new IllegalArgumentException("Invalid userId: " + userId);

        // 1) 서버 토큰 revoke (가능하면)
        try {
            if (user.getAccessToken() != null && !user.getAccessToken().isBlank()) {
                // Factory를 통해 클라이언트 생성 (필요 시 refresh 포함)
                DbxClientV2 client = dropboxClientFactory.getClient(user);

                // ✅ 서버에서 토큰 폐기
                client.auth().tokenRevoke();  // SDK revoke :contentReference[oaicite:3]{index=3}
            }
        } catch (DbxException e) {
            // 이미 무효화된 토큰이라면 여기서 에러가 날 수 있음
            // "연결 끊기" UX 관점에서는 토큰 삭제가 더 중요하므로 경고만 남기고 진행 권장
            log.warn("Dropbox tokenRevoke failed (will still clear local tokens). userId={}, msg={}", userId, e.getMessage());
        } catch (DropboxSdkClientFactory.ReauthRequiredException e) {
            // refresh token이 이미 무효인 케이스여도 로컬 토큰 삭제로 "연결 끊기"는 완료 처리
            log.warn("Dropbox reauth required while unlink (will still clear local tokens). userId={}", userId);
        }

        // 2) 로컬(DB) 토큰 제거
        user.setAccessToken(null);
        user.setRefreshToken(null);
        user.setExpiresAt(null);
        try {
			userDao.saveDropboxToken(user);
		} catch (Exception e) {
			e.printStackTrace();
		}

        // 3) 스케줄러/캐시/클라이언트 캐시가 있다면 여기서 정리
        this.cancel(userId);
        
        return new ReturnBasic();
    }



    private static String readAll(InputStream is) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            return sb.toString();
        }
    }
    
    private boolean isReauthRequired(int httpCode, String json) {
        // Dropbox token endpoint에서 refresh_token이 무효/철회되면 보통 400 + invalid_grant(또는 invalid_request류)로 옵니다.
        // (실제 에러 바디는 Map으로 파싱해서 보는 게 안전)
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> m = objectMapper.readValue(json, Map.class);

            String err = String.valueOf(m.getOrDefault("error", ""));
            String desc = String.valueOf(m.getOrDefault("error_description", ""));
            String summary = String.valueOf(m.getOrDefault("error_summary", ""));

            String all = (err + " " + desc + " " + summary).toLowerCase();

            // 실무에서 “재인증 필요”로 보는 패턴들
            if (all.contains("invalid_grant")) return true;
            if (all.contains("revoked")) return true;
            if (all.contains("expired") && all.contains("refresh")) return true;
            if (all.contains("invalid_refresh_token")) return true;

            // HTTP 코드만으로도 강하게 의심되는 경우(보수적으로)
            return httpCode == 400 && (all.contains("invalid") || all.contains("grant"));
        } catch (Exception ignore) {
            // 파싱 실패 시 문자열 기반으로 최소 판정
            String lower = (json == null ? "" : json.toLowerCase());
            return httpCode == 400 && (lower.contains("invalid_grant") || lower.contains("revoked"));
        }
    }

    private void markReconnectRequired(UserVo user, String reason) {
        // ✅ 스케줄 중단
        cancel(user.getId());

        // ✅ 토큰/만료정보 정리 (UI가 "연결 필요"로 판단하기 쉬움)
        user.setAccessToken(null);
        user.setRefreshToken(null);
        user.setExpiresAt(null);

        try {
            userDao.saveDropboxToken(user);
        } catch (Exception e) {
            log.error("Failed to mark reconnect required for user ID: {}", user.getId(), e);
        }

        log.warn("[DropboxReconnectRequired] userId={} reason={}", user.getId(), reason);
    }


    // ---------------------------
    // 1) 파일/폴더 목록 읽기
    // ---------------------------

    /**
     * 특정 경로 목록 조회 (페이징 전체 수집)
     *
     * @param userId 사용자
     * @param path ""(루트) 또는 "/some/folder"
     * @param recursive 재귀 여부
     */
    @Override
    public List<Metadata> listAll(String userId, String path) throws DbxException {
        DbxClientV2 client = this.getDbxClient(userId);

        String p = (path == null) ? "" : path;

        ListFolderResult res = client.files()
                .listFolderBuilder(p)
                .withRecursive(true)
                .withIncludeDeleted(false)
                .start();

        List<Metadata> all = new ArrayList<>(res.getEntries());

        while (res.getHasMore()) {
            res = client.files().listFolderContinue(res.getCursor());
            all.addAll(res.getEntries());
        }

        return all;
    }

    /**
     * 파일/폴더 다운로드 (폴더는 zip으로 저장)
     *
     * @param userId 사용자
     * @param dropboxPath 다운로드할 경로 (file 또는 folder)
     * @param localTargetDir 로컬 저장 디렉토리
     * @return 저장된 로컬 파일 경로
     */
    public Path download(String userId, String dropboxPath, Path localTargetDir) throws DbxException, IOException {
        DbxClientV2 client = this.getDbxClient(userId);

        if (dropboxPath == null || dropboxPath.isBlank()) {
            throw new IllegalArgumentException("dropboxPath is empty");
        }

        Files.createDirectories(localTargetDir);

        Metadata meta = client.files().getMetadata(dropboxPath);

        Path saved;
        if (meta instanceof FileMetadata fm) {
            String fileName = safeFileName(fm.getName());
            saved = localTargetDir.resolve(fileName);

            try (OutputStream os = Files.newOutputStream(saved, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                client.files().download(fm.getPathLower()).download(os);
            }

        } else if (meta instanceof FolderMetadata folder) {
            String folderName = safeFileName(folder.getName());
            // 폴더는 zip으로 받음
            saved = localTargetDir.resolve(folderName + ".zip");

            try (OutputStream os = Files.newOutputStream(saved, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                client.files().downloadZip(folder.getPathLower()).download(os);
            }
        } else {
            throw new IllegalStateException("Unsupported metadata type: " + meta.getClass());
        }

        return saved;
    }
    
    /**
     * 여러 개 다운로드
     */
    public List<Path> downloadMany(String userId, List<String> dropboxPaths, Path localTargetDir) {
        if (dropboxPaths == null || dropboxPaths.isEmpty()) return List.of();

        List<Path> saved = new ArrayList<>();
        for (String p : dropboxPaths) {
            try {
                saved.add(download(userId, p, localTargetDir));
            } catch (DropboxSdkClientFactory.ReauthRequiredException e) {
                // 재인증 필요는 즉시 중단(원하는 정책에 따라 다르게)
                throw e;
            } catch (Exception e) {
                log.error("download failed path={}", p, e);
                // 부분 실패 허용 정책: 계속 진행
            }
        }
        return saved;
    }

//    // ---------------------------
//    // 3) 다운로드 했던 것 모두 삭제 (단 "카메라 업로드" 폴더 제외)
//    // ---------------------------
//
//    /**
//     * 지금까지 "다운로드 성공 기록"된 경로들을 삭제한다.
//     * - 단, 카메라 업로드 폴더(및 하위)는 삭제하지 않는다.
//     * - 삭제 성공한 항목은 기록에서 제거
//     */
//    public DeleteResult deleteAllDownloadedExceptCameraUploads(String userId) throws DbxException {
//        DbxClientV2 client = this.getDbxClient(userId);
//
//        List<String> downloaded = userDao.listDownloadedPaths(userId);
//        if (downloaded == null || downloaded.isEmpty()) {
//            return new DeleteResult(0, 0, List.of());
//        }
//
//        int deletedCount = 0;
//        int skippedCount = 0;
//        List<String> failed = new ArrayList<>();
//
//        // ✅ 폴더가 먼저 오면 하위 파일들이 자동 삭제될 수 있으니,
//        // 깊이가 긴 경로(하위)부터 지우는 전략(충돌 줄임)
//        List<String> sorted = downloaded.stream()
//                .filter(Objects::nonNull)
//                .map(String::trim)
//                .filter(s -> !s.isEmpty())
//                .sorted(Comparator.comparingInt(String::length).reversed())
//                .collect(Collectors.toList());
//
//        // 삭제 성공한 것은 별도로 모아서 기록에서 삭제
//        List<String> deletedPaths = new ArrayList<>();
//
//        for (String path : sorted) {
//            if (isCameraUploadsPath(path)) {
//                skippedCount++;
//                continue;
//            }
//
//            try {
//                client.files().deleteV2(path);
//                deletedCount++;
//                deletedPaths.add(path);
//            } catch (DeleteErrorException e) {
//                // 이미 없어도(또는 경로 문제) 정책에 따라 성공 취급할지 결정 가능
//                log.warn("delete error path={} err={}", path, e.getMessage());
//                failed.add(path);
//            } catch (DbxException e) {
//                log.error("delete failed path={}", path, e);
//                failed.add(path);
//            }
//        }
//
//        // 성공분 기록에서 제거
//        if (!deletedPaths.isEmpty()) {
//            userDao.removeDownloadedPaths(userId, deletedPaths);
//        }
//
//        return new DeleteResult(deletedCount, skippedCount, failed);
//    }

    
    private List<Metadata> listFolderAllRecursive(DbxClientV2 client, String folderPathLower) throws DbxException {
        ListFolderResult res = client.files()
                .listFolderBuilder(folderPathLower)
                .withRecursive(true)
                .withIncludeDeleted(false)
                .start();

        List<Metadata> all = new ArrayList<>(res.getEntries());
        while (res.getHasMore()) {
            res = client.files().listFolderContinue(res.getCursor());
            all.addAll(res.getEntries());
        }
        return all;
    }

    /**
     * Dropbox의 full path를, 기준 폴더 아래의 상대경로로 변환해서 로컬에 매핑.
     * 예)
     * folderPathLower = "/a/b"
     * filePathLower   = "/a/b/c/d.txt"
     * => localFolder + "c/d.txt"
     */
    private Path mapToLocalPath(Path localFolder, String folderPathLower, String filePathLower) {
        String base = folderPathLower.endsWith("/") ? folderPathLower : folderPathLower + "/";
        String rel = filePathLower.startsWith(base)
                ? filePathLower.substring(base.length())
                : lastName(filePathLower); // 방어: 이상하면 파일명만

        // rel에는 "/"가 들어올 수 있으니 Path로 안전하게 처리
        Path p = localFolder;
        for (String part : rel.split("/")) {
            if (part.isBlank()) continue;
            p = p.resolve(safeFileName(part));
        }
        return p;
    }

    private String lastName(String pathLower) {
        if (pathLower == null || pathLower.isBlank()) return "folder";
        int idx = pathLower.lastIndexOf('/');
        if (idx < 0 || idx == pathLower.length() - 1) return pathLower.replace("/", "");
        return pathLower.substring(idx + 1);
    }


    // ---------------------------
    // 2) 파일/폴더 다운로드 후 바로 삭제
    // ---------------------------

    /**
     * 경로 하나를 다운로드 후 즉시 삭제한다.
     * - 파일: 그대로 다운로드 후 delete_v2
     * - 폴더: zip 다운로드 후 delete_v2
     * - 단, 카메라 업로드 "폴더 자체"는 삭제하지 않는다(그 안의 파일/폴더는 가능)
     *
     * @return 로컬에 저장된 파일 경로
     */
    public Path downloadAndDelete(String userId, String dropboxPath, Path localDir)
            throws DbxException, IOException {

        DbxClientV2 client = getDbxClient(userId);

        if (dropboxPath == null || dropboxPath.isBlank()) {
            throw new IllegalArgumentException("dropboxPath is empty");
        }
        Files.createDirectories(localDir);

        Metadata meta = client.files().getMetadata(dropboxPath);
        String pathLower = normPathLower(meta, dropboxPath);

        // ✅ 카메라 업로드 "폴더 엔트리" 자체는 스킵(다운로드/삭제 모두 하지 않음)
        if (isCameraUploadsFolderItself(meta, pathLower)) {
            throw new IllegalStateException("Camera Uploads 폴더 자체는 다운로드/삭제 대상이 아닙니다: " + pathLower);
        }

        Path saved;
        if (meta instanceof FileMetadata fm) {
            saved = downloadFile(client, fm, localDir);
        } else if (meta instanceof FolderMetadata folder) {
            saved = downloadFolderZip(client, folder, localDir);
        } else {
            throw new IllegalStateException("Unsupported metadata type: " + meta.getClass());
        }

        // ✅ 다운로드 성공 후 즉시 삭제
        safeDelete(client, pathLower);

        return saved;
    }
    
    private void deleteChildrenOnly(DbxClientV2 client, String folderLower) throws DbxException {
        // folderLower 하위 목록을 가져와서 (재귀) "하위 엔트리"만 삭제한다.
        // 폴더 자체는 삭제하지 않는다.
        ListFolderResult res = client.files()
                .listFolderBuilder(folderLower)
                .withRecursive(true)
                .withIncludeDeleted(false)
                .start();

        List<Metadata> all = new ArrayList<>(res.getEntries());
        while (res.getHasMore()) {
            res = client.files().listFolderContinue(res.getCursor());
            all.addAll(res.getEntries());
        }

        // ✅ 하위부터(깊이 큰 것부터) 삭제해야 충돌이 적음
        List<String> pathsToDelete = all.stream()
                .map(m -> normPathLower(m, null))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(String::length).reversed())
                .collect(Collectors.toList());

        for (String p : pathsToDelete) {
            // 혹시라도 카메라 업로드 폴더 엔트리가 하위에 또 있을 가능성은 낮지만 방어
            if (isCameraUploadsPath(p)) {
                // 하위에 같은 폴더가 또 있으면 그 폴더는 남기고 내부는 다음 재귀에서 처리됨
                continue;
            }
            safeDelete(client, p);
        }
    }

    private void safeDelete(DbxClientV2 client, String pathLower) throws DbxException {
        // 카메라 업로드 "폴더 엔트리"만 스킵 (파일들은 삭제 허용)
        if (isCameraUploadsPath(pathLower)) {
            log.info("Skip deleting Camera Uploads folder itself: {}", pathLower);
            return;
        }
        client.files().deleteV2(pathLower);
    }
    
    private Path downloadFile(DbxClientV2 client, FileMetadata fm, Path localDir) throws DbxException, IOException {
        String fileName = safeFileName(fm.getName());
        Path saved = localDir.resolve(fileName);

        try (OutputStream os = Files.newOutputStream(saved, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            client.files().download(fm.getPathLower()).download(os);
        }
        return saved;
    }

    private Path downloadFolderZip(DbxClientV2 client, FolderMetadata folder, Path localDir) throws DbxException, IOException {
        String folderName = safeFileName(folder.getName());
        Path saved = localDir.resolve(folderName + ".zip");

        try (OutputStream os = Files.newOutputStream(saved, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            client.files().downloadZip(folder.getPathLower()).download(os);
        }
        return saved;
    }

    // ---------------------------
    // 유틸
    // ---------------------------

    private String safeFileName(String name) {
        if (name == null) return "download";
        // 파일명 안전 처리(윈도우/리눅스 공통 대충)
        return name.replaceAll("[\\\\/:*?\"<>|]+", "_");
    }

    private String normalizeDropboxPath(Metadata meta) {
        // pathLower가 있으면 가장 안정적
        String p = meta.getPathLower();
        if (p != null && !p.isBlank()) return p;

        // fallback
        return meta.getName();
    }
    
    /**
	 * Metadata의 path_lower 우선, 없으면 fallback 소문자 경로
	 */
    private String normPathLower(Metadata meta, String fallback) {
        if (meta != null) {
            String p = meta.getPathLower();
            if (p != null && !p.isBlank()) return p;
        }
        if (fallback != null && !fallback.isBlank()) return fallback.trim().toLowerCase(Locale.ROOT);
        return null;
    }
    
    /**
     * "카메라 업로드 폴더 자체"인지 판단 (이 경우 폴더 엔트리는 삭제하지 않음)
     * - 파일은 삭제 가능해야 하므로, "폴더"인지도 함께 본다.
     */
    private boolean isCameraUploadsFolderItself(Metadata meta, String pathLower) {
        if (!(meta instanceof FolderMetadata)) return false;
        return isCameraUploadsPath(pathLower);
    }

    /**
     * "카메라 업로드" 폴더(및 하위)는 삭제 금지
     * - 계정 언어에 따라 표시명이 다를 수 있어 보수적으로 여러 패턴을 체크
     * - 가장 안전한 건: 최초 목록 조회에서 실제 카메라 업로드 폴더 path_lower를 찾아 DB에 저장해두고 그 값을 기준으로 체크하는 것
     */
    private boolean isCameraUploadsPath(String path) {
        if (path == null) return false;
        String p = path.trim().toLowerCase(Locale.ROOT);

        // 대표적인 폴더명 패턴들
        // (실제 운영에서는 "목록에서 실제 path_lower를 확인해서 그 값을 사용" 추천)
        return p.equals("/camera uploads")
                || p.startsWith("/camera uploads/")
                || p.equals("/카메라 업로드")
                || p.startsWith("/카메라 업로드/");
    }

    
    static class ReauthRequiredException extends RuntimeException {
    	
		private static final long serialVersionUID = 2023176227500332697L;

		ReauthRequiredException(String message) { super(message); }
    }

    
}
