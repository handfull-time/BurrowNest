package com.utime.burrowNest.user.service.impl;

import java.io.IOException;
import java.security.KeyPair;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.utime.burrowNest.common.jwt.JwtProvider;
import com.utime.burrowNest.common.util.BurrowUtils;
import com.utime.burrowNest.common.util.CacheIntervalMap;
import com.utime.burrowNest.common.util.RsaEncDec;
import com.utime.burrowNest.common.util.Sha256;
import com.utime.burrowNest.common.vo.EJwtRole;
import com.utime.burrowNest.common.vo.ReturnBasic;
import com.utime.burrowNest.user.dao.UserDao;
import com.utime.burrowNest.user.service.AuthService;
import com.utime.burrowNest.user.vo.GroupVo;
import com.utime.burrowNest.user.vo.LoginReqVo;
import com.utime.burrowNest.user.vo.ReqUniqueVo;
import com.utime.burrowNest.user.vo.ResUserVo;
import com.utime.burrowNest.user.vo.UserReqVo;
import com.utime.burrowNest.user.vo.UserVo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
class AuthServiceImpl implements AuthService {
	
	private final CacheIntervalMap<String, String> intervalMap = new CacheIntervalMap<>(10L, TimeUnit.MINUTES);
	
	private final JwtProvider jwtUtil;
	
	private final UserDao userDao;
	
	@Value("${security.pwSaltKey}")
    private String saltKey;
	
	/**
	 * Interval 에 추가.
	 * @param value
	 * @return 추가 key
	 */
	private String inputInterval( String value ) {
		
		if( value == null ) {
			return null;
		}
		
		final UUID guid = UUID.randomUUID();
		 
		final String result = guid.toString();
		
		this.intervalMap.put(result, value);
		
		log.info("interval 추가: {} - {}", result, value );

		return result;
	}
	
	@Override
	public boolean IsInit() {
		return userDao.isInit();
	}
	
	@Override
	public ReqUniqueVo getNewGenUnique(HttpServletRequest request) {
		
		final ReqUniqueVo result = new ReqUniqueVo();
		result.setToken( this.inputInterval( BurrowUtils.getRemoteAddress( request ) ) );
		
		final KeyPair pair = RsaEncDec.generateRSAKeyPair();
		result.setPublicKey( RsaEncDec.getPulicKeyScript(pair) );
		result.setRsaId( this.inputInterval( RsaEncDec.getPrivateKey(pair) ) );
		
		return result;
	}
	
	/**
	 * 유효성 검사
	 * @param reqVo
	 * @return true: 옳은 데이터
	 */
	private boolean validation(ReqUniqueVo reqVo) {
		final String key = reqVo.getToken();
		if( key == null ) {
			log.warn("⚠️ interval Key 없음: {} ", key );
			return false;
		}
		
		String ip = intervalMap.get(key);
		if( ip == null ) {
			log.warn("⚠️ interval Key-value 없음: {} ", reqVo.getToken() );
			return false;
		}
		
		if( ! ip.equals(reqVo.getIp()) ) {
			log.warn("⚠️ interval Value 불일치 : {} - {} ", ip, reqVo.getIp() );
			return false;
		}
		
		return true;
	}
	
	/**
	 * 암호 RSA 복호화
	 * @param reqVo
	 * @return
	 */
	private String convertEncPw( ReqUniqueVo reqVo, String encPw ) {
		
		if( BurrowUtils.isEmpty(encPw)) {
			return null;
		}
		
		final String privateKey = intervalMap.get(reqVo.getRsaId());
		if( BurrowUtils.isEmpty(privateKey)) {
			return null;
		}
		
		final String pw = RsaEncDec.rsaDecode( encPw, privateKey);
		
		return pw;
	}
	
	/**
	 * 유효성 검사 키 삭제.
	 * @param reqVo
	 */
	private void validationRemove(LoginReqVo reqVo) {
		this.intervalMap.remove(reqVo.getToken());
		this.intervalMap.remove(reqVo.getRsaId());
	}

	@Override
	public ResUserVo procLogin(HttpServletRequest request, HttpServletResponse response, LoginReqVo reqVo) {
		
		log.info("로그인 시도 : {}", reqVo);
		
		if( ! this.validation(reqVo) ) {
			return new ResUserVo("E", "Invalid credentials");
		}
		
		final String pw = this.convertEncPw( reqVo, reqVo.getPw() );
		
		if( pw == null ){
			return new ResUserVo("E", "Invalid credentials");
		}
		reqVo.setPw(pw);		
		
		ResUserVo result;
		try {
			result = userDao.procLogin(reqVo);
		} catch (Exception e) {
			log.error("", e);
			return new ResUserVo("E", "Invalid credentials");
		}
		
		log.info("로그인 결과 : {}", result);
		
		if( ! result.isError() ) {
			this.validationRemove(reqVo);
			
			jwtUtil.procLogin( request, response, result.getUser() );
		}
		
		return result;		
	}
	
