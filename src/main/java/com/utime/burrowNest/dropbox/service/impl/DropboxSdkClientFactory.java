package com.utime.burrowNest.dropbox.service.impl;

import java.time.Instant;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.oauth.DbxOAuthException;
import com.dropbox.core.oauth.DbxRefreshResult;
import com.dropbox.core.v2.DbxClientV2;
import com.utime.burrowNest.user.dao.UserDao;
import com.utime.burrowNest.user.vo.UserVo;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DropboxSdkClientFactory {

    @Value("${dropbox.client-id}")
    private String appKey;

    @Value("${dropbox.client-secret}")
    private String appSecret;

    private final UserDao userDao;

    private final DbxRequestConfig config =
            DbxRequestConfig.newBuilder("BurrowNest/1.0")
                    .withUserLocale(Locale.KOREA.toLanguageTag())
                    .build();


    /** 사용자 토큰 상태로 DbxClientV2 생성 (필요 시 refresh + DB 반영) */
    public DbxClientV2 getClient(UserVo user) {
        DbxCredential cred = new DbxCredential(
                user.getAccessToken(),
                user.getExpiresAt() == null ? null : user.getExpiresAt().toEpochMilli(),
                user.getRefreshToken(),
                appKey,
                appSecret
        );

        // access token이 없거나(초기/정리됨), 만료 임박이면 refresh 시도
        if (cred.getAccessToken() == null || cred.aboutToExpire()) { // aboutToExpire: 만료 임박/만료 :contentReference[oaicite:5]{index=5}
            this.refreshAndPersist(user, cred);
        }

        return new DbxClientV2(config, cred); // DbxCredential 기반 클라이언트 :contentReference[oaicite:6]{index=6}
    }

    private void refreshAndPersist(UserVo user, DbxCredential cred) {
        try {
            DbxRefreshResult result = cred.refresh(config); // SDK refresh :contentReference[oaicite:7]{index=7}

            // cred 내부도 갱신되지만, 우리 DB(UserVo)도 동기화
            user.setAccessToken(result.getAccessToken());
            user.setExpiresAt(Instant.ofEpochMilli(result.getExpiresAt()));
            
            userDao.saveDropboxToken(user);

        } catch (DbxOAuthException e) {
            // ✅ 여기서 “재인증 필요”를 분기
            // refresh_token 무효/철회/권한 변경 등으로 invalid_grant가 나는 케이스가 대표적입니다. :contentReference[oaicite:8]{index=8}

            // 운영/화면이 명확히 알 수 있게 토큰 정리 + 상태 저장(추천)
            user.setAccessToken(null);
            user.setRefreshToken(null);
            user.setExpiresAt(null);
            try {
            	userDao.saveDropboxToken(user);
			} catch (Exception e1) {
				e1.printStackTrace();
			}

            throw new ReauthRequiredException("Dropbox 재연결 필요(토큰 갱신 실패): " + e.getMessage(), e);
        } catch (Exception e) {
            // 일시적 장애는 토큰 정리하지 말고 에러만 올리는 게 안전
            throw new IllegalStateException("Dropbox token refresh failed: " + e.getMessage(), e);
        }
    }

    public static class ReauthRequiredException extends RuntimeException {
        public ReauthRequiredException(String msg, Throwable t) { super(msg, t); }
    }
}
