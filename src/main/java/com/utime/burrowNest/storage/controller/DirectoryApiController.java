package com.utime.burrowNest.storage.controller;

import java.util.List;

//DirectoryApiController.java
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.utime.burrowNest.storage.dto.ChildrenResponse;
import com.utime.burrowNest.storage.dto.DirContextResponse;
import com.utime.burrowNest.storage.dto.DirNodeDto;
import com.utime.burrowNest.storage.service.StorageService;
import com.utime.burrowNest.user.vo.UserVo;

@RestController
@RequestMapping("api/dir")
public class DirectoryApiController {

	private final StorageService storageService;

	public DirectoryApiController(StorageService storageService) {
		this.storageService = storageService;
	}

	// 컨텍스트: node + ancestors + children + breadcrumbs + flags
	@GetMapping("context")
	public DirContextResponse context(@RequestParam(value = "uid", required = false) String uid, UserVo user) {
		
		DirContextResponse ctx = storageService.buildContext(user, uid); // uid 없으면 루트
		
		if (ctx == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
		
		return ctx;
	}

	// 자식 지연 로딩
	@GetMapping("children")
	public ChildrenResponse children(@RequestParam("uid") String uid,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "200") int size, UserVo user) {
		
		ChildrenResponse res = storageService.findChildren(user, uid, page, size, null);
		
		if (res == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
		
		return res;
	}

	// 사용자에게 허용된 최상위 루트들
	@GetMapping("roots")
	public List<DirNodeDto> roots(UserVo user) {
		return storageService.findAllowedRoots(user);
	}
}
