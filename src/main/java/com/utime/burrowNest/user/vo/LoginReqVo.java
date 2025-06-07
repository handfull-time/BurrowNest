package com.utime.burrowNest.user.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.utime.burrowNest.common.util.BurrowUtils;

import lombok.Getter;
import lombok.Setter;

/**
 * 로그인 요청 정보
 */
@Setter
@Getter
public class LoginReqVo extends ReqUniqueVo{
	
	/**
	 * id 
	 */
	private String id;

	/**
	 * 암호
	 */
	@JsonIgnore
	private String pw;
	
	/**
	 * 접속 브라우져 정보
	 */
	private String userAgent;
	
	@Override
	public String toString() {
		return BurrowUtils.toJson(this);
	}
	
}
