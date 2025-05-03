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
@RequestMapping("Admin/User")
public class AdminUserController {
	
	@Autowired
	private AdminUserService userService;
	
	/**
	 * 어드민 관리 페이지
	 * @param user
	 * @return
	 */
	@GetMapping("User.html")
	public String adminUserPage(Model model, UserVo user) {
		return "Admin/User/AdminUserMain";
	}
	
	
	@GetMapping("UserList.layer")
	public String adminUserList(Model model, @RequestParam(name = "id", required = false) String id) {
		
		model.addAttribute("users", userService.userList(id) );
		
		return "Admin/User/AdminUserList";
	}
	
	@GetMapping("Profile.layer")
    public String getUserProfile(ModelMap model, @RequestParam(name="userNo") int userNo) {
		
		model.addAttribute("item", userService.getUserFromNo(userNo));
		model.addAttribute("roles", EJwtRole.values() );
        
		return "Admin/User/UserLayer";
    }
	
	@ResponseBody
	@PostMapping("SaveUser.json")
    public ReturnBasic saveUser( @RequestBody UserVo user) {
		
		return userService.saveUser( user );
    }
	
	@ResponseBody
	@PostMapping("DeleteUser.json")
    public ReturnBasic deleteUser(@RequestBody UserVo user) {
		
		return userService.deleteUser( user );
    }
}

