package com.utime.burrowNest.storage.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.utime.burrowNest.common.vo.BinResultVo;
import com.utime.burrowNest.storage.vo.BnDirectory;
import com.utime.burrowNest.storage.vo.BnFile;
import com.utime.burrowNest.storage.vo.BnFileArchive;
import com.utime.burrowNest.storage.vo.BnFileAudio;
import com.utime.burrowNest.storage.vo.BnFileDocument;
import com.utime.burrowNest.storage.vo.BnFileImage;
import com.utime.burrowNest.storage.vo.BnFileVideo;
import com.utime.burrowNest.storage.vo.BnPathAccess;
import com.utime.burrowNest.user.vo.GroupVo;

@Mapper
public interface StorageMapper {
	
	/**
	 * 관리자 초기화 했는지 여부 
	 * @return true:했음.
	 */
	boolean IsInit();
	
	/**
	 * dir 추가
	 * @param dir
	 * @return
	 */
	int insertDirectory( BnDirectory dir );
	
	/**
	 * dir 수정
	 * @param dir
	 * @return
	 */
	int updateDirectory( BnDirectory dir );

	/**
	 * 전체 목록 조회
	 * @return
	 */
	List<BnDirectory> selectAllDirectory();

	/**
	 * 루트 Dir 조회
	 * @return
	 */
	BnDirectory selectRootDirectory();

	/**
	 * dir 조회
	 * @param dirNo
	 * @return
	 */
	BnDirectory selectDirectoryByNo( @Param("dirNo") long dirNo );
	
	/**
	 * dir 조회
	 * @param user
	 * @param uid
	 * @return
	 */
	BnDirectory selectDirectoryByGuid(@Param("group") GroupVo group, @Param("uid") String uid);
	
	/**
	 * 삭제
	 * @param dirNo
	 * @return
	 */
	int deleteDirectoryByNo( @Param("dirNo") long dirNo );
	
	/**
	 * 파일 추가.
	 * @param file
	 * @return
	 */
	int insertFile( BnFile file );
	
	/**
	 * 파일 조회
	 * @param fileNo
	 * @return
	 */
	BnFile selectFileByNo( @Param("fileNo") long fileNo );
	
	/**
	 * 파일 조회
	 * @param uid
	 * @return
	 */
	BnFile selectFileByUid(@Param("uid") String uid);

	
	/**
	 * 파일 수정
	 * @param file
	 * @return
	 */
	int updateFile( BnFile file );
	
	/**
	 * 파일 삭제
	 * @param fileNo
	 * @return
	 */
	int deleteFileByNo( @Param("fileNo") long fileNo );
	
	/**
	 * Dir 권한 추가하기
	 * @param dirNo
	 * @param groupNo
	 * @param accType
	 * @return
	 */
	int insertDirectoryAccess(@Param("dirNo") long dirNo, @Param("groupNo") long groupNo, @Param("accType") int accType);

	/**
	 * Dir 권한 수정
	 * @param dirNo
	 * @param groupNo
	 * @param accType
	 * @return
	 */
	int updateDirectoryAccess(@Param("dirNo") long dirNo, @Param("groupNo") long groupNo, @Param("accType") int accType);
	
	/**
	 * 전체 엑세스 목록
	 * @return
	 */
	List<BnPathAccess> selectBnDirectoryAccess();

	/**
	 * 파일 권한 추가하기
	 * @param dirNo
	 * @param groupNo
	 * @param accType
	 * @return
	 */
	int insertFileAccess(@Param("fileNo") long fileNo, @Param("groupNo") long groupNo, @Param("accType") int accType);

	/**
	 * 파일 권한 수정하기
	 * @param fileNo
	 * @param groupNo
	 * @param accType
	 * @return
	 */
	int updateFileAccess(@Param("fileNo") long fileNo, @Param("groupNo") long groupNo, @Param("accType") int accType);

	/**
	 * 확장 정보 유무
	 * @param tableName
	 * @param fileNo
	 * @return true:있다.
	 */
	boolean existFileInfo( @Param("tableName") String tableName, @Param("fileNo") long fileNo );
	
	/**
	 * file doc 추가.
	 * @param document
	 * @return
	 */
	int insertFileDocument( BnFileDocument document );
	
	/**
	 * file doc 조회
	 * @param fileNo
	 * @return
	 */
	BnFileDocument selectFileDocumentByFileNo( @Param("fileNo") long fileNo );
	
	/**
	 * 파일 doc 수정
	 * @param document
	 * @return
	 */
	int updateFileDocument( BnFileDocument document );
	
	/**
	 * 파일 이미지 추가
	 * @param image
	 * @return
	 */
	int insertFileImage( BnFileImage image );
	
	/**
	 * 파일 이미지 조회
	 * @param fileNo
	 * @return
	 */
	BnFileImage selectFileImageByFileNo( @Param("fileNo") long fileNo );
	
	/**
	 * 파일 이미지 수정
	 * @param image
	 * @return
	 */
	int updateFileImage( BnFileImage image );
	
	/**
	 * 파일 비디오 추가
	 * @param video
	 * @return
	 */
	int insertFileVideo( BnFileVideo video );
	
