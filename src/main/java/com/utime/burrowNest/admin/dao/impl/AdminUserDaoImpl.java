package com.utime.burrowNest.admin.dao.impl;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.utime.burrowNest.admin.dao.AdminUserDao;
import com.utime.burrowNest.admin.mapper.AdminMapper;
import com.utime.burrowNest.admin.vo.BnAccessVo;
import com.utime.burrowNest.admin.vo.ManageUserVo;
import com.utime.burrowNest.common.util.BurrowUtils;
import com.utime.burrowNest.storage.vo.BnDirectory;
import com.utime.burrowNest.user.mapper.UserMapper;
import com.utime.burrowNest.user.vo.GroupVo;
import com.utime.burrowNest.user.vo.UserVo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
		}else {
			result = mapper.updateGroup(vo);
		}
		
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int deleteGroup(GroupVo group) throws Exception {
		return mapper.deleteGroup( group.getGroupNo() );
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int saveGroupStorageList(int groupNo, List<BnDirectory> list) throws Exception {
		int result = 0;
		
		final GroupVo group = userMapper.selectGroupByNo(groupNo);
		if( group == null ) {
			log.warn("그룹 없음");
			return result;
		}
		
		final int accType = group.getAccType().getBit();
		final Map<Long, BnAccessVo> dbAccessMap = mapper.selectDirectoryAccessGroup(groupNo)
		        .stream()
		        .collect(Collectors.toMap(BnAccessVo::getNo, Function.identity()));
		
		for (BnDirectory item : list) {
			final long directoryNo = item.getNo();
			final BnAccessVo existingAccess = dbAccessMap.remove(directoryNo);
			
			if (existingAccess == null) {
				final BnAccessVo newAccess = new BnAccessVo(directoryNo, groupNo, accType);
				result += mapper.insertDirectoryAccessGroup(newAccess);
			} else if (existingAccess.getAccType() != accType) {
				existingAccess.setAccType(accType);
				result += mapper.updateDirectoryAccessGroup(existingAccess);
			}
		}

		for (BnAccessVo leftover : dbAccessMap.values()) {
			result += mapper.deleteDirectoryAccessGroup(leftover);
		}
		
		return result;
	}
}
