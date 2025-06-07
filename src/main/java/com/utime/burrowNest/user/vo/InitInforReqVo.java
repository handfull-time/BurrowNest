package com.utime.burrowNest.user.vo;

import java.util.List;

import com.utime.burrowNest.common.util.BurrowUtils;

import lombok.Getter;
import lombok.Setter;

/**
 * 관리자 가입 요청 값
 */
@Getter
@Setter
public class InitInforReqVo extends UserReqVo{
	
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
