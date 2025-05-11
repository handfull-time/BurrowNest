package com.utime.burrowNest.root.service;

import com.utime.burrowNest.common.vo.ReturnBasic;
import com.utime.burrowNest.user.vo.InitInforReqVo;

public interface LoadStorageService {
	

	/**
	 * 관리자 초기화 했는지 여부 
	 * @return true:했음.
	 */
	boolean IsInit();

	
	/**
	 * 저장소 초기 정보 저장
	 * @param req
	 * @return
	 */
	ReturnBasic saveInitStorage(InitInforReqVo req);
}
