package com.utime.burrowNest.admin.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.utime.burrowNest.admin.dao.AdminUserDao;
import com.utime.burrowNest.admin.service.AdminUserService;
import com.utime.burrowNest.admin.vo.ManageUserVo;
import com.utime.burrowNest.common.vo.ReturnBasic;
import com.utime.burrowNest.user.vo.UserVo;

@Service
class AdminUserServiceImpl implements AdminUserService{

	@Autowired
	private AdminUserDao adminUserDao;

	@Override
	public List<ManageUserVo> userList(String id) {
		
		return adminUserDao.userList(id);
	}
	
	@Override
	public UserVo getUserFromNo(int userNo) {
		return adminUserDao.getUserFromNo(userNo);
	}
	
	@Override
	public ReturnBasic saveUser(UserVo user) {
		ReturnBasic result = new ReturnBasic();
		
		try {
			adminUserDao.updateUser( user );
		} catch (Exception e) {
			result.setCodeMessage("E", e.getMessage());
		}
		
		return result;
	}
}
