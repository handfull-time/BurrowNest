package com.utime.burrowNest.dropbox.dao;

import com.dropbox.core.v2.files.FileMetadata;
import com.utime.burrowNest.user.vo.UserVo;

public interface DropboxDao {

	/**
	 * 드롭박스 메타데이터 삽입
	 * 
	 * @param user     사용자 정보
	 * @param metadata 드롭박스 메타데이터
	 * @return 삽입된 행 수
	 * @throws Exception 예외 발생 시
	 */
	public int addMetadata(UserVo user, FileMetadata metadata) throws Exception;
	
	/**
	 * 드롭박스 메타데이터 존재 여부 확인
	 * 
	 * @param user     사용자 정보
	 * @param metadata 드롭박스 메타데이터
	 * @return 존재 여부. true(존재), false(미존재)
	 */
	public String getMetadataRev(UserVo user, FileMetadata metadata);
	
	/**
	 * 드롭박스 메타데이터 REV 수정
	 * 
	 * @param user     사용자 정보
	 * @param metadata 드롭박스 메타데이터
	 * @return 수정된 행 수
	 */
	public int modifyMetadataRev(UserVo user, FileMetadata metadata) throws Exception;
}
