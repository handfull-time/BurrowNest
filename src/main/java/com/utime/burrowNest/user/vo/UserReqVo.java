package com.utime.burrowNest.user.vo;

import org.springframework.web.multipart.MultipartFile;

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
	
	/** 별명 */
	private String nickname;
	
	/** 사용자 아이콘 */
	private MultipartFile profileImg;
	
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
