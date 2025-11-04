package com.utime.burrowNest.user.controller;

import java.time.Duration;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import com.utime.burrowNest.common.util.BurrowUtils;
import com.utime.burrowNest.common.vo.ReturnBasic;
import com.utime.burrowNest.user.service.AuthService;
import com.utime.burrowNest.user.vo.ThumbnailData;
import com.utime.burrowNest.user.vo.UserReqVo;
import com.utime.burrowNest.user.vo.UserVo;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("User")
@RequiredArgsConstructor
public class UserController {
	
	private final AuthService authService;
	
	@GetMapping("MyProfile.layer")
    public String getMyUserProfile(HttpServletRequest request, ModelMap model, UserVo user) {
		
		model.addAttribute("item", user);
		model.addAttribute("unique", authService.getNewGenUnique(request) );
        
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
    	
        final ThumbnailData data = authService.getThumbnail(userNo); // bytes + lastModified
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
    
    @ResponseBody
    @PostMapping("UpdateUser.json")
    public ResponseEntity<ReturnBasic> UpdateUser(UserVo user, UserReqVo reqVo) throws Exception {
    	
    	final ReturnBasic result = authService.procUpdateUser(user, reqVo);
    	
    	if( result.isError() ) {
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    	}
    	
    	return ResponseEntity.ok().body(result);
	}
}

