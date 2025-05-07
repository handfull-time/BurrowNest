package com.utime.burrowNest.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.utime.burrowNest.admin.service.AdminUserService;
import com.utime.burrowNest.common.vo.EJwtRole;
import com.utime.burrowNest.common.vo.ReturnBasic;
import com.utime.burrowNest.user.vo.UserVo;

@Controller
@RequestMapping("Admin/Group")
public class AdminGroupUserController {
	
	@Autowired
	private AdminUserService userService;
	
	/**
	 * 어드민 관리 페이지
	 * @param user
	 * @return
	 */
	@GetMapping("Group.html")
	public String adminUserPage(Model model, UserVo user) {
		return "Admin/Group/AdminGroupUserMain";
	}
	
	
	@GetMapping("GroupUserList.layer")
	public String adminUserList(Model model, @RequestParam(name = "grName", required = false) String grName) {
		
		model.addAttribute("groups", userService.getUserGroupListAll(grName) );
		
		return "Admin/Group/AdminGroupUserList";
	}
	
	@GetMapping("GroupItem.layer")
    public String getUserProfile(ModelMap model, @RequestParam(name="groupNo") int groupNo) {
		
		model.addAttribute("item", userService.getUserFromNo(groupNo));
		model.addAttribute("roles", EJwtRole.values() );
        
		return "Admin/Group/GroupUserLayer";
    }
	
	@ResponseBody
	@PostMapping("SaveGroupUser.json")
    public ReturnBasic saveUser( @RequestBody UserVo user) {
		
		return userService.saveUser( user );
    }
}

