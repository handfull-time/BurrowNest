package com.utime.burrowNest.storage.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.utime.burrowNest.storage.vo.BnDirectory;
import com.utime.burrowNest.storage.vo.BnFile;
import com.utime.burrowNest.storage.vo.BnFileArchive;
import com.utime.burrowNest.storage.vo.BnFileAudio;
import com.utime.burrowNest.storage.vo.BnFileDocument;
import com.utime.burrowNest.storage.vo.BnFileImage;
import com.utime.burrowNest.storage.vo.BnFileVideo;

@Mapper
public interface StorageMapper {
	/**
	 * Dir 권한 추가하기
	 * @param dirNo
	 * @param userNo
	 * @param accType
	 * @return
	 */
	int InsertDirectoryAccess(@Param("dirNo") long dirNo, @Param("userNo") int userNo, @Param("accType") int accType);

	/**
	 * 파일 권한 추가하기
	 * @param dirNo
	 * @param userNo
	 * @param accType
	 * @return
	 */
	int InsertFileAccess(@Param("fileNo") long fileNo, @Param("userNo") int userNo, @Param("accType") int accType);

	/**
	 * dir 추가
	 * @param dir
	 * @return
	 */
	int insertBnDirectory( BnDirectory dir );
	
	/**
	 * dir 수정
	 * @param dir
	 * @return
	 */
	int updateBnDirectory( BnDirectory dir );
	
	/**
	 * dir 조회
	 * @param dirNo
	 * @return
	 */
	BnDirectory selectBnDirectoryByNo( long dirNo );
	
	/**
	 * 삭제
	 * @param dirNo
	 * @return
	 */
	int deleteBnDirectoryByNo( long dirNo );
	
	/**
	 * 파일 추가.
	 * @param file
	 * @return
	 */
	int insertBnFile( BnFile file );
	
	/**
	 * 파일 조회
	 * @param fileNo
	 * @return
	 */
	BnFile selectBnFileByNo( long fileNo );
	
	/**
	 * 파일 수정
	 * @param file
	 * @return
	 */
	int updateBnFile( BnFile file );
	
	/**
	 * 파일 삭제
	 * @param fileNo
	 * @return
	 */
	int deleteBnFileById( long fileNo );
	
	/**
	 * file doc 추가.
	 * @param document
	 * @return
	 */
	int insertBnFileDocument( BnFileDocument document );
	
	/**
	 * file doc 조회
	 * @param fileNo
	 * @return
	 */
	BnFileDocument selectBnFileDocumentByFileNo( long fileNo );
	
	/**
	 * 파일 doc 수정
	 * @param document
	 * @return
	 */
	int updateBnFileDocument( BnFileDocument document );
	
	/**
	 * 파일 이미지 추가
	 * @param image
	 * @return
	 */
	int insertBnFileImage( BnFileImage image );
	
	/**
	 * 파일 이미지 조회
	 * @param fileNo
	 * @return
	 */
	BnFileImage selectBnFileImageByFileNo( long fileNo );
	
	/**
	 * 파일 이미지 수정
	 * @param image
	 * @return
	 */
	int updateBnFileImage( BnFileImage image );
	
	/**
	 * 파일 비디오 추가
	 * @param video
	 * @return
	 */
	int insertBnFileVideo( BnFileVideo video );
	
	/**
	 * 파일 비디오 조회
	 * @param fileNo
	 * @return
	 */
	BnFileVideo selectBnFileVideoByFileNo( long fileNo );
	
	/**
	 * 파일 비디오 수정
	 * @param video
	 * @return
	 */
	int updateBnFileVideo( BnFileVideo video );
	
	/**
	 * 오디오 파일 추가
	 * @param audio
	 * @return
	 */
	int insertBnFileAudio( BnFileAudio audio );
	
	/**
	 * 오디오 파일 조회
	 * @param fileNo
	 * @return
	 */
	BnFileAudio selectBnFileAudioByFileNo( long fileNo );
	/**
	 * 오디오 파일 수정
	 * @param audio
	 * @return
	 */
	int updateBnFileAudio( BnFileAudio audio );
	
	/**
	 * 압축 파일 추가
	 * @param archive
	 * @return
	 */
	int insertBnFileArchive( BnFileArchive archive );
	
	/**
	 * 압축 파일 조회
	 * @param fileNo
	 * @return
	 */
	BnFileArchive selectBnFileArchiveByFileNo( long fileNo );
	
	/**
	 * 압축 파일 수정
	 * @param archive
	 * @return
	 */
	int updateBnFileArchive( BnFileArchive archive );
	
}