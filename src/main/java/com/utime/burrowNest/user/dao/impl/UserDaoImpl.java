package com.utime.burrowNest.user.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.utime.burrowNest.common.mapper.CommonMapper;
import com.utime.burrowNest.common.util.BurrowUtils;
import com.utime.burrowNest.common.util.Sha256;
import com.utime.burrowNest.common.vo.EJwtRole;
import com.utime.burrowNest.user.dao.UserDao;
import com.utime.burrowNest.user.mapper.AdminMapper;
import com.utime.burrowNest.user.mapper.UserMapper;
import com.utime.burrowNest.user.vo.ELoginResult;
import com.utime.burrowNest.user.vo.InitInforReqVo;
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
	
	private final String AdminId = "admin";
	
	
	@PostConstruct
	private void construct() {
		try {
			
			// 가계부 메인 데이터
			if( ! common.existTable("BN_ADMIN") ) {
				log.info("BN_ADMIN 생성");
				adminMapper.createAdmin();
			}
			
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
	
	@Override
	public boolean IsInit() {
		
		return adminMapper.IsInit();
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
	
	private UserVo getAdminInstance() {
		
		final UserVo result = new UserVo();
		
		result.setUserNo(0);
		result.setId(AdminId);
		result.setRole(EJwtRole.Admin);
		
		return result;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public ResUserVo procLogin(LoginReqVo reqVo)throws Exception  {
		
		try {Thread.sleep(1000L);} catch (InterruptedException e) {e.printStackTrace();}
		
		ResUserVo result = new ResUserVo();
		final String id = reqVo.getId();
		
		final UserVo user;
		final String dbPw;
		if( AdminId.equals(id) ) {
			user = this.getAdminInstance();
			
			dbPw = adminMapper.getAdminPw();
		}else {
			user = userMapper.getUserId( id );

			if( user == null ) {
				userMapper.insertLoginRecord( reqVo, user, ELoginResult.IdNotFound );
				result.setCodeMessage("E", "다시 시도 하세요.");
				return result;
			}
			
			if( ! user.isEnabled() ) {
				userMapper.insertLoginRecord( reqVo, user, ELoginResult.Denied );
				result.setCodeMessage("E", "다시 시도 하세요.");
				return result;
			}
			
			dbPw = userMapper.getUserPw( id );
		}

		final String genPw = Sha256.encrypt(genPwString( user, reqVo.getPw()));
		if( ! genPw.equals(dbPw) ) {
			userMapper.insertLoginRecord( reqVo, user, ELoginResult.MismatchPw );
			result.setCodeMessage("E", "다시 시도 하세요.");
		}else {
			result.setUser(user);
			userMapper.insertLoginRecord( reqVo, user, ELoginResult.Success );
		}
		
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int saveInitInfo(InitInforReqVo req) throws Exception {

		int res = 0;
		final UserVo user = this.getAdminInstance();
		
		final String genPw = this.genPwString( user, req.getPw() );
		
		final UserVo admin = this.getManageUser();
		if( admin != null ) {
			res += adminMapper.updateAdmin(genPw);
			
			res += this.updateUserPw( admin, genPw);
			return res;
		}
		
		res += adminMapper.insertAdmin(genPw);
		
		if( res < 1 ) {
			return res;
		}
		
		user.setEnabled(true);
		user.setNickname("Owner");
		user.setProfileImg("profile1.svg");
		
		res += adminMapper.insertUser(user);
		return res;
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
}
