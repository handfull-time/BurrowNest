package com.utime.burrowNest.admin.dao;

import java.util.List;

import com.utime.burrowNest.admin.vo.ManageUserVo;
import com.utime.burrowNest.user.vo.UserVo;

public interface AdminUserDao {

	/**
	 * 회원 전체 조회
	 * @param id
	 * @return
	 */
	List<ManageUserVo> userList(String id);

	/**
	 * 회원 정보 조회
	 * @param userNo
	 * @return
	 */
	UserVo getUserFromNo(int userNo);

	/**
	 * 사용자 정보 수정
	 * @param user
	 * @return
	 */
	int updateUser(UserVo user);

}
