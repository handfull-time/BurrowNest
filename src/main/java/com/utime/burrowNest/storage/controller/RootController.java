package com.utime.burrowNest.storage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.utime.burrowNest.common.vo.EJwtRole;
import com.utime.burrowNest.storage.service.StorageService;
import com.utime.burrowNest.user.vo.UserVo;

@Controller
public class RootController {
	
	@Autowired
	private StorageService storageService;
	
	/**
	 * Root 
	 * @param model
	 * @return
	 */
	@GetMapping(path = { "/", "Index.html" })
    public String root(UserVo user) {
		
		if( ! storageService.IsInit() ) {
			return "redirect:/Intro/Intro.html";
		}
		
		if( user == null ) {
			return "redirect:/Auth/Login.html";
		}else if( user.getRole() == EJwtRole.Admin ){
			return "redirect:/Admin/User/User.html";
		}else {
			return "redirect:/Dir/Index.html";
		}
    }
	
}

