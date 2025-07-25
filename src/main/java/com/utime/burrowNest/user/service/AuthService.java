package com.utime.burrowNest.user.service;


import com.utime.burrowNest.common.vo.ReturnBasic;
import com.utime.burrowNest.user.vo.LoginReqVo;
import com.utime.burrowNest.user.vo.ReqUniqueVo;
import com.utime.burrowNest.user.vo.ResUserVo;
import com.utime.burrowNest.user.vo.UserReqVo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
	
	/**
	 * 초기화 했나?
	 * @return true:초기화 했다.
	 */
	boolean IsInit();
	
	/**
	 * 초기 정보 - 암호화, 유니크 검사 필수 값 등.
	 * @param request
	 * @return
	 */
	ReqUniqueVo getNewGenUnique(HttpServletRequest request);

	/**
	 * 초기 정보 저장
	 * @param req
	 * @return
	 */
	ReturnBasic saveInitInfor(UserReqVo req);

	/**
	 * 로그인 처리
	 * @param request
	 * @param response
	 * @param reqVo
	 * @return
	 */
	ResUserVo procLogin(HttpServletRequest request, HttpServletResponse response, LoginReqVo reqVo);
	
	/**
	 * 회원 가입
	 * @param request
	 * @param reqVo
	 * @return
	 */
	ReturnBasic procJoinUser(UserReqVo reqVo);

	/**
	 * 로그 아웃
	 * @param request
	 * @param response
	 */
	void logout(HttpServletRequest request, HttpServletResponse response);

	/**
	 * 토큰 업뎃
	 * @param request
	 * @param response
	 * @param refreshToken
	 * @return
	 */
	ReturnBasic refreshAccessToken(HttpServletRequest request, HttpServletResponse response, String refreshToken);

	/**
	 * 비번 찾기
	 * @param reqVo
	 * @return
	 */
	ReturnBasic findUserPw(UserReqVo reqVo);

	/**
	 * 암호 변경
	 * @param reqVo
	 * @return
	 */
	ReturnBasic convertUserPw(LoginReqVo reqVo);

	/**
	 * id가 사용하던 id 인지 검사.
	 * @param id
	 * @return
	 */
	ReturnBasic checkId(String id);

}
