package com.utime.burrowNest.user.dao.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.utime.burrowNest.common.mapper.CommonMapper;
import com.utime.burrowNest.common.util.BurrowUtils;
import com.utime.burrowNest.common.util.CacheIntervalMap;
import com.utime.burrowNest.common.util.Sha256;
import com.utime.burrowNest.user.dao.UserDao;
import com.utime.burrowNest.user.mapper.AdminMapper;
import com.utime.burrowNest.user.mapper.UserMapper;
import com.utime.burrowNest.user.vo.ELoginResult;
import com.utime.burrowNest.user.vo.LoginReqVo;
import com.utime.burrowNest.user.vo.ResUserVo;
import com.utime.burrowNest.user.vo.UserVo;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
class UserDaoImpl implements UserDao {
	
	@Autowired
	private CommonMapper common;
	
	@Autowired
	private AdminMapper adminMapper;
	
	@Autowired
	private UserMapper userMapper;
	
	@Value("${security.pwSaltKey}")
    private String saltKey;
	
	final CacheIntervalMap<String, UserVo> intervalMap = new CacheIntervalMap<>(10L, TimeUnit.MINUTES);
	
	@PostConstruct
	private void construct() {
		try {
			
//			// 가계부 메인 데이터
//			if( ! common.existTable("BN_ADMIN") ) {
//				log.info("BN_ADMIN 생성");
//				adminMapper.createAdmin();
//			}
			
			if( ! common.existTable("BN_USER") ) {
				log.info("BN_USER 생성");
				userMapper.createUser();
				common.createUniqueIndex("BN_USER_ID_INDX", "BN_USER", "ID");
			}

			if( ! common.existTable("BN_USER_LOGIN_RECORD") ) {
				log.info("BN_USER_LOGIN_RECORD 생성");
				userMapper.createLoginRecord();
				common.createIndex("BN_USER_LOGIN_RECORD_IP_INDX", "BN_USER_LOGIN_RECORD", "IP");
				common.createIndex("BN_USER_LOGIN_RECORD_USER_NO_INDX", "BN_USER_LOGIN_RECORD", "USER_NO");
				common.createIndex("BN_USER_LOGIN_RECORD_REG_DATE_INDX", "BN_USER_LOGIN_RECORD", "REG_DATE");
				
			}

			if( ! common.existTable("BN_USER_PW") ) {
				log.info("BN_USER_PW 생성");
				userMapper.createUserPw();
				
			}
		} catch (Exception e) {
			log.error("", e);
		}
	}

	/**
	 * 회원 비번 생성
	 * @param user
	 * @param pw
	 * @return
	 */
	private String genPwString( UserVo user, String pw ) {
		return saltKey + "[" + user.getId() + "]-{" +  user.getUserNo() + "}" + pw;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public ResUserVo procLogin(LoginReqVo reqVo)throws Exception  {
		
		ResUserVo result = new ResUserVo();
		final String id = reqVo.getId();
		
		final UserVo user = userMapper.getUserId( id );
		
		if( user == null ) {
			userMapper.insertLoginRecord( reqVo, user, ELoginResult.IdNotFound );
			result.setCodeMessage("E", "다시 시도 하세요.");
			try {Thread.sleep(1000L);} catch (InterruptedException e) {e.printStackTrace();}
			return result;
		}

		if( ! user.isEnabled() ) {
			userMapper.insertLoginRecord( reqVo, user, ELoginResult.Denied );
			result.setCodeMessage("E", "다시 시도 하세요.");
			try {Thread.sleep(1000L);} catch (InterruptedException e) {e.printStackTrace();}
			return result;
		}

		final String dbPw = userMapper.getUserPw( id );
		final String genPw = Sha256.encrypt(this.genPwString( user, reqVo.getPw()));
		if( ! genPw.equals(dbPw) ) {
			userMapper.insertLoginRecord( reqVo, user, ELoginResult.MismatchPw );
			userMapper.updatePwCount(user, false);
			result.setCodeMessage("E", "다시 시도 하세요.");
			try {Thread.sleep(1000L);} catch (InterruptedException e) {e.printStackTrace();}
		}else {
			result.setUser(user);
			userMapper.insertLoginRecord( reqVo, user, ELoginResult.Success );
			userMapper.updatePwCount(user, true);
		}
		
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int insertUser(UserVo user, String pw) throws Exception {
		
		user.setEnabled(false);
		
		final int res = adminMapper.insertUser(user);
		if( res < 1 ) {
			return res;
		}
		
		return this.updateUserPw( user, pw);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int updateUserPw(UserVo user, String pw) throws Exception {
		
		final String genPw = this.genPwString( user, pw );
		
		final int result;
		if( BurrowUtils.isEmpty( userMapper.getUserPw(user.getId()) ) ) {
			result = userMapper.insertUserPw(user, genPw);
		}else {
			result = userMapper.updateUserPw(user, genPw);
		}
		
		return result;
	}
	
	/**
	 * 회원 상태 변경
	 * @param user
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int updateUserEnabled(UserVo user) throws Exception {
		return adminMapper.updateUserEnabled( user );
	}
	
	@Override
	public UserVo getManageUser() {
		
		return userMapper.getUserId(AdminId);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int updateUser(UserVo user) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<UserVo> getUserList() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public UserVo getUserFormIdByProvider(String id) {
		
		final UserVo result;
		if( intervalMap.containsKey(id) ) {
			result = intervalMap.get(id);
		}else {
			result = userMapper.getUserIdBasic(id);
			if( result != null ) {
				intervalMap.put(id, result);
			}
		}
		
		return result; 
	}
}
