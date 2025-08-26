package com.utime.burrowNest.user.controller;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import com.utime.burrowNest.common.util.BurrowUtils;
import com.utime.burrowNest.user.service.AuthService;
import com.utime.burrowNest.user.vo.ThumbnailData;
import com.utime.burrowNest.user.vo.UserVo;

@Controller
@RequestMapping("User")
public class UserController {
	
	@Autowired
	private AuthService userService;
	
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
	@GetMapping("{userNo}/Profile.img")
    public ResponseEntity<byte[]> getUserThumbnail( @PathVariable long userNo, 
    		WebRequest webRequest ) {
    	
        final ThumbnailData data = userService.getThumbnail(userNo); // bytes + lastModified
        if (data == null ) return ResponseEntity.notFound().build();
        
        final byte [] dataBytes = data.bytes();

        final String etag = "\"" + DigestUtils.md5DigestAsHex(dataBytes) + "\"";
        final long lastModified = data.lastModified() != null ? data.lastModified().longValue() : -1;

        // ETag 또는 Last-Modified 중 하나라도 일치하면 304
        if (webRequest.checkNotModified(etag, lastModified)) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }

        final MediaType mediaType = BurrowUtils.detectImageType(dataBytes);

        return ResponseEntity.ok()
                .eTag(etag)
                .lastModified(lastModified)
                // 바뀔 수 있으니 재검증 중심. (정말 드물게 바뀐다면 maxAge도 고려)
//                .cacheControl(CacheControl.noCache().cachePublic())
                .cacheControl(CacheControl.maxAge(Duration.ofDays(30)).cachePublic())
                .contentType(mediaType)
                .contentLength(dataBytes.length)
                .body(dataBytes);
    }
    
    /**
     * 현 로그인 사용자의 프로파일 이미지
     * @param user
     * @return
     */
    @GetMapping("Profile.img")
    public ResponseEntity<byte[]> getMyThumbnail(UserVo user, WebRequest webRequest) {
    	
    	final long userNo = user == null ? 0:user.getUserNo();
		
    	return this.getUserThumbnail( userNo, webRequest );
    }
}