	/**
	 * 파일 비디오 조회
	 * @param fileNo
	 * @return
	 */
	BnFileVideo selectFileVideoByFileNo( @Param("fileNo") long fileNo );
	
	/**
	 * 파일 비디오 수정
	 * @param video
	 * @return
	 */
	int updateFileVideo( BnFileVideo video );
	
	/**
	 * 오디오 파일 추가
	 * @param audio
	 * @return
	 */
	int insertFileAudio( BnFileAudio audio );
	
	/**
	 * 오디오 파일 조회
	 * @param fileNo
	 * @return
	 */
	BnFileAudio selectFileAudioByFileNo( @Param("fileNo") long fileNo );
	/**
	 * 오디오 파일 수정
	 * @param audio
	 * @return
	 */
	int updateFileAudio( BnFileAudio audio );
	
	/**
	 * 압축 파일 추가
	 * @param archive
	 * @return
	 */
	int insertFileArchive( BnFileArchive archive );
	
	/**
	 * 압축 파일 조회
	 * @param fileNo
	 * @return
	 */
	BnFileArchive selectFileArchiveByFileNo( @Param("fileNo") long fileNo );
	
	/**
	 * 압축 파일 수정
	 * @param archive
	 * @return
	 */
	int updateFileArchive( BnFileArchive archive );
	
	/**
	 * 섬네일 추가
	 * @param fileNo
	 * @param thumbnail
	 * @return
	 */
	int insertThumbnail( @Param("fileNo") long fileNo, @Param("thumbnail") byte [] thumbnail);
	
	/**
	 * 섬네일 조회
	 * @param fileNo
	 * @param thumbnail
	 * @return
	 */
	int updateThumbnail( @Param("fileNo") long fileNo, @Param("thumbnail") byte [] thumbnail);
	
	/**
	 * 섬네일 조회
	 * @param uid
	 * @return
	 */
	BinResultVo selectThumbnail( @Param("fid") String uid );

	/**
	 * 루트 directory 조회
	 * @param user
	 * @return
	 */
	List<BnDirectory> selectRootDirectories(@Param("groupNo") long groupNo);

	/**
	 * directory 속한 파일 및 폴더 목록
	 * @param group
	 * @param no
	 * @return
	 */
	List<BnFile> selectFiles(@Param("group") GroupVo group, @Param("dirNo") long no);
	
	/**
	 * dir의 Directory 목록 
	 * @param user
	 * @param dir
	 * @return
	 */
	List<BnDirectory> selectDirectories(@Param("group") GroupVo group, @Param("dirNo") long no);

	/**
	 * Directory의 경로 목록 조회
	 * @param group
	 * @param no
	 * @return
	 */
	List<String> selectPaths(@Param("group") GroupVo group, @Param("dirNo") long no);

	/**
	 * parentNo 기준 조회
	 * @param parentNo
	 * @return
	 */
	List<BnDirectory> selectBnDirectoryParentNo(@Param("parentNo") long parentNo);


}

/*
 * 
 이건 동작 하는 SQL 이다. 
 ㅠㅠ 얼마만인가...
 덕분에 구조를 싹 바꿔야 하지만...
 
WITH RECURSIVE subtree (NO, PARENT_NO, NAME, DEPTH) AS (
  SELECT NO, PARENT_NO, NAME, CAST(0 AS INT) AS DEPTH
  FROM BN_DIRECTORY
  WHERE NO = 1
  UNION ALL
  SELECT d.NO, d.PARENT_NO, d.NAME, s.DEPTH + 1
  FROM BN_DIRECTORY d
  JOIN subtree s ON d.PARENT_NO = s.NO
)
SELECT * FROM subtree
ORDER BY DEPTH, NO;


WITH RECURSIVE st (ID, PARENT_ID, NM, DEPTH) AS (
  SELECT NO, PARENT_NO, NAME, CAST(0 AS INT) FROM BN_DIRECTORY WHERE NO = 1
  UNION ALL
  SELECT d.NO, d.PARENT_NO, d.NAME, st.DEPTH + 1
  FROM BN_DIRECTORY d JOIN st ON d.PARENT_NO = st.ID
)
SELECT * FROM st ORDER BY DEPTH, ID;



HAS_CHILD 대용 쿼리

SELECT d.NO,
       EXISTS (SELECT 1 FROM BN_DIRECTORY c WHERE c.PARENT_NO = d.NO) AS HAS_CHILD
FROM BN_DIRECTORY d
WHERE d.NO IN (:listOrFilter);


-- Depth 목록 전달.
WITH RECURSIVE ancestors (NO, PARENT_NO, NAME, DEPTH) AS (
    SELECT d.NO, d.PARENT_NO, d.NAME, CAST(0 AS INT) AS DEPTH
    FROM BN_DIRECTORY d
    WHERE d.NO =1
    UNION ALL
    SELECT p.NO, p.PARENT_NO, p.NAME, a.DEPTH + 1
    FROM BN_DIRECTORY p
    JOIN ancestors a ON a.PARENT_NO = p.NO
  )
  SELECT NO AS no, PARENT_NO AS parentNo, NAME AS name, DEPTH AS depth
  FROM ancestors
  ORDER BY DEPTH
 */