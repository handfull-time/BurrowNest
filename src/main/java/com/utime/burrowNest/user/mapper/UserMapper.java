package com.utime.burrowNest.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.utime.burrowNest.user.vo.ELoginResult;
import com.utime.burrowNest.user.vo.LoginReqVo;
import com.utime.burrowNest.user.vo.UserVo;

/**
 * 사용자 처리
 */
@Mapper
public interface UserMapper {
	
	/**
	 * 회원 테이블 생성
	 * @return
	 */
	int createUser();
	
	/**
	 * 비번 
	 * @return
	 */
	int createUserPw();
	
	/**
	 * 로그인 기록
	 * @return
	 */
	int createLoginRecord();
	
	/**
	 * 로그인 기록 추가.
	 * @param reqVo
	 * @param user
	 * @param idnotfound
	 * @return
	 */
	int insertLoginRecord(@Param("req") LoginReqVo reqVo, @Param("user") UserVo user, @Param("status") ELoginResult res);

	/**
	 * id 조회
	 * @param id
	 * @return
	 */
	UserVo getUserId(@Param("id") String id);
	
	/**
	 * 기본 회원 정보 조회
	 * @param id
	 * @return
	 */
	UserVo getUserIdBasic(@Param("id") String id);
	
	/**
	 * 암호 조회
	 * @param id
	 * @return
	 */
	String getUserPw(@Param("id") String id);

	/**
	 * pw 추가
	 * @param user
	 * @param pw
	 * @return
	 */
	int insertUserPw(@Param("user") UserVo user, @Param("pw") String pw);
	
	/**
	 * pw 수정.
	 * @param user
	 * @param pw
	 * @return
	 */
	int updateUserPw(@Param("user") UserVo user, @Param("pw") String pw);

	/**
	 * 사용자 정보 갱신 
	 * @param user
	 * @return
	 */
	int updateUser( UserVo user );

	/**
	 * PW 성공 여부 횟수 업데이트
	 * @param user
	 * @param isSuccess
	 * @return
	 */
	int updatePwCount(@Param("user") UserVo user, @Param("isSuccess") boolean isSuccess);
}
