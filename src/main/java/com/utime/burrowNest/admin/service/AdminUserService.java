package com.utime.burrowNest.admin.service;

import java.util.List;

import com.utime.burrowNest.user.vo.UserVo;

public interface AdminUserService {

	/**
	 * 사용자 조회
	 * @param id
	 * @return
	 */
	List<UserVo> userList(String id);

	UserVo getUserFromNo(int userNo);

}
