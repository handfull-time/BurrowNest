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
	 * @param reqVo
	 * @param user
	 * @param pw
	 * @return
	 * @throws Exception
	 */
	int insertUser(LoginReqVo reqVo, UserVo user, String pw, byte [] profileImg) throws Exception;
	
	/**
	 * 사용자 정보 수정
	 * @param user
	 * @return
	 * @throws Exception
	 */
	int updateUser(UserVo user, byte [] profileImg) throws Exception;
	
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
	 * 사용자 정보 조회
	 * @param id
	 * @return
	 */
	UserVo getUserFormId(String id);

	/**
	 * 사용자 정보 조회 (캐시 사용 )
	 * @param id
	 * @return
	 */
	UserVo getUserFormIdByProvider(String id);
	
	/**
	 * 해당 id 찾기
	 * @param id
	 * @param genUserUniqueHashing
	 * @return
	 */
	UserVo findUser(String id, String genUserUniqueHashing);

	/**
	 * id 사용 여부
	 * @param id
	 * @return
	 */
	boolean checkId(String id);

	/**
	 * 사용자 프로필 이미지
	 * @param userNo
	 * @return
	 */
	byte[] getProfileImg(int userNo);


}
