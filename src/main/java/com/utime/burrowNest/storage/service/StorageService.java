package com.utime.burrowNest.storage.service;

import java.util.List;

import com.utime.burrowNest.common.vo.ReturnBasic;
import com.utime.burrowNest.storage.vo.BnDirectory;
import com.utime.burrowNest.storage.vo.BnFile;
import com.utime.burrowNest.storage.vo.DirectoryDto;
import com.utime.burrowNest.user.vo.InitInforReqVo;
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
	DirectoryDto getRootDirectory(UserVo user);

	/**
	 * dir 정보 조회
	 * @param user
	 * @param path
	 * @return
	 */
	DirectoryDto getDirectory(UserVo user, String path);

	/**
	 * dir의 파일 목록
	 * @param user
	 * @param dir
	 * @return
	 */
	List<BnFile> getFiles(UserVo user, BnDirectory dir);

	

}
