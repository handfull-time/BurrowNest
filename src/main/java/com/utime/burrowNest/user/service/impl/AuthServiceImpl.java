package com.utime.burrowNest.user.service.impl;

import java.security.KeyPair;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.utime.burrowNest.common.jwt.JwtProvider;
import com.utime.burrowNest.common.util.BurrowUtils;
import com.utime.burrowNest.common.util.CacheIntervalMap;
import com.utime.burrowNest.common.util.RsaEncDec;
import com.utime.burrowNest.common.vo.ReturnBasic;
import com.utime.burrowNest.user.dao.UserDao;
import com.utime.burrowNest.user.service.AuthService;
import com.utime.burrowNest.user.vo.InitInforReqVo;
import com.utime.burrowNest.user.vo.LoginReqVo;
import com.utime.burrowNest.user.vo.ReqUniqueVo;
import com.utime.burrowNest.user.vo.ResUserVo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
class AuthServiceImpl implements AuthService {
	
	final CacheIntervalMap<String, String> intervalMap = new CacheIntervalMap<>(10L, TimeUnit.MINUTES);
	
	@Autowired
	private JwtProvider jwtUtil;
	
	@Autowired
	private UserDao userDao;
	
	@Override
	public boolean IsInit() {
		
		return userDao.IsInit();
	}
	
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
		
		final ResUserVo result = userDao.procLogin(reqVo);
		
		log.info("로그인 결과 : {}", result);
		
		if( ! result.isError() ) {
			this.validationRemove(reqVo);
			
			jwtUtil.procLogin( request, response, result.getUser() );
		}
		
		return result;		
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
	
	@Override
	public ReturnBasic saveInitInfor(InitInforReqVo req) {
		log.info("초기화 시도 : {}", req);
		
		if( ! this.validation(req) ) {
			return new ResUserVo("E", "Invalid credentials");
		}
		
		final String pw = this.convertEncPw( req, req.getPw() );
		
		if( pw == null ){
			return new ResUserVo("E", "Invalid credentials");
		}
		req.setPw(pw);
		
		try {
			userDao.saveInitInfo( req );
		} catch (Exception e) {
			e.printStackTrace();
			return new ResUserVo("E", "Invalid credentials");
		}
		
		return null;
	}
	
}
