package com.utime.burrowNest.user.dao;

import java.util.List;

import com.utime.burrowNest.user.vo.LoginReqVo;
import com.utime.burrowNest.user.vo.ResUserVo;
import com.utime.burrowNest.user.vo.UserVo;

public interface UserDao {
	
	public final String AdminId = "admin";

	/**
	 * 회원 로그인
	 * @param reqVo
	 * @return
	 */
	ResUserVo procLogin(LoginReqVo reqVo)throws Exception ;
	
	
	/**
	 * 사용자 추가
	 * @param user
	 * @return
	 */
	int insertUser(UserVo user, String pw) throws Exception;
	
	/**
	 * 사용자 정보 수정
	 * @param user
	 * @return
	 * @throws Exception
	 */
	int updateUser(UserVo user) throws Exception;
	
	/**
	 * 암호 변경
	 * @param user
	 * @param pw
	 * @return
	 * @throws Exception
	 */
	int updateUserPw(UserVo user, String pw) throws Exception;
	
	/**
	 * 관리자 조회
	 * @return
	 */
	UserVo getManageUser();

	/**
	 * 사용자 목록 조회
	 * @return
	 */
	List<UserVo> getUserList();

	/**
	 * 회원 상태 변경
	 * @param user
	 * @return
	 */
	int updateUserEnabled(UserVo user)throws Exception;

}
