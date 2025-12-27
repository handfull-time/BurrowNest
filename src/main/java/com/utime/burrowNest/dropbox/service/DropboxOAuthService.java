package com.utime.burrowNest.dropbox.service;

import java.io.IOException;
import java.util.List;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.Metadata;
import com.utime.burrowNest.common.vo.ReturnBasic;

/**
 * Dropbox OAuth 서비스 인터페이스
 */
public interface DropboxOAuthService {

	/**
	 * 멤버별 authorize URL 생성
	 */
	String buildAuthorizeUrl(String id)throws IOException;

	/**
	 * code를 토큰으로 교환하고 멤버에 저장
	 */
	void exchangeCodeAndSave(String state, String code)throws IOException;

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
	
	public List<Metadata> listAll(String userId, String path) throws DbxException;

}
