package com.utime.burrowNest.dropbox.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.utime.burrowNest.admin.dao.AdminUserDao;
import com.utime.burrowNest.admin.vo.ManageUserVo;
import com.utime.burrowNest.dropbox.service.DropboxOAuthService;
import com.utime.burrowNest.user.vo.UserVo;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("Dropbox")
public class DropboxController {

	private final AdminUserDao repo;
	private final DropboxOAuthService dropboxService;
	
	@GetMapping(path = { "/", "Index.html" })
    public String index(Model model) {
		List<ManageUserVo> userList = repo.userList(null);
        model.addAttribute("members", userList);
        return "DropBox/Dropbox";
    }

    @PostMapping("Members")
    public String addMember(@RequestParam String name) throws Exception {
    	UserVo m = new UserVo();
        m.setNickname(name);
        repo.deleteUser(m);
        return "DropBox/Dropbox";
    }

    @GetMapping("Connect/{id}")
    public String connect(@PathVariable String id) {
        String url = dropboxService.buildAuthorizeUrl(id);
        return "redirect:" + url;
    }
    
	@GetMapping("OAuth/callback")
	public String callback(@RequestParam(required = false) String code, @RequestParam(required = false) String state,
			@RequestParam(required = false) String error) throws Exception {

		if (error != null) {
			return "redirect:/?error=" + error;
		}
		
		dropboxService.exchangeCodeAndSave(state, code);
		return "DropBox/Dropbox";
	}
	
}
