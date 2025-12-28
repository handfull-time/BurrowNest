package com.utime.burrowNest.dropbox.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.dropbox.core.v2.files.FileMetadata;

@Mapper
public interface DropboxMapper {
	/**
	 * 드롭박스 메타데이터 테이블 생성
	 * 
	 * @return 생성된 행 수
	 */
	public int createMetadataTable();
	
	/**
	 * 드롭박스 메타데이터 삽입
	 * 
	 * @param user     사용자 정보
	 * @param metadata 드롭박스 메타데이터
	 * @return 삽입된 행 수
	 * @throws Exception 예외 발생 시
	 */
	public int insertMetadata(@Param("userNo") long userNo, @Param("data") FileMetadata metadata) throws Exception;
	
	/**
	 * 드롭박스 메타데이터 REV 조회
	 * 
	 * @param user     사용자 정보
	 * @param metadata 드롭박스 메타데이터
	 * @return id 해당 되는 rev 조회
	 */
	public String selectMetadataRev(@Param("userNo") long userNo, @Param("data") FileMetadata metadata);
	
	/**
	 * 드롭박스 메타데이터 REV 수정
	 * 
	 * @param user     사용자 정보
	 * @param metadata 드롭박스 메타데이터
	 * @return 수정된 행 수
	 */
	public int updateMetadataRev(@Param("userNo") long userNo, @Param("data") FileMetadata metadata);
}
