package com.utime.burrowNest.admin.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.utime.burrowNest.admin.dao.AdminUserDao;
import com.utime.burrowNest.admin.mapper.AdminMapper;
import com.utime.burrowNest.admin.vo.ManageUserVo;
import com.utime.burrowNest.user.vo.UserVo;

@Repository
class AdminUserDaoImpl implements AdminUserDao{

	@Autowired
	private AdminMapper mapper;
	
	@Override
	public List<ManageUserVo> userList(String id) {
		return mapper.getUserList(id);
	}
	
	@Override
	public UserVo getUserFromNo(int userNo) {
		return mapper.getUserDetail(userNo);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int updateUser(UserVo user) throws Exception{
		return mapper.updateUser(user);
	}

	@Override
	public int deleteUser(UserVo user) throws Exception {
		return mapper.deleteUser( user.getUserNo() );
	}

}
