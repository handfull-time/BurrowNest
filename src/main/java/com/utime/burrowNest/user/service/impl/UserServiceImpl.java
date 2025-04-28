package com.utime.burrowNest.user.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.utime.burrowNest.user.dao.UserDao;
import com.utime.burrowNest.user.service.UserService;
import com.utime.burrowNest.user.vo.UserVo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
class UserServiceImpl implements UserService {
	
	@Autowired
	private UserDao userDao;
	
	@Override
	public UserVo getUserFromNo(int userNo) {
		UserVo result = userDao.getUserFromNo(userNo);
		return result;
	}
	
	
}
