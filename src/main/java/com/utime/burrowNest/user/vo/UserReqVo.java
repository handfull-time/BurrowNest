package com.utime.burrowNest.user.vo;

import org.springframework.web.multipart.MultipartFile;

import com.utime.burrowNest.common.util.BurrowUtils;

import lombok.Getter;
import lombok.Setter;

/**
 * 회원가입 요청 값
 */
@Setter
@Getter
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
	
	@Override
	public String toString() {
		return BurrowUtils.toJson(this);
	}

}
