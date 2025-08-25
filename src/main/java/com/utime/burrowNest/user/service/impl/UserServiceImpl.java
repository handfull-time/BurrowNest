package com.utime.burrowNest.user.service.impl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.utime.burrowNest.common.util.BurrowUtils;
import com.utime.burrowNest.user.dao.UserDao;
import com.utime.burrowNest.user.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
class UserServiceImpl implements UserService {
	
	@Autowired
	private UserDao userDao;

	private byte [] defaultProfileImg() {
		
		byte[] result;
		try {
			result = BurrowUtils.encodeImageToByteArray(new ClassPathResource("static/images/profile/DefaultProfile.png").getInputStream());
		} catch (IOException e) {
			log.error("", e);
			return null;
		}
		
		return result;
	}
	
	@Override
	public byte[] getThumbnail(long userNo) {
		
		if( userNo < 1 ) {
			return this.defaultProfileImg();
		}
		
		byte[] result;
		result = userDao.getProfileImg( userNo );
		
		if( result == null ) {
			result = this.defaultProfileImg();
		}
		
		return result;
	}
}