	private ResUserVo procJoinUser( UserReqVo reqVo, GroupVo group, boolean enabled, EJwtRole role ) {
		log.info("초기화 시도 : {}", reqVo);
		
		if( ! this.validation(reqVo) ) {
			return new ResUserVo("E", "Invalid credentials");
		}
		
		final String pw = this.convertEncPw( reqVo, reqVo.getPw() );
		
		if( pw == null ){
			return new ResUserVo("E", "Invalid credentials");
		}
		
		byte[] profileImg;
		try {
			profileImg = BurrowUtils.encodeImageToByteArray( reqVo.getProfileImg().getInputStream() );
		} catch (IOException e) {
			log.error("", e);
			return new ResUserVo("E", e.getMessage());
		}
		
		final UserVo user = new UserVo();
		user.setUserNo(-1);
		user.setEnabled(enabled);
		user.setGroup(group);
		user.setId(reqVo.getId());
		user.setNickname(reqVo.getNickname());
		user.setRole(role);
		user.setAuthHint( this.genUserUniqueHashing( reqVo ) );
		
		final ResUserVo result = new ResUserVo();
		try {
			userDao.addUser(reqVo, user, pw, profileImg);
			this.validationRemove(reqVo);
			result.setUser(user);
		} catch (Exception e) {
			log.error("", e);
			result.setCodeMessage("E", e.getMessage());
		}
		
		return result;
	}
	
	@Override
	public ReturnBasic procJoinUser( UserReqVo reqVo) {
		
		final String id = reqVo.getId().toLowerCase();
		if( id.indexOf("admin") > -1 ) {
			return new ReturnBasic("E", "부적합한 id(key: admin)");
		}
		
		return this.procJoinUser( reqVo, this.userDao.getNormalGroup(), false, EJwtRole.User);
	}
	
	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response) {
		jwtUtil.procLogout( request, response);
	}
	
	@Override
	public ReturnBasic refreshAccessToken(HttpServletRequest request, HttpServletResponse response,
			String refreshToken) {
		
		if( BurrowUtils.isEmpty(refreshToken)) {
			return new ReturnBasic("E", "Token is empty");
		}
		
		return jwtUtil.procRefresh( request, response, refreshToken );
	}
	
	/**
	 * 계정 찾기 정보 해싱
	 * @param user
	 * @return
	 */
	private String genUserUniqueHashing( UserReqVo user ) {
		return Sha256.encrypt(user.getId() + saltKey + user.getMyNumber() + user.getMyRainbow() + user.getMySeason());
	}
	
	@Override
	public ResUserVo saveInitInfor(UserReqVo req) {
		
		try {
			// 최초 회원 관련 테이블 생성
			this.userDao.initTable();
		} catch (Exception e) {
			log.error("", e);
			return new ResUserVo("E", e.getMessage());
		}
		
		final ResUserVo result = this.procJoinUser( req, this.userDao.getAdminGroup(), true, EJwtRole.Admin); 
		
		return result; 
	}
	
	@Override
	public ReturnBasic findUserPw(UserReqVo reqVo) {
		
		if( ! this.validation(reqVo) ) {
			return new ReturnBasic("E", "Invalid credentials");
		}
		
		final UserVo user = userDao.findUser( reqVo.getId(), this.genUserUniqueHashing( reqVo ) );
		if( user == null ) {
			return new ReturnBasic("E", "Invalid credentials");
		}
		
		final ReturnBasic result = new ReturnBasic();
		
		final String key = inputInterval( user.getId() );
		result.setMessage(key);
		return result;
	}
	
	@Override
	public ReturnBasic convertUserPw(LoginReqVo reqVo) {
		
		final ReturnBasic result = new ReturnBasic();
		
		final String id = intervalMap.get(reqVo.getToken());
		if( id == null ) {
			result.setCodeMessage("E", "일치 데이터 없음");
			return result;
		}
		
		final String pw = this.convertEncPw( reqVo, reqVo.getPw() );
		if( pw == null ){
			return result.setCodeMessage("E", "Invalid credentials");
		}
		
		final UserVo user = userDao.getUserFormId(id);
		if( user == null ) {
			return result.setCodeMessage("E", "Invalid credentials");
		}
		
		try {
			userDao.updateUserPw(user, pw);
		} catch (Exception e) {
			log.error("", e);
			result.setCodeMessage("E", e.getMessage());
		}
		
		this.validationRemove(reqVo);

		return result;
	}
	
	@Override
	public ReturnBasic checkId(String id) {
		
		final ReturnBasic result = new ReturnBasic();
		if( userDao.checkId(id) ) {
			result.setCodeMessage("E", "사용 중");
		}
		
		return result;
	}
	
}
