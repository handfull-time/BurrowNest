package com.utime.burrowNest.dropbox.service;

import java.io.IOException;

/**
 * Dropbox OAuth 서비스 인터페이스
 */
public interface DropboxOAuthService {

	/**
	 * 멤버별 authorize URL 생성
	 */
	String buildAuthorizeUrl(String id);

	/**
	 * code를 토큰으로 교환하고 멤버에 저장
	 */
	void exchangeCodeAndSave(String state, String code)throws IOException;

}
