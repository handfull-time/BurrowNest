package com.utime.burrowNest.user.dao.impl;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.utime.burrowNest.common.mapper.CommonMapper;
import com.utime.burrowNest.common.util.BurrowUtils;
import com.utime.burrowNest.common.util.CacheIntervalMap;
import com.utime.burrowNest.common.util.Sha256;
import com.utime.burrowNest.common.vo.BinResultVo;
import com.utime.burrowNest.common.vo.EJwtRole;
import com.utime.burrowNest.storage.vo.EAccessType;
import com.utime.burrowNest.user.dao.UserDao;
import com.utime.burrowNest.user.mapper.UserMapper;
import com.utime.burrowNest.user.vo.ELoginResult;
import com.utime.burrowNest.user.vo.GroupVo;
import com.utime.burrowNest.user.vo.LoginReqVo;
import com.utime.burrowNest.user.vo.ResUserVo;
import com.utime.burrowNest.user.vo.UserVo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
class UserDaoImpl implements UserDao {
	
	private final String KeyGroupAdmin = "Admin";
	private final String KeyGroupUnsel = "Unselected";
	
	@Autowired
	private CommonMapper common;
	
	@Autowired
	private UserMapper userMapper;
	
	@Value("${security.pwSaltKey}")
    private String saltKey;
	
	final CacheIntervalMap<String, UserVo> intervalMap = new CacheIntervalMap<>(10L, TimeUnit.MINUTES);
	
	@Override
	public boolean isInit() {
		return common.existTable("BN_USER_GROUP") && common.existTable("BN_USER");
	}

	@Override
	public int initUserTable() throws Exception {
		int result = 0;
		if( ! common.existTable("BN_USER_GROUP") ) {
			log.info("BN_USER_GROUP 생성");
			result += userMapper.createUserGroup();
//			result += common.createUniqueIndex("BN_USER_GROUP_NAME_INDX", "BN_USER_GROUP", "NAME");
			result += this.insertBasicGroup();
		}

		if( ! common.existTable("BN_USER") ) {
			log.info("BN_USER 생성");
			result += userMapper.createUser();
//			result += common.createUniqueIndex("BN_USER_ID_INDX", "BN_USER", "ID");
		}

		if( ! common.existTable("BN_USER_PROFILE_IMG") ) {
			log.info("BN_USER_PROFILE_IMG 생성");
			result += userMapper.createProfileImg();
		}
		
		if( ! common.existTable("BN_USER_LOGIN_RECORD") ) {
			log.info("BN_USER_LOGIN_RECORD 생성");
			result += userMapper.createLoginRecord();
			result += common.createIndex("BN_USER_LOGIN_RECORD_IP_INDX", "BN_USER_LOGIN_RECORD", "IP");
			result += common.createIndex("BN_USER_LOGIN_RECORD_STATUS_INDX", "BN_USER_LOGIN_RECORD", "STATUS");
			result += common.createIndex("BN_USER_LOGIN_RECORD_USER_NO_INDX", "BN_USER_LOGIN_RECORD", "USER_NO");
			result += common.createIndex("BN_USER_LOGIN_RECORD_REG_DATE_INDX", "BN_USER_LOGIN_RECORD", "REG_DATE");
		}

		if( ! common.existTable("BN_USER_PW") ) {
			log.info("BN_USER_PW 생성");
			result += userMapper.createUserPw();
			
		}
		return result;
	}

	/**
	 * 관리자 그룹 추가
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor = Exception.class)
	private int insertBasicGroup() throws Exception {
		int result = 0;
		
		{
			final GroupVo group = new GroupVo();
			group.setEnabled(true);
			group.setName( KeyGroupAdmin );
			group.setRole(EJwtRole.Admin);
			group.setAccType(EAccessType.All);
			group.setNote("관리자 그룹");
			
			result += this.userMapper.insertGroup(group);
		}
		
		{
			final GroupVo group = new GroupVo();
			group.setEnabled(true);
			group.setName( KeyGroupUnsel );
			group.setRole(EJwtRole.User);
			group.setAccType(EAccessType.None);
			group.setNote("처음 회원 가입시 포함될 그룹");
			
			result += this.userMapper.insertGroup(group);
		}
		
		return result;
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
		
		final UserVo user = userMapper.selectUserId( id );
		
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

		final String dbPw = userMapper.selectUserPw( id );
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
	public int addUser(LoginReqVo reqVo, UserVo user, String pw, byte [] profileImg) throws Exception {
		
		int res = userMapper.insertUser(user);
		if( res < 1 ) {
			return res;
		}
		
		res += userMapper.insertUserProfileImg(user.getUserNo(), profileImg);
		res += this.updateUserPw( user, pw);
		
		userMapper.insertLoginRecord( reqVo, user, ELoginResult.Join );
		
		return res;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int updateUserPw(UserVo user, String pw) throws Exception {
		
		final String genPw = this.genPwString( user, pw );
		
		final int result;
		if( BurrowUtils.isEmpty( userMapper.selectUserPw(user.getId()) ) ) {
			result = userMapper.insertUserPw(user, genPw);
		}else {
			result = userMapper.updateUserPw(user, genPw);
		}
		
		return result;
	}
	
	@Override
	public GroupVo getAdminGroup() {
		
		return userMapper.selectGroupByName(this.KeyGroupAdmin);
	}
	
	@Override
	public GroupVo getNormalGroup() {
		
		return userMapper.selectGroupByName(this.KeyGroupUnsel);
	}
	
	@Override
	public UserVo getManageUser() {
		
		return userMapper.selectUserId(AdminId);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int updateUser(UserVo user, byte [] profileImg) throws Exception {
		int result = userMapper.updateUser(user);
		
		if( profileImg != null ) {
			result += userMapper.updateUserProfileImg(user.getUserNo(), profileImg);
		}
			
		return result;
	}
	
	@Override
	public UserVo getUserFormId(String id) {
		return userMapper.selectUserIdBasic(id);
	}
	
	@Override
	public UserVo getUserFormIdByProvider(String id) {
		
		final UserVo result;
		if( intervalMap.containsKey(id) ) {
			result = intervalMap.get(id);
		}else {
			result = userMapper.selectUserIdBasic(id);
			if( result != null ) {
				intervalMap.put(id, result);
			}
		}
		
		return result; 
	}
	
	@Override
	public UserVo findUser(String id, String genUserUniqueHashing) {
		
		return userMapper.findUser(id, genUserUniqueHashing);
	}
	
	@Override
	public boolean checkId(String id) {
		return userMapper.checkId( id );
	}
	
	@Override
	public BinResultVo getProfileImg(long userNo) {
		
		final BinResultVo result = userMapper.selectProfileImg( userNo );
		
		return result;
	}
}
