package com.utime.burrowNest.storage.dao;

import java.io.File;

import com.utime.burrowNest.storage.vo.BnDirectory;
import com.utime.burrowNest.storage.vo.BnFile;
import com.utime.burrowNest.storage.vo.BnFileArchive;
import com.utime.burrowNest.storage.vo.BnFileAudio;
import com.utime.burrowNest.storage.vo.BnFileDocument;
import com.utime.burrowNest.storage.vo.BnFileImage;
import com.utime.burrowNest.storage.vo.BnFileVideo;

public interface StorageDao {

	/**
	 * Dir 추가
	 * @param dir
	 * @return
	 */
	BnDirectory insertDirectory(File dir) throws Exception;
	
	/**
	 * File 추가
	 * @param file
	 * @return
	 * @throws Exception
	 */
	BnFile insertFile(File file) throws Exception;
	
	/**
	 * 문서 파일 추가.
	 * @param file
	 * @return
	 * @throws Exception
	 */
	int insertFileDocument( BnFileDocument file )throws Exception;
	
	/**
	 * 이미지 파일 추가
	 * @param file
	 * @return
	 * @throws Exception
	 */
	int insertFileImage( BnFileImage file )throws Exception;
	
	/**
	 * 영상 파일 추가
	 * @param file
	 * @return
	 * @throws Exception
	 */
	int insertFileVideo( BnFileVideo file )throws Exception;
	
	/**
	 * 소리 파일 추가
	 * @param file
	 * @return
	 * @throws Exception
	 */
	int insertFileAudio( BnFileAudio file )throws Exception;
	
	/**
	 * 압축 파일 추가
	 * @param file
	 * @return
	 * @throws Exception
	 */
	int insertFileArchive( BnFileArchive file )throws Exception;
}
