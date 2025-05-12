package com.utime.burrowNest.admin.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.utime.burrowNest.admin.dao.AdminUserDao;
import com.utime.burrowNest.admin.mapper.AdminMapper;
import com.utime.burrowNest.admin.vo.ManageUserVo;
import com.utime.burrowNest.user.mapper.UserMapper;
import com.utime.burrowNest.user.vo.GroupVo;
import com.utime.burrowNest.user.vo.UserVo;

@Repository
class AdminUserDaoImpl implements AdminUserDao{

	@Autowired
	private AdminMapper mapper;
	
	@Autowired
	private UserMapper userMapper;
	
	
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
	@Transactional(rollbackFor = Exception.class)
	public int deleteUser(UserVo user) throws Exception {
		return mapper.deleteUser( user.getUserNo() );
	}

	@Override
	public List<GroupVo> getUserGroupList(Boolean enabled, String grName) {
		return mapper.selectUserGroupList(enabled, grName);
	}

	@Override
	public GroupVo getGroupByNo(int groupNo) {
		return userMapper.selectGroupByNo(groupNo);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int saveGroup(GroupVo vo) throws Exception {

		int result;
		if( vo.getGroupNo() < 1 ) {
			result = userMapper.insertGroup(vo);
			if( result > 0 ) {
				result += mapper.insertDirectoryAccessGroup( vo.getGroupNo(), vo.getAccType().getBit() );
				result += mapper.insertFileAccessGroup( vo.getGroupNo(), vo.getAccType().getBit() );
			}
		}else {
			final GroupVo dbGroup = userMapper.selectGroupByNo(vo.getGroupNo()); 
			final boolean isSame = dbGroup.getAccType() == vo.getAccType();
			
			result = mapper.updateGroup(vo);
			
			if( ! isSame && result > 0 ) {
				result += mapper.updateDirectoryAccessGroup( vo.getGroupNo(), vo.getAccType().getBit() );
				result += mapper.updateFileAccessGroup( vo.getGroupNo(), vo.getAccType().getBit() );
			}
		}
		
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int deleteGroup(GroupVo group) throws Exception {
		return mapper.deleteGroup( group.getGroupNo() );
	}
}
