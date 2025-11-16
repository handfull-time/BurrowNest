package com.utime.burrowNest.root.service;

import java.io.File;

import com.utime.burrowNest.admin.vo.SaveSotrageReqVo;
import com.utime.burrowNest.common.vo.ReturnBasic;
import com.utime.burrowNest.user.vo.UserVo;

public interface LoadStorageService {
	
	/**
	 * 저장소 초기 정보 저장
	 * @param req
	 * @return
	 */
	ReturnBasic saveRootStorage(SaveSotrageReqVo req);

	/**
	 * 루트 저장소 삭제
	 * @param no
	 * @return
	 */
	ReturnBasic deleteRootStorage(long no);
	
	/**
	 * 파일 정보 저장
	 * @param parentNo
	 * @param owner
	 * @param file
	 * @return
	 */
	ReturnBasic saveFileStorage(long parentNo, UserVo owner, File file);
}
