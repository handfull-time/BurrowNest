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
	UserVo getId(@Param("id") String id);
	
	
	String getPw(@Param("id") String id);

	
	int updatePw(@Param("id") String id, @Param("pw") String pw);


}
