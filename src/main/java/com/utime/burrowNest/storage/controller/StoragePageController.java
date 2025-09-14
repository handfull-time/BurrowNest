package com.utime.burrowNest.storage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.utime.burrowNest.user.vo.UserVo;

@Controller
@RequestMapping("storage")
public class StoragePageController {
	
	@GetMapping("Path.html")
	public String path(ModelMap model, @RequestParam(value = "uid", required = false) String uid, UserVo user) {
		model.addAttribute("selectedUid", uid); // JS가 초기 진입 uid 사용
		model.addAttribute("user", user); // 기존 Header 등에서 사용
		return "Storage/StorageMain";
	}
}
