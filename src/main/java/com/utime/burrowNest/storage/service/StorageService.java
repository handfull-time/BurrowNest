package com.utime.burrowNest.storage.service;

import java.util.List;

import com.utime.burrowNest.storage.vo.AbsPath;
import com.utime.burrowNest.storage.vo.BnDirectory;
import com.utime.burrowNest.user.vo.UserVo;

public interface StorageService {
	
	/**
	 * 섬네일 조회
	 * @param fid
	 * @return
	 */
	byte [] getThumbnail(String fid);

	/**
	 * Root dir 정보 조회
	 * @param user
	 * @return
	 */
	BnDirectory getRootDirectory(UserVo user);

	/**
	 * dir 정보 조회
	 * @param user
	 * @param uid
	 * @return
	 */
	BnDirectory getDirectory(UserVo user, String uid);

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
	 * 파일 목록 조회
	 * @param user
	 * @param uid
	 * @return
	 */
	List<AbsPath> getFiles(UserVo user, String uid);

	

}
