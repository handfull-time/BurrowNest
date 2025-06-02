package com.utime.burrowNest.storage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.utime.burrowNest.common.util.BurrowUtils;
import com.utime.burrowNest.storage.service.StorageService;
import com.utime.burrowNest.storage.vo.AbsPath;
import com.utime.burrowNest.storage.vo.BnDirectory;
import com.utime.burrowNest.storage.vo.DirectoryDto;
import com.utime.burrowNest.user.vo.UserVo;

@Controller
@RequestMapping("Dir")
public class DirectoryController {
	
	@Autowired
	private StorageService storageService;
	
	/**
	 * Root 
	 * @param model
	 * @return
	 */
	@GetMapping(path = { "/", "Index.html" })
    public String noneAuthMetaPage(ModelMap model, UserVo user) throws Exception{
		
		return this.path(model, user, null);
    }
	
	@GetMapping("Path.html")
    public String path(ModelMap model, UserVo user, @RequestParam("uid") String uid) throws Exception {
		
		final List<DirectoryDto> dirList = storageService.getDirectory(user, uid);
		
		if( BurrowUtils.isEmpty(dirList) ) {
			throw new Exception("폴더 없음.");
		}
		
		model.addAttribute("directories", dirList);
//	    // 선택한 path의 파일 목록
//        model.addAttribute("files", storageService.getFiles(user, dir) );
//        model.addAttribute("paths", storageService.getPaths(user, dir) );
		
//		final DirectoryDto rootTree = buildDirectoryTreeLimited("F:\\WorkData\\Burrow", path);
//
//	    model.addAttribute("directoryTree", rootTree);
//	    // 선택한 path의 파일 목록
//        model.addAttribute("files", this.files(path) );
//        
//        model.addAttribute("path", path );
	    
	    return "Storage/StorageMain";
	}
	
	@GetMapping("Parent.json")
    public BnDirectory getParent(UserVo user, @RequestParam("uid") String uid) throws Exception {
		
		final BnDirectory result = storageService.getParentDirectory(user, uid);
		
		if( result == null ) {
			throw new Exception("폴더 없음.");
		}
	    
	    return result;
	}
	
	@GetMapping("Files.json")
    public List<AbsPath> getFiles(UserVo user, @RequestParam("uid") String uid) throws Exception {
		
		final List<AbsPath> result = storageService.getFiles(user, uid);
		
		if( result == null ) {
			throw new Exception("폴더 없음.");
		}
	    
	    return result;
	}
/*
	public DirectoryDto buildDirectoryTreeLimited(String root, String currentPath) throws IOException {
		final Path basePath = Paths.get(root).normalize();
		final Path targetPath = Paths.get(root, currentPath).normalize();

	    // 루트 노드 초기화
		final DirectoryDto rootDto = new DirectoryDto();
	    rootDto.setName("/");
	    rootDto.setPath("/");
	    rootDto.setSelected(currentPath.equals("/") || currentPath.isBlank());

	    buildTreeRecursive(basePath, basePath, rootDto, targetPath);

	    return rootDto;
	}

	private boolean buildTreeRecursive(Path rootBase, Path currentPath, DirectoryDto currentDto, Path targetPath) throws IOException {
	    final List<DirectoryDto> subList = new ArrayList<>();
	    boolean hasSelectedDescendant = false;

	    try (Stream<Path> stream = Files.list(currentPath)) {
	        for (Path subPath : stream.filter(Files::isDirectory).toList()) {
	        	final DirectoryDto child = new DirectoryDto();

	        	final String relativePath = "/" + rootBase.relativize(subPath).toString().replace(File.separator, "/");
	            child.setName(subPath.getFileName().toString());
	            child.setPath(relativePath);
	            child.setSelected(subPath.normalize().equals(targetPath));

	            // 자식 폴더 존재 여부
	            try (Stream<Path> children = Files.list(subPath)) {
	                child.setHasChildren(children.anyMatch(Files::isDirectory));
	            }

	            // 하위로 계속 탐색할지 결정
	            if (child.isSelected() || targetPath.startsWith(subPath)) {
	            	final boolean deeper = buildTreeRecursive(rootBase, subPath, child, targetPath);
	                hasSelectedDescendant |= (child.isSelected() || deeper);
	            }

	            subList.add(child);
	        }
	    }

	    currentDto.setSubDirectories(subList);
	    currentDto.setHasChildren(!subList.isEmpty());

	    return hasSelectedDescendant;
	}


	
	@ResponseBody
	@GetMapping("List.json")
    public List<DirectoryDto> list(@RequestParam String path) {
        final Path dir = Paths.get("F:\\WorkData\\Burrow", path); // 실제 루트 경로 설정

	    if (!Files.exists(dir) || !Files.isDirectory(dir)) {
	        return Collections.emptyList();
	    }

	    try (Stream<Path> stream = Files.list(dir)) {
	        return stream
	            .filter(Files::isDirectory) // ✅ 디렉토리만 필터링
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
	
	public List<FileDto> files(String path) {
	    final Path dir = Paths.get("F:\\WorkData\\Burrow", path); // 실제 루트 경로 설정

	    if (!Files.exists(dir) || !Files.isDirectory(dir)) {
	        return Collections.emptyList();
	    }

	    try (Stream<Path> stream = Files.list(dir)) {
	        return stream.map(p -> {
	            try {
	                final boolean isDirectory = Files.isDirectory(p);
	                final long size = Files.isDirectory(p) ? 0 : Files.size(p);
	                final String contentType = Files.isDirectory(p) ? "folder" : Files.probeContentType(p);
	                final String lastModified = Files.getLastModifiedTime(p).toString();

	                return new FileDto(
	                    p.getFileName().toString(),
	                    size,
	                    isDirectory,
	                    contentType,
	                    lastModified
	                );
	            } catch (IOException e) {
	                return new FileDto(
	                    p.getFileName().toString(),
	                    0,
	                    false,
	                    "unknown",
	                    "-"
	                );
	            }
	        }).collect(Collectors.toList());
	    } catch (IOException e) {
	        return Collections.emptyList();
	    }
	}
	
	*/
}

