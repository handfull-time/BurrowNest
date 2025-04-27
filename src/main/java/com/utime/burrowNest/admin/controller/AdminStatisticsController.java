package com.utime.burrowNest.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.utime.burrowNest.user.vo.UserVo;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("Admin/Statistics")
public class AdminStatisticsController {
	
	/**
	 * 어드민 관리 페이지
	 * @param user
	 * @return
	 */
	@GetMapping("Statistics.html")
	public String adminStatisticsPage(HttpServletRequest request, Model model, UserVo user) {
		model.addAttribute("RequestURI", request.getRequestURI());
		return "Admin/Statistics/AdminStatisticsMain";
	}
}

