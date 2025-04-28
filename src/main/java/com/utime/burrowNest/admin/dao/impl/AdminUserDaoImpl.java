package com.utime.burrowNest.admin.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.utime.burrowNest.admin.dao.AdminUserDao;
import com.utime.burrowNest.user.mapper.AdminMapper;
import com.utime.burrowNest.user.vo.UserVo;

@Repository
class AdminUserDaoImpl implements AdminUserDao{

	@Autowired
	private AdminMapper mapper;
	
	@Override
	public List<UserVo> userList(String id) {
		return mapper.getUserList(id);
	}
	
	@Override
	public UserVo getUserFromNo(int userNo) {
		return mapper.getUserDetail(userNo);
	}

}
