package com.utime.burrowNest.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.utime.burrowNest.user.service.UserService;
import com.utime.burrowNest.user.vo.UserVo;

@Controller
@RequestMapping("User")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping("MyProfile.layer")
    public String getMyUserProfile(ModelMap model, UserVo user) {
		
		model.addAttribute("item", user);
        
		return "User/ProfileLayer";
    }
	
	
}

