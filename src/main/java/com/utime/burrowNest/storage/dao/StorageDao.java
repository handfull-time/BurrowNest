package com.utime.burrowNest.storage.dao;

import java.util.List;
import java.util.Map;

import com.utime.burrowNest.storage.vo.AbsBnFileInfo;
import com.utime.burrowNest.storage.vo.AbsPath;
import com.utime.burrowNest.storage.vo.BnDirectory;
import com.utime.burrowNest.storage.vo.BnFile;
import com.utime.burrowNest.storage.vo.BnPathAccess;
import com.utime.burrowNest.storage.vo.DirectoryDto;
import com.utime.burrowNest.storage.vo.EBnFileType;
import com.utime.burrowNest.user.vo.UserVo;

public interface StorageDao {

	/**
	 * 초기화 처리
	 * @return
	 * @throws Exception
	 */
	int initStorageTable()throws Exception;
	
	/**
	 * 관리자 초기화 했는지 여부 
	 * @return true:했음.
	 */
	boolean IsInit();
	
	/**
	 * 최초 Root dir 생성
	 * @param owner
	 * @return
	 * @throws Exception
	 */
	BnDirectory InsertRootDirectory(UserVo owner ) throws Exception;
	
	/**
	 * 루트 Dir 조회
	 * @param owner
	 * @return
	 */
	BnDirectory getRootDirectory(UserVo owner);
	
	/**
	 * 확장자 별 파일 종류
	 * @return
	 */
	Map<String, EBnFileType> getBnFileType();

	/**
	 * Dir 저장
	 * @param dir
	 * @param owner
	 * @return
	 */
	int saveDirectory(BnDirectory dir, UserVo owner ) throws Exception;
	
	/**
	 * File 저장
	 * @param file
	 * @param owner
	 * @return
	 * @throws Exception
	 */
	int saveFile(BnFile file, UserVo owner) throws Exception;
	
	/**
	 * 파일 확장 저장
	 * @param file
	 * @return
	 * @throws Exception
	 */
	int saveFileInfor( BnFile bnFile )throws Exception;
	
	/**
	 * 이미지 섬네일 저장
	 * @param file
	 * @param bArray
	 * @return
	 */
	int saveThumbnail( BnFile file, byte [] bArray);
	
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
	 * directory 정보 조회
	 * @param user
	 * @param uid
	 * @return
	 */
	BnDirectory getDirectory(UserVo user, String uid);
	
	/**
	 *  정보 조회
	 * @param fileNo
	 * @return
	 */
	BnFile getFile( long fileNo );
	
	/**
	 * 파일 정보 조회
	 * @param fileNo
	 * @return
	 */
	BnFile getFile( UserVo user, String uid );
	
	/**
	 * 파일 확장 조회
	 * @param file
	 * @return
	 */
	AbsBnFileInfo getFileInfor( BnFile file );

	/**
	 * 섬네일 조회
	 * @param uid
	 * @return
	 */
	byte[] getThumbnail(String uid);

	/**
	 * 루트 Dir 조회.
	 * @param groupNo
	 * @return
	 */
	List<BnDirectory> getRootDirectory(int groupNo);

	/**
	 * dir의 파일 목록
	 * @param user
	 * @param dir
	 * @return
	 */
	List<BnFile> getFiles(UserVo user, BnDirectory dir);

	/**
	 * dir의 Directory 목록 
	 * @param user
	 * @param dir
	 * @return
	 */
	List<BnDirectory> getDirectories(UserVo user, BnDirectory dir);

	/**
	 * Path 목록
	 * @param user
	 * @param dir
	 * @return
	 */
	List<String> getPaths(UserVo user, BnDirectory dir);
	
	/**
	 * 부모 directory 조회
	 * @param user
	 * @param uid
	 * @return
	 */
	BnDirectory getParentDirectory(UserVo user, String uid);

	/**
	 * 디랙터리 전체 조회
	 * @return
	 */
	List<BnDirectory> getAllDirectory();

	List<BnPathAccess> getAllDirectoryAccess();

	

}
