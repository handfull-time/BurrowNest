package com.utime.burrowNest.storage.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.utime.burrowNest.storage.vo.BnDirectory;
import com.utime.burrowNest.storage.vo.BnFileExtension;
import com.utime.burrowNest.storage.vo.EBnFileType;

/**
 * Storage 기본 동작에 필요한 mapper
 */
@Mapper
public interface StorageBasicMapper {
	/**
	 * 디렉터리 테이블 생성 
	 * @return
	 */
	int CreateDirectory();
	
	/**
	 * 루트 생성
	 */
	int InsertRootDirectory(BnDirectory dir);
	
	/**
	 * dir 접근 권한 테이블 생성
	 * @return
	 */
	int CreateDirectoryAccess();
	
	/**
	 * 파일 테이블 생성 
	 * @return
	 */
	int CreateFile();
	/**
	 * 파일 접근 권한 테이블 생성
	 * @return
	 */
	int CreateFileAccess();
	/**
	 * 파일 확장자 종류
	 * @return
	 */
	int CreateFileExtension();
	/**
	 * 거부 파일 확장자 종류
	 * @return
	 */
	int CreateDeniedFileExtension();
	/**
	 * 섬네일 테이블 생성
	 * @return
	 */
	int CreateFileThumbnail();
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
	
	/**
	 * 파일 확장자 종류 추가
	 * @param fileExt
	 * @return
	 */
	int insertFileExtension( @Param("extension") String extension, @Param("fileType") EBnFileType fileType);
	
	/**
	 * 파일 확장자 종류 전체 조회
	 * @return
	 */
	List<BnFileExtension> selectFileExtensionAll();
	/**
	 * 파일 확장자 종류 조회
	 * @param ext
	 * @return
	 */
	BnFileExtension selectFileExtensionByExt( String ext );
	/**
	 * 파일 확장자 종류 수정
	 * @param fileExt
	 * @return
	 */
	int updateFileExtension( BnFileExtension fileExt);
	/**
	 * 파일 확장자 종류 삭제
	 * @param fileExt
	 * @return
	 */
	int deleteFileExtension( BnFileExtension fileExt);
	
	/**
	 * 거부 확장자 추가
	 * @param extension
	 * @param memo
	 * @return
	 */
	int insertDeniedFileExtension( @Param("extension") String extension, @Param("memo") String memo);
	
	/**
	 * 거부 확장자 조회
	 * @return
	 */
	List<String> selectDeniedFileExtension();
	
	/**
	 * 거부 확장자 조회
	 * @param extension
	 * @return
	 */
	int deleteDeniedFileExtension( @Param("extension") String extension);
	
}