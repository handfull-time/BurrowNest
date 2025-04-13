package com.utime.burrowNest.storage.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.utime.burrowNest.common.util.BurrowUtils;
import com.utime.burrowNest.common.vo.ReturnBasic;
import com.utime.burrowNest.storage.vo.DirectoryDto;
import com.utime.burrowNest.user.service.AuthService;
import com.utime.burrowNest.user.service.UserService;
import com.utime.burrowNest.user.vo.InitInforReqVo;
import com.utime.burrowNest.user.vo.UserVo;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("Intro")
public class IntroController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AuthService authService;
	
	/**
	 * 인트로 페이지
	 * @param request
	 * @param model
	 * @param user
	 * @return
	 */
	@GetMapping(path = { "Intro.html" })
    public String BeginIntro(HttpServletRequest request, ModelMap model, UserVo user) {
		
		if( ! userService.IsInit() ) {
			model.addAttribute("unique", authService.getNewGenUnique(request) );
			return "Intro/Infor";
		}
		
		if( user == null ) {
			return "redirect:/Auth/Login.html";
		}else {
			return "redirect:/Dir/Index.html";	
		}		
    }
	
	/**
	 * 저장소 목록을 보여질 팝업창
	 * @param model
	 * @return
	 */
	@GetMapping(path = { "Dir.layer" })
    public String BeginIntroDirListLayer(ModelMap model) {
		final File [] roots = File.listRoots();
		
		final List<String> list = new ArrayList<>();
		
		for( File f : roots ) {
			list.add( f.getPath().toString() );
		}
		
		model.addAttribute("list", list);		
		
		return "Intro/DirLayer";
    }
	
	/**
	 * 초기 정보를 저장한다.
	 * @param request
	 * @param req
	 * @return
	 */
	@ResponseBody
	@PostMapping(path = { "SaveInit.json" })
    public ReturnBasic SaveInitinfor(HttpServletRequest request, @RequestBody InitInforReqVo req) {
		
		req.setIp( BurrowUtils.getRemoteAddress(request) );
		
		return authService.saveInitInfor(req);
    }
	
	/**
	 * 패스 정보를 얻어온다.
	 * @param path
	 * @return
	 */
	@ResponseBody
	@GetMapping("PathList.json")
    public List<DirectoryDto> list(@RequestParam String path) {
        
		if( userService.IsInit() ) {
			// 이미 초기화 했다면 무효
			return null;
		}
		
		if( "root".equals( path ) ){
			final File [] roots = File.listRoots();
			
			List<DirectoryDto> result = new ArrayList<>();
			for( File root : roots ) {
				if( root.getPath().contains("Y:") )
					continue;
				
				final DirectoryDto item = new DirectoryDto();
				try {
					// 자식 폴더 존재 여부
		            try (Stream<Path> children = Files.list(root.toPath())) {
		            	item.setHasChildren(children.anyMatch(Files::isDirectory));
		            }
		            
		            item.setName( root.toPath().toString() );
		            item.setPath( root.toPath().toString() );
		            
		            result.add(item);
				} catch (IOException e) {
					log.error(e.getMessage());
				}
			}
			
			return result;
		}
		
		final Path dir = Paths.get(path); // 실제 루트 경로 설정

	    if (!Files.exists(dir) || !Files.isDirectory(dir)) {
	        return Collections.emptyList();
	    }

	    try (Stream<Path> stream = Files.list(dir)) {
	        return stream
	            .filter(Files::isDirectory) // 디렉토리만
	            .map(p -> {
	                boolean hasChildren = false;
	                try (Stream<Path> children = Files.list(p)) {
	                    hasChildren = children.anyMatch(Files::isDirectory);
	                } catch (IOException ignored) {}

	                return new DirectoryDto(
	                    p.getFileName().toString(),
	                    dir.relativize(p).toString(),
	                    hasChildren,
	                    false
	                );
	            })
	            .collect(Collectors.toList());
	    } catch (IOException e) {
	        return Collections.emptyList();
	    }
	}
	
}

