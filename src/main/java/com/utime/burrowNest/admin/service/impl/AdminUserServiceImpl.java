package com.utime.burrowNest.admin.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.utime.burrowNest.admin.dao.AdminUserDao;
import com.utime.burrowNest.admin.service.AdminUserService;
import com.utime.burrowNest.admin.vo.ManageUserVo;
import com.utime.burrowNest.common.vo.EJwtRole;
import com.utime.burrowNest.common.vo.ReturnBasic;
import com.utime.burrowNest.storage.vo.BnDirectory;
import com.utime.burrowNest.user.vo.GroupVo;
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
	public UserVo getUserFromNo(long userNo) {
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
	
	@Override
	public ReturnBasic deleteUser(UserVo user) {
		ReturnBasic result = new ReturnBasic();
		
		try {
			adminUserDao.deleteUser( user );
		} catch (Exception e) {
			result.setCodeMessage("E", e.getMessage());
		}
		
		return result;
	}

	@Override
	public List<GroupVo> getUserGroupListAll(String grName) {
		return adminUserDao.getUserGroupList(null, grName);	
	}
	
	@Override
	public List<GroupVo> getUserGroupList() {
		return adminUserDao.getUserGroupList(true, null);
	}

	@Override
	public GroupVo getGroupByNo(long groupNo) {
		final GroupVo result;
		if( groupNo < 1 ) {
			result = new GroupVo();
			result.setGroupNo(0);
			result.setEnabled(false);
			result.setRole(EJwtRole.User);
		}else {
			result = adminUserDao.getGroupByNo(groupNo);
		}
		
		return result;
	}

	@Override
	public ReturnBasic saveGroup(GroupVo group) {
		final ReturnBasic result = new ReturnBasic();
		
		try {
			adminUserDao.saveGroup( group );
		} catch (Exception e) {
			result.setCodeMessage("E", e.getMessage());
		}
		
		return result;
	}
	
	@Override
	public ReturnBasic deleteGroup(GroupVo group) {
		final ReturnBasic result = new ReturnBasic();
		
		try {
			adminUserDao.deleteGroup( group );
		} catch (Exception e) {
			result.setCodeMessage("E", e.getMessage());
		}
		
		return result;
	}
	
	@Override
	public ReturnBasic setGroupStorageList(long groupNo, List<BnDirectory> list) {
		final ReturnBasic result = new ReturnBasic();
		
		try {
			adminUserDao.saveGroupStorageList( groupNo, list );
		} catch (Exception e) {
			result.setCodeMessage("E", e.getMessage());
		}
		
		return result;
	}
}
