package com.utime.burrowNest.storage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.utime.burrowNest.user.service.AuthService;
import com.utime.burrowNest.user.vo.UserVo;

@Controller
public class RootController {
	
	@Autowired
	private AuthService authService;
	
	/**
	 * Root 
	 * @param model
	 * @return
	 */
	@GetMapping(path = { "/", "Index.html" })
    public String root(UserVo user) {
		
		if( ! authService.IsInit() ) {
			return "redirect:/Intro/Intro.html";
		}
		
		if( user == null ) {
			return "redirect:/Auth/Login.html";
		}else {
			return "redirect:/Dir/Index.html";	
		}		
    }
	
}

