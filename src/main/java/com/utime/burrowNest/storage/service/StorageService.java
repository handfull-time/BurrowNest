package com.utime.burrowNest.storage.service;

import com.utime.burrowNest.common.vo.ReturnBasic;
import com.utime.burrowNest.user.vo.InitInforReqVo;

public interface StorageService {

	/**
	 * 저장소 초기 정보 저장
	 * @param req
	 * @return
	 */
	ReturnBasic saveInitStorage(InitInforReqVo req);
	
	/**
	 * 섬네일 조회
	 * @param fid
	 * @return
	 */
	byte [] getThumbnail(String fid);

}
