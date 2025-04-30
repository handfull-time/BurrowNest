package com.utime.burrowNest.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.utime.burrowNest.user.service.UserService;
import com.utime.burrowNest.user.vo.UserVo;

@Controller
@RequestMapping("User")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping("MyProfile.layer")
    public String getMyUserProfile(ModelMap model, UserVo user) {
		
		model.addAttribute("item", user);
        
		return "User/ProfileLayer";
    }
	
	/**
	 * userNo 회원의 프로파일 이미지
	 * @param userNo
	 * @return
	 */
	@GetMapping("{userNo}/Thumbnail.jpg")
    public ResponseEntity<byte[]> getUserThumbnail(@PathVariable("userNo") int userNo) {
		
		final byte[] image = userService.getThumbnail( userNo );
        
		if( image == null ) {
	    	return ResponseEntity.notFound().build();
	    }
	    
	    return ResponseEntity.ok()
	            .contentType(MediaType.IMAGE_PNG)
	            .contentLength(image.length)
	            .body(image);
    }
    
    /**
     * 현 로그인 사용자의 프로파일 이미지
     * @param user
     * @return
     */
    @GetMapping("Thumbnail.jpg")
    public ResponseEntity<byte[]> getMyThumbnail(UserVo user) {
    	
    	final int userNo = user == null ? 0:user.getUserNo();
		
    	return this.getUserThumbnail( userNo );
    }
	
}

