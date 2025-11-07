package com.utime.burrowNest.admin.service;

import java.util.List;

import com.utime.burrowNest.common.vo.ReturnBasic;
import com.utime.burrowNest.storage.vo.BnDirectory;
import com.utime.burrowNest.user.vo.UserVo;

public interface AdminStorageService {
	
	/**
	 * 그룹 최상위 폴더 목록 전달
	 * @return
	 */
	List<BnDirectory> getAdminRootStorage();

	
	/**
	 * 최상위 top of Root 조회 
	 * @return
	 */
	BnDirectory getAdminTopStorage();
	
	/**
	 * 그룹 해당 루트 조회
	 * @param groupNo
	 * @return
	 */
	List<BnDirectory> getGroupStorageList(long groupNo);
	
	/**
	 * Path 목록 조회
	 * @param user
	 * @param uid
	 * @return
	 */
	List<BnDirectory> getGroupStorageList(UserVo user, String uid);
	
	/**
	 * Path 목록 조회 - 이미 지정된 건 제외
	 * @param user
	 * @param uid
	 * @return
	 */
	List<BnDirectory> getGroupStorageList(long groupNo, long dirNo);
	
	/**
	 * 그룹 저장소 삭제
	 * @param groupNo
	 * @param dirNo
	 * @return
	 */
	ReturnBasic removeGroupStorage(long groupNo, long dirNo);
}
