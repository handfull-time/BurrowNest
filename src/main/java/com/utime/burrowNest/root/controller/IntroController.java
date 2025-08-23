package com.utime.burrowNest.root.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.utime.burrowNest.common.util.BurrowUtils;
import com.utime.burrowNest.common.vo.ReturnBasic;
import com.utime.burrowNest.storage.service.StorageService;
import com.utime.burrowNest.user.service.AuthService;
import com.utime.burrowNest.user.vo.ResUserVo;
import com.utime.burrowNest.user.vo.UserReqVo;
import com.utime.burrowNest.user.vo.UserVo;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("Intro")
@RequiredArgsConstructor
public class IntroController {
	
	private final AuthService authService;
	
	private final StorageService storageService;
	
	/**
	 * 인트로 페이지
	 * @param request
	 * @param model
	 * @param user
	 * @return
	 */
	@GetMapping(path = { "Intro.html" })
    public String BeginIntro(HttpServletRequest request, ModelMap model, UserVo user) {

		if( ! authService.IsInit() ) {
			model.addAttribute("unique", authService.getNewGenUnique(request) );
			return "Intro/Infor";
		}
		
		return "redirect:/";
    }
	
	/**
	 * 초기 정보를 저장한다.
	 * @param request
	 * @param req
	 * @return
	 */
	@ResponseBody
	@PostMapping(path = { "SaveInit.json" })
    public ReturnBasic SaveInitinfor(HttpServletRequest request, UserReqVo req) {
		
		req.setIp( BurrowUtils.getRemoteAddress( request ) );
		req.setUserAgent( request.getHeader(HttpHeaders.USER_AGENT) );
		
		final ResUserVo userRes = authService.saveInitInfor(req);
		log.info(userRes.toString());
		if( userRes.isError() ) {
			return userRes;
		}
		
		return storageService.saveRootStorage( userRes.getUser() );
    }
	
	
	
}

