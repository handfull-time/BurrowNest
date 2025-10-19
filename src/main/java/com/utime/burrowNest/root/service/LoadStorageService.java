package com.utime.burrowNest.root.service;

import com.utime.burrowNest.admin.vo.SaveSotrageReqVo;
import com.utime.burrowNest.common.vo.ReturnBasic;

public interface LoadStorageService {
	
	/**
	 * 저장소 초기 정보 저장
	 * @param req
	 * @return
	 */
	ReturnBasic saveRootStorage(SaveSotrageReqVo req);
}
