package com.utime.burrowNest.storage.service;

import java.util.List;

import com.utime.burrowNest.common.vo.ReturnBasic;
import com.utime.burrowNest.storage.vo.AbsPath;
import com.utime.burrowNest.storage.vo.BnDirectory;
import com.utime.burrowNest.storage.vo.BnFile;
import com.utime.burrowNest.user.vo.UserVo;

public interface StorageService {
	
	/**
	 * 저장소 초기 정보 세팅
	 * @param req
	 * @return
	 */
	ReturnBasic saveRootStorage(UserVo user);

	/**
	 * 부모 directory 조회
	 * @param user
	 * @param uid
	 * @return
	 */
	BnDirectory getParentDirectory(UserVo user, String uid);

	/**
	 * Path 목록 조회
	 * @param user
	 * @param uid
	 * @return
	 */
	List<BnDirectory> getGroupStorageList(UserVo user, String uid);

	/**
	 * 파일 목록 조회
	 * @param user
	 * @param uid
	 * @return
	 */
	List<AbsPath> getFiles(UserVo user, String uid);

	/**
	 * 파일 갖고 오기
	 * @param user
	 * @param uid
	 * @return
	 */
	BnFile getFile(UserVo user, String uid);

	/**
	 * 섬네일 조회
	 * @param uid
	 * @return
	 */
	byte [] getThumbnail(UserVo user, String uid);

}
