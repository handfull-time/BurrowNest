package com.utime.burrowNest.dropbox.service;

import java.io.IOException;
import java.util.List;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.Metadata;
import com.utime.burrowNest.common.vo.ReturnBasic;
import com.utime.burrowNest.dropbox.vo.DropboxContextConfig;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Dropbox OAuth 서비스 인터페이스
 */
public interface DropboxOAuthService {

	/**
	 * 멤버별 authorize URL 생성
	 */
	String buildAuthorizeUrl(HttpServletRequest request, String id, String returnUrl, boolean popup)throws IOException;

	/**
	 * state 문자열을 파싱하여 DropboxContextConfig 객체 생성
	 */
	DropboxContextConfig parseContextConfig(String state);
	
	/**
	 * code를 토큰으로 교환하고 멤버에 저장
	 */
	void exchangeCodeAndSave(String userId, String code)throws IOException;

	/**
	 * 갱신
	 * @param userId
	 * @return
	 */
	ReturnBasic refreshMember(String userId);
	
	/**
	 * 드롭박스 연결 해제
	 * @param userId
	 */
	public ReturnBasic unlinkDropbox(String userId);
	
	/**
	 * 전체 목록 조회
	 * @param userId
	 * @return
	 * @throws DbxException
	 */
	public List<Metadata> getAllList(String userId) throws DbxException;
	
	/**
	 * 드롭박스 설정 저장
	 * @param clientId
	 * @param secret
	 * @param redirectUrl
	 * @return
	 */
	public ReturnBasic SaveConfig(String clientId, String secret, String redirectUrl );

}
