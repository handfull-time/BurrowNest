package com.utime.burrowNest.admin.dao;

import java.util.List;

import com.utime.burrowNest.user.vo.UserVo;

public interface AdminUserDao {

	List<UserVo> userList(String id);

	UserVo getUserFromNo(int userNo);

}
