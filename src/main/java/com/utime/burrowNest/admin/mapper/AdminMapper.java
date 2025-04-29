package com.utime.burrowNest.admin.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.utime.burrowNest.admin.vo.ManageUserVo;
import com.utime.burrowNest.user.vo.UserVo;

/**
 * 관리자 처리
 */
@Mapper
public interface AdminMapper {
	
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
	List<ManageUserVo> getUserList( @Param("id") String id);
	
	/**
	 * 사용자 상세 정보 조회
	 * @param userNo
	 * @return
	 */
	UserVo getUserDetail( @Param("userNo") int userNo);

}
