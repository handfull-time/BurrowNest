package com.utime.burrowNest.storage.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StorageMapper {
	/**
	 * 디렉터리 테이블 생성 
	 * @return
	 */
	int CreateDirectory();
	/**
	 * 파일 테이블 생성 
	 * @return
	 */
	int CreateFile();
	/**
	 * 문서 파일 테이블 생성 
	 * @return
	 */
	int CreateFileDocument();
	/**
	 * 이미지 파일 테이블 생성 
	 * @return
	 */
	int CreateFileImage();
	/**
	 * 비디오 파일 테이블 생성 
	 * @return
	 */
	int CreateFileVideo();
	/**
	 * 오디오 파일 테이블 생성 
	 * @return
	 */
	int CreateFileAudio();
	/**
	 * 압축 파일 테이블 생성 
	 * @return
	 */
	int CreateFileArchive();
}