package com.utime.burrowNest.admin.controller;

import java.util.List;

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
import com.utime.burrowNest.common.util.BurrowUtils;
import com.utime.burrowNest.common.vo.EJwtRole;
import com.utime.burrowNest.common.vo.ReturnBasic;
import com.utime.burrowNest.storage.service.StorageService;
import com.utime.burrowNest.storage.vo.BnDirectory;
import com.utime.burrowNest.storage.vo.DirectoryDto;
import com.utime.burrowNest.storage.vo.EAccessType;
import com.utime.burrowNest.user.vo.GroupVo;
import com.utime.burrowNest.user.vo.UserVo;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("Admin/Group")
public class AdminGroupUserController {
	
	private final AdminUserService userService;
	
	private final StorageService storageService;
	
	/**
	 * 그룹 관리 페이지
	 * @param user
	 * @return
	 */
	@GetMapping("Group.html")
	public String adminUserPage(Model model, UserVo user) {
		return "Admin/Group/AdminGroupUserMain";
	}
	
	/**
	 * 그룹 목록 
	 * @param model
	 * @param grName
	 * @return
	 */
	@GetMapping("GroupUserList.layer")
	public String adminUserList(Model model, @RequestParam(name = "grName", required = false) String grName) {
		
		final List<GroupVo> groupList = userService.getUserGroupList();
		
		model.addAttribute("groups", groupList );
		
		return "Admin/Group/AdminGroupUserList";
	}
	
	/**
	 * 그룹 상세
	 * @param model
	 * @param groupNo
	 * @return
	 */
	@GetMapping("GroupItem.layer")
    public String getUserProfile(ModelMap model, @RequestParam(name="groupNo") long groupNo) {
		
		model.addAttribute("item", userService.getGroupByNo(groupNo));
		model.addAttribute("roles", EJwtRole.values() );
		model.addAttribute("accTypes", EAccessType.values() );
        
		return "Admin/Group/GroupUserLayer";
    }
	
	/**
	 * 그룹 저장
	 * @param group
	 * @return
	 */
	@ResponseBody
	@PostMapping("SaveGroup.json")
    public ReturnBasic saveGroup( @RequestBody GroupVo group) {
		
		return userService.saveGroup( group );
    }
	
	/**
	 * 그룹 삭제
	 * @param group
	 * @return
	 */
	@ResponseBody
	@PostMapping("DeleteGroup.json")
    public ReturnBasic deleteGroup( @RequestBody GroupVo group) {
		
		return userService.deleteGroup( group );
    }
	
	/**
	 * 그룹 보유 저장소
	 * @param model
	 * @param groupNo
	 * @return
	 */
	@GetMapping("GroupUserStorageList.layer")
    public String getGroupUserStorageList(ModelMap model, @RequestParam(name="groupNo") long groupNo) {
		
		final GroupVo group = userService.getGroupByNo(groupNo);
		model.addAttribute("group", group);
		
		final List<BnDirectory> list = storageService.getGroupStorageList(groupNo);
		model.addAttribute("list", list);
        
		return "Admin/Group/AdminGroupStorageList";
    }
	
	/**
	 * 저장소 목록을 보여질 팝업창
	 * @param model
	 * @return
	 * @throws Exception 
	 */
	@GetMapping(path = { "AdminGroupSelectPath.layer" })
    public String BeginIntroDirListLayer(UserVo user, ModelMap model) throws Exception {
		
		final List<DirectoryDto> dirList = storageService.getDirectory(user, null);
		
		model.addAttribute("directories", dirList);
		
		return "Admin/Group/AdminGroupSelectPathLayer";
    }
	
	/**
	 * 그룹 저장소 저장
	 * @return
	 */
	@ResponseBody
	@PostMapping("SaveGroupUserStorageList.json")
    public ReturnBasic saveGroupUserStorageList(@RequestParam(name="groupNo") long groupNo, @RequestBody List<BnDirectory> list ) {
		
		return userService.setGroupStorageList(groupNo, list);
    }

}

