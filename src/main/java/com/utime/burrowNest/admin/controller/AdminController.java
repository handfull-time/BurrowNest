package com.utime.burrowNest.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.utime.burrowNest.user.vo.UserVo;

@Controller
@RequestMapping("Admin")
public class AdminController {
	
	/**
	 * 어드민 관리 페이지
	 * @param user
	 * @return
	 */
	@GetMapping("AdminHome.html")
	public String adminPage(UserVo user) {
		return "Admin/User/AdminUserMain";
	}
}

