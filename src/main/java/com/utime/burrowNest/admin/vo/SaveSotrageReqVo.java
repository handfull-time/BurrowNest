package com.utime.burrowNest.admin.vo;

import java.util.List;

import com.utime.burrowNest.common.util.BurrowUtils;

import lombok.Getter;
import lombok.Setter;

/**
 * Root storage 정보
 */
@Getter
@Setter
public class SaveSotrageReqVo {
	
	/**
	 * 저장소 경로
	 */
	private List<String> roots;
	
	/**
	 * websocket id 
	 */
	private String wsUserName;
	
	@Override
	public String toString() {
		return BurrowUtils.toJson(this);
	}
}
