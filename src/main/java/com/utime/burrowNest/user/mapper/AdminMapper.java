package com.utime.burrowNest.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.utime.burrowNest.user.vo.UserVo;

/**
 * 관리자 처리
 */
@Mapper
public interface AdminMapper {
	
	/**
	 * 초기화 했는지 여부
	 * @return
	 */
	boolean IsInit();
	
	/**
	 * 회원 테이블 생성
	 * @return
	 */
	int createAdmin();
	
	/**
	 * 어드민 비번 추가
	 * @param pw
	 * @return
	 */
	int insertAdmin( @Param("pw") String pw );
	
	/**
	 * 어드민 비번 수정
	 * @param pw
	 * @return
	 */
	int updateAdmin( @Param("pw") String pw );
	
	/**
	 * 어드민 pw 조회
	 * @return
	 */
	String getAdminPw();
	
	/**
	 * 사용자 추가
	 * @param user
	 * @return
	 */
	int insertUser(UserVo user);
	
	/**
	 * 사용자 정보 상세 갱신 
	 * @param user
	 * @return
	 */
	int updateUser( UserVo user );


	/**
	 * 사용자 목록 조회
	 * @return
	 */
	List<UserVo> getUserList();
}
