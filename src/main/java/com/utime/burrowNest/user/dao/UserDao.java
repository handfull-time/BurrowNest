package com.utime.burrowNest.user.dao;

import java.util.List;

import com.utime.burrowNest.user.vo.InitInforReqVo;
import com.utime.burrowNest.user.vo.LoginReqVo;
import com.utime.burrowNest.user.vo.ResUserVo;
import com.utime.burrowNest.user.vo.UserVo;

public interface UserDao {

	/**
	 * 초기화 처리
	 * @param req
	 * @return
	 * @throws Exception
	 */
	int saveInitInfo(InitInforReqVo req) throws Exception;

	/**
	 * 회원 로그인
	 * @param reqVo
	 * @return
	 */
	ResUserVo procLogin(LoginReqVo reqVo);
	
	
	/**
	 * 사용자 추가
	 * @param user
	 * @return
	 */
	int insertUser(UserVo user) throws Exception;
	
	/**
	 * 사용자 정보 수정
	 * @param user
	 * @return
	 * @throws Exception
	 */
	int updateUser(UserVo user) throws Exception;

	/**
	 * 사용자 목록 조회
	 * @return
	 */
	List<UserVo> getUserList();

}
