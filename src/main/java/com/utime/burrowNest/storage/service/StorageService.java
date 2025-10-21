package com.utime.burrowNest.storage.service;

import java.util.List;

import com.utime.burrowNest.common.vo.ReturnBasic;
import com.utime.burrowNest.storage.dto.ChildrenResponse;
import com.utime.burrowNest.storage.dto.DirContextResponse;
import com.utime.burrowNest.storage.dto.DirNodeDto;
import com.utime.burrowNest.storage.vo.AbsPath;
import com.utime.burrowNest.storage.vo.BnDirectory;
import com.utime.burrowNest.storage.vo.BnFile;
import com.utime.burrowNest.storage.vo.DirectoryDto;
import com.utime.burrowNest.user.vo.UserVo;

public interface StorageService {
	
	/**
	 * 저장소 초기 정보 세팅
	 * @param req
	 * @return
	 */
	ReturnBasic saveRootStorage(UserVo user);

	/**
	 * 섬네일 조회
	 * @param uid
	 * @return
	 */
	byte [] getThumbnail(UserVo user, String uid);

	/**
	 * dir 정보 조회
	 * @param user
	 * @param uid
	 * @return
	 */
	List<DirectoryDto> getDirectory(UserVo user, String uid);

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

	/**
	 * 파일 갖고 오기
	 * @param user
	 * @param uid
	 * @return
	 */
	BnFile getFile(UserVo user, String uid);

	/**
	 * 그룹 해당 루트 조회
	 * @param groupNo
	 * @return
	 */
	List<BnDirectory> getGroupStorageList(long groupNo);

	/**
	 * 그룹 최상위 폴더 목록 전달
	 * @return
	 */
	List<BnDirectory> getAdminRootStorage();

	DirContextResponse buildContext(UserVo user, String uid);

	ChildrenResponse findChildren(UserVo user, String uid, int page, int size, Object object);

	List<DirNodeDto> findAllowedRoots(UserVo user);

	/**
	 * 최상위 top of Root 조회 
	 * @return
	 */
	BnDirectory getAdminTopStorage();

	/**
	 * Path 목록 조회 - 이미 지정된 건 제외
	 * @param groupNo
	 * @param dirNo
	 * @return
	 */
	List<BnDirectory> getGroupStorageList(long groupNo, long dirNo);
	
	/**
	 * Path 목록 조회
	 * @param user
	 * @param uid
	 * @return
	 */
	List<BnDirectory> getGroupStorageList(UserVo user, String uid);
	
	/**
	 * 그룹 저장소 삭제
	 * @param groupNo
	 * @param dirNo
	 * @return
	 */
	ReturnBasic removeGroupStorage(long groupNo, long dirNo);

}
