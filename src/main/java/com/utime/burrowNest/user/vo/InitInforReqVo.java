package com.utime.burrowNest.user.vo;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 관리자 가입 요청 값
 */
@Getter
@Setter
@ToString(callSuper = true)
public class InitInforReqVo extends UserReqVo{
	
	/**
	 * 저장소 경로
	 */
	private List<String> roots;
	
	/**
	 * websocket id 
	 */
	private String wsUserName;
	
	
}
