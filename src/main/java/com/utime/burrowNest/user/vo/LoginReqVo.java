package com.utime.burrowNest.user.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 로그인 요청 정보
 */
@Setter
@Getter
@ToString(callSuper = true)
public class LoginReqVo extends ReqUniqueVo{
	
	/**
	 * id 
	 */
	private String id;

	/**
	 * 암호
	 */
	private String pw;
	
	/**
	 * 접속 브라우져 정보
	 */
	private String userAgent;
	
}
