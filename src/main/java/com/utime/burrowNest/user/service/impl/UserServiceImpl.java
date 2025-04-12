package com.utime.burrowNest.user.service.impl;

import org.springframework.stereotype.Service;

import com.utime.burrowNest.user.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
class UserServiceImpl implements UserService {
	
	@Override
	public boolean IsInit() {
		return false;
	}
	
	
}
