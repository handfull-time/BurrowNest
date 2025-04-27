package com.utime.burrowNest.user.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 회원가입 요청 값
 */
@Setter
@Getter
@ToString(callSuper = true)
public class UserReqVo extends LoginReqVo{
	
	/**
	 * 암호 찾기 : 무지개 색 
	 */
	private String myRainbow;
	/**
	 * 암호 찾기 : 계절
	 */
	private String mySeason;
	/**
	 * 암호 찾기 : 숫자
	 */
	private String myNumber;

}
