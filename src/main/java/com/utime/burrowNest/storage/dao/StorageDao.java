package com.utime.burrowNest.storage.dao;

import java.util.Map;

import com.utime.burrowNest.storage.vo.AbsBnFileInfo;
import com.utime.burrowNest.storage.vo.BnDirectory;
import com.utime.burrowNest.storage.vo.BnFile;
import com.utime.burrowNest.storage.vo.EBnFileType;

public interface StorageDao {
	
	/**
	 * 확장자 별 파일 종류
	 * @return
	 */
	Map<String, EBnFileType> getBnFileType();

	/**
	 * Dir 저장
	 * @param dir
	 * @return
	 */
	int saveDirectory(BnDirectory dir) throws Exception;
	
	/**
	 * File 저장
	 * @param file
	 * @return
	 * @throws Exception
	 */
	int saveFile(BnFile file) throws Exception;
	
	/**
	 * 파일 확장 저장
	 * @param file
	 * @return
	 * @throws Exception
	 */
	int saveFileInfor( AbsBnFileInfo file )throws Exception;
	
	/**
	 * 이미지 섬네일 저장
	 * @param file
	 * @param binary
	 * @return
	 */
	int saveThumbnail( BnFile file, String base64);
	
	/**
	 * Dierctory 삭제
	 * @param dir
	 * @return
	 */
	int deleteDirectory( BnDirectory dir )throws Exception;
	
	/**
	 * 파일 삭제
	 * @param file
	 * @return
	 */
	int deleteFile( BnFile file )throws Exception;
	
	/**
	 * directory 정보 조회
	 * @param dirNo
	 * @return
	 */
	BnDirectory getDirectory( long dirNo );
	
	/**
	 *  정보 조회
	 * @param fileNo
	 * @return
	 */
	BnFile getFile( long fileNo );
	
	/**
	 * 섬네일
	 * @param fileNo
	 * @return base64 Encode String
	 */
	String getFileThumbnail( long fileNo );
	
	/**
	 * 파일 확장 조회
	 * @param file
	 * @return
	 */
	AbsBnFileInfo getFileInfor( BnFile file );
	
}
