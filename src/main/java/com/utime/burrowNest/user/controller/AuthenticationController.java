package com.utime.burrowNest.user.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.utime.burrowNest.common.util.BurrowUtils;
import com.utime.burrowNest.common.vo.BurrowDefine;
import com.utime.burrowNest.common.vo.ReturnBasic;
import com.utime.burrowNest.storage.service.StorageService;
import com.utime.burrowNest.user.service.AuthService;
import com.utime.burrowNest.user.vo.LoginReqVo;
import com.utime.burrowNest.user.vo.ResUserVo;
import com.utime.burrowNest.user.vo.UserReqVo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("Auth")
public class AuthenticationController {
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	private StorageService storageService;
	
	/**
	 * 메타 처리
	 * @param model
	 * @param redirectUrl 이전 호출 됐던 URL
	 * @return
	 */
	@GetMapping("NoneAuthMeta.html")
    public String noneAuthMetaPage(ModelMap model, @RequestParam(required = false) String redirectUrl) {
		
		model.addAttribute("redirectUrl", redirectUrl);
        
		return "Auth/NoneAuthMeta";
    }
	
	/**
	 * 로그인 화면
	 * @param request
	 * @param model
	 * @return
	 */
	@GetMapping("Login.html")
    public String loginPage( HttpServletRequest request, ModelMap model ) {
		
		if( ! storageService.IsInit() ) {
			return "redirect:/Intro/Intro.html";
		}
		
		model.addAttribute("unique", authService.getNewGenUnique(request) );
		
        return "Auth/Login";
    }
	
	
	@GetMapping("FindUserPw.html")
    public String FindUserPw( HttpServletRequest request, ModelMap model ) {
		
		model.addAttribute("unique", authService.getNewGenUnique(request) );
		
        return "Auth/FindUserPw";
    }
	
	@GetMapping("JoinUser.html")
    public String JoinUser( HttpServletRequest request, ModelMap model ) {
		
		model.addAttribute("unique", authService.getNewGenUnique(request) );
		
        return "Auth/JoinUser";
    }
	
	/**
	 * id 중복인지 검사
	 * @param id
	 * @return
	 */
	@ResponseBody
	@GetMapping("CheckId.json")
    public ResponseEntity<ReturnBasic> checkId( @RequestParam("id") String id) {
    	
    	final ReturnBasic result = authService.checkId(id);
    	
    	return ResponseEntity.ok().body(result);
    }
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param refreshToken
	 * @return
	 */
	@PostMapping("Refresh.json")
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request, HttpServletResponse response, 
    		@CookieValue(BurrowDefine.KeyRefreshToken) String refreshToken) {
    	
    	final ReturnBasic result = authService.refreshAccessToken(request, response, refreshToken);
    	
    	if( result.isError() ) {
    		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
    	}

    	return ResponseEntity.ok().body(result);
    }
	
	@GetMapping("Check.json")
    public ResponseEntity<?> CheckAccessToken(HttpServletRequest request, HttpServletResponse response, 
    		@CookieValue(BurrowDefine.KeyRefreshToken) String refreshToken) {
    	
    	final ReturnBasic result = authService.refreshAccessToken(request, response, refreshToken);
    	
    	if( result.isError() ) {
    		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
    	}

    	return ResponseEntity.ok().body(result);
    }

	/**
	 * 로그인 동작. 
	 * 로그인 후 필요 정보는 Cookie에 존재함.
	 * @param request
	 * @param response
	 * @param reqVo
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
    @PostMapping("Login.json")
    public ResponseEntity<?> login( HttpServletRequest request, HttpServletResponse response, @RequestBody LoginReqVo reqVo) throws Exception {
    	
		reqVo.setIp( BurrowUtils.getRemoteAddress( request ) );
		reqVo.setUserAgent( request.getHeader(HttpHeaders.USER_AGENT) );
		
    	final ResUserVo result = authService.procLogin(request, response, reqVo);
    	
    	if( result.isError() ) {
    		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", ""));
    	}
    	
    	// 로그인 전에 호출된 주소가 있다면 로그인 후 해당 주소로 이동한다.
    	String url;
    	final Object obj = request.getSession().getAttribute(BurrowDefine.KeyBeforeUri);
    	if( obj != null ) {
    		request.getSession().removeAttribute(BurrowDefine.KeyBeforeUri);
    		url = (String)obj;
    	}else {
//    		url = request.getContextPath() + "/Dir/Index.html";
    		url = request.getContextPath() + "/";
    	}
    	
    	result.setUser(null);
    	
    	result.setMessage(url);
    	
    	return ResponseEntity.ok().body(result);
    }

    /**
     * 로그아웃
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
	@ResponseBody
	@GetMapping("Logout.json")
    public ResponseEntity<?> userLogout( HttpServletRequest request, HttpServletResponse response )throws Exception {
		
		authService.logout(request, response);
    	
    	return ResponseEntity.ok().body(new ReturnBasic());
    }
	
	/**
	 * 회원 가입
	 * @param request
	 * @param response
	 * @param reqVo
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
    @PostMapping("JoinUser.json")
    public ResponseEntity<ReturnBasic> JoinUser( HttpServletRequest request, UserReqVo reqVo) throws Exception {
    	
		reqVo.setIp( BurrowUtils.getRemoteAddress( request ) );
		reqVo.setUserAgent( request.getHeader(HttpHeaders.USER_AGENT) );
		
    	final ReturnBasic result = authService.procJoinUser(reqVo);
    	
    	if( result.isError() ) {
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    	}
    	
    	return ResponseEntity.ok().body(result);
	}
	
	/**
	 * 비번 변경
	 * @param request
	 * @param reqVo
	 * @return
	 */
	@PostMapping("ConvertUserPw.json")
    public ResponseEntity<ReturnBasic> convertUserPw( HttpServletRequest request, LoginReqVo reqVo )throws Exception {
		
		reqVo.setIp( BurrowUtils.getRemoteAddress( request ) );
		
		final ReturnBasic result = authService.convertUserPw(reqVo);
    	
    	return ResponseEntity.ok().body(result);
    }

	/**
	 * 비밀번호 찾기
	 * @param request
	 * @param reqVo
	 * @return
	 */
	@PostMapping("FindUserPw.layer")
    public String findUserPwLayer( HttpServletRequest request, ModelMap model, UserReqVo reqVo ) {
		
		reqVo.setIp( BurrowUtils.getRemoteAddress( request ) );
		
		final ReturnBasic result = authService.findUserPw(reqVo);
		
		model.addAttribute("item", result);
    	
    	return "Auth/FindUserPwLayer";
    }
	
	
}

