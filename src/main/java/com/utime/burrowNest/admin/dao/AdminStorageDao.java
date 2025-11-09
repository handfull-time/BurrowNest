package com.utime.burrowNest.admin.dao;

import java.util.List;

import com.utime.burrowNest.storage.vo.BnDirectory;

public interface AdminStorageDao {

	/**
	 * 루트 Dir 조회
	 * @param owner
	 * @return
	 */
	BnDirectory getRootDirectory();
	
	/**
	 * 최상위 폴더 목록 전달
	 * @return
	 */
	List<BnDirectory> getAdminRootStorage();
	
	/**
	 * 루트 Dir 조회.
	 * @param groupNo
	 * @return
	 */
	List<BnDirectory> getRootDirectory(long groupNo);
	
	/**
	 * 루트 Dir 조회.
	 * @param groupNo
	 * @return
	 */
	List<BnDirectory> selectUnIncludeRootDirectories(long groupNo);
	
	/**
	 * 접근 가능 dir 목록 조회
	 * @param groupNo
	 * @param parentDirNo
	 * @return
	 */
	List<BnDirectory> getGroupStorageList(long groupNo, long parentDirNo);
	
	/**
	 * 접근 가능 dir 목록 조회
	 * @param groupNo
	 * @param parentUid
	 * @return
	 */
	List<BnDirectory> getGroupStorageList(long groupNo, String parentUid);

	/**
	 * 그룹 저장소 삭제
	 * @param groupNo
	 * @param dirNo
	 * @return
	 */
	int removeGroupStorage(long groupNo, long dirNo)throws Exception;
	
	/**
	 * 그룹 소유자 저장소 목록 전달
	 * @param groupNo
	 * @return
	 */
	List<BnDirectory> getOwnerGroupStorageList(long groupNo);

	/**
	 * 루트 디렉토리 삭제
	 * @param no
	 * @return
	 */
	int deleteRootDirectory(long no);
}
