package com.utime.burrowNest.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.utime.burrowNest.user.vo.UserVo;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("Admin/User")
public class AdminUserController {
	
	/**
	 * 어드민 관리 페이지
	 * @param user
	 * @return
	 */
	@GetMapping("User.html")
	public String adminUserPage(Model model, UserVo user) {
		return "Admin/User/AdminUserMain";
	}
}

