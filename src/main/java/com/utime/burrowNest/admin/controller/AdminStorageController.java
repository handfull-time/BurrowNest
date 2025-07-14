package com.utime.burrowNest.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.utime.burrowNest.admin.service.AdminUserService;
import com.utime.burrowNest.storage.service.StorageService;
import com.utime.burrowNest.storage.vo.BnDirectory;
import com.utime.burrowNest.user.vo.GroupVo;
import com.utime.burrowNest.user.vo.UserVo;

@Controller
@RequestMapping("Admin/Storage")
public class AdminStorageController {
	
	@Autowired
	private AdminUserService userService;
	
	@Autowired
	private StorageService storageService;
	
	/**
	 * 그룹 Storeage 목록
	 * @param user
	 * @return
	 */
	@GetMapping("Storage.html")
	public String adminStoragePage(Model model, UserVo user) {
		
		model.addAttribute("directories", storageService.getGroupStorageList(user) );
		
		return "Admin/Storage/AdminStorageMain";
	}
	
	@ResponseBody
	@GetMapping("GroupStorageList.json")
	public List<BnDirectory> GroupStorageList(@RequestParam("GroupNo") int groupNo) {
		return null;
	}
}

