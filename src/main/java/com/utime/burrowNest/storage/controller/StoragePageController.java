package com.utime.burrowNest.storage.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.utime.burrowNest.common.vo.ReturnBasic;
import com.utime.burrowNest.storage.service.StorageService;
import com.utime.burrowNest.storage.vo.PasteItem;
import com.utime.burrowNest.storage.vo.RenameItem;
import com.utime.burrowNest.storage.vo.StorageIOItem;
import com.utime.burrowNest.user.vo.UserVo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("Storage")
public class StoragePageController {
	
	final StorageService storageService;
	
	/**
	 * 붙여 넣기
	 */
	@PostMapping("Paste.json")
	public ReturnBasic pasteStorage(UserVo user, @RequestBody PasteItem pasteItem) {
		
		ReturnBasic result;
		
		try {
			result = storageService.pasteStorage(user, pasteItem);
		} catch (Exception e) {
			log.error("StoragePageController.pasteStorage", e);
			result = new ReturnBasic();
			result.setCodeMessage("", e.getMessage());
		}
		
		return result;
	}

	/**
	 * 삭제
	 */
	@PostMapping("Delete.json")
	public ReturnBasic deleteStorage(UserVo user, @RequestBody List<StorageIOItem> delItems) {
		
		ReturnBasic result;
		
		try {
			result = storageService.deleteStorage(user, delItems);
		} catch (Exception e) {
			log.error("StoragePageController.deleteStorage", e);
			result = new ReturnBasic();
			result.setCodeMessage("", e.getMessage());
		}
		
		return result;
	}
	
	/**
	 * 이름 변경
	 */
	@PostMapping("Rename.json")
	public ReturnBasic renameStorage(UserVo user, @RequestBody RenameItem renameItem) {
		
		ReturnBasic result;
		
		try {
			result = storageService.renameStorage(user, renameItem);
		} catch (Exception e) {
			log.error("StoragePageController.renameStorage", e);
			result = new ReturnBasic();
			result.setCodeMessage("", e.getMessage());
		}
		
		return result;
	}

	/**
	 * 새 폴더 생성
	 */
	@PostMapping("NewFolder.json")
	public ReturnBasic newFolderStorage(UserVo user, @RequestBody RenameItem newFolderItem) {
		
		ReturnBasic result;
		
		try {
			result = storageService.newFolderStorage(user, newFolderItem);
		} catch (Exception e) {
			log.error("StoragePageController.newFolderStorage", e);
			result = new ReturnBasic();
			result.setCodeMessage("", e.getMessage());
		}
		
		return result;
	}
}
