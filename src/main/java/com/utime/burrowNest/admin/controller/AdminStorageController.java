package com.utime.burrowNest.admin.controller;

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
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.utime.burrowNest.admin.vo.SaveSotrageReqVo;
import com.utime.burrowNest.common.vo.ReturnBasic;
import com.utime.burrowNest.root.service.LoadStorageService;
import com.utime.burrowNest.storage.service.StorageService;
import com.utime.burrowNest.storage.vo.BnDirectory;
import com.utime.burrowNest.user.vo.UserVo;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("Admin/Storage")
public class AdminStorageController {
	
	@Autowired
	private LoadStorageService loadStorageService;
	
	@Autowired
	private StorageService storageService;
	
	/**
	 * 그룹 Storeage 목록
	 * @param user
	 * @return
	 */
	@GetMapping("Storage.html")
	public String adminStoragePage(Model model, UserVo user) {
		
		model.addAttribute("directories", storageService.getGroupStorageList(user.getGroup().getGroupNo()) );
		
		return "Admin/Storage/AdminStorageMain";
	}
	
	@ResponseBody
	@GetMapping("GroupStorageList.json")
	public List<BnDirectory> GroupStorageList(@RequestParam("GroupNo") int groupNo) {
		return null;
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
		
		return "Admin/Storage/DirLayer";
    }
	
	/**
	 * 패스 정보를 얻어온다.
	 * @param path
	 * @return
	 */
	@ResponseBody
	@GetMapping("PathList.json")
    public List<BnDirectory> list(@RequestParam String path) {
        
//		if( storageService.IsInit() ) {
//			// 이미 초기화 했다면 무효
//			return null;
//		}
		
		if( "root".equals( path ) ){
			final File [] roots = File.listRoots();
			
			List<BnDirectory> result = new ArrayList<>();
			for( File root : roots ) {
				log.info(root.toPath().toString());
				
				if( root.getPath().contains("Y:") )
					continue;
				
				final BnDirectory item = new BnDirectory();
				try {
					// 자식 폴더 존재 여부
		            try (Stream<Path> children = Files.list(root.toPath())) {
		            	item.setHasChild(children.anyMatch(Files::isDirectory));
		            }
		            
		            item.setName( root.toPath().toString() );
		            item.setAbsolutePath( root.toPath().toString() );
		            
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

	                final BnDirectory dirDto = new BnDirectory();
	                dirDto.setName( p.getFileName().toString() );
	                dirDto.setAbsolutePath( dir.relativize(p).toString() );
	                dirDto.setHasChild(hasChildren);
	                
	                return dirDto;
	            })
	            .collect(Collectors.toList());
	    } catch (IOException e) {
	        return Collections.emptyList();
	    }
	}
	
	@ResponseBody
	@PostMapping(path = { "SaveRootStorage.json" })
    public ReturnBasic SaveInitinfor(HttpServletRequest request, SaveSotrageReqVo req) {
		return loadStorageService.saveRootStorage(req);
	}
	
	@ResponseBody
	@DeleteMapping(path = { "DeleteRootStorage.json" })
    public ReturnBasic DeleteInitinfor(HttpServletRequest request, @RequestParam("no") long no) {
//		return loadStorageService.saveInitStorage(req);
		return null;
	}
}

