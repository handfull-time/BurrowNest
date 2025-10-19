package com.utime.burrowNest.storage.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utime.burrowNest.common.util.FileUtils;
import com.utime.burrowNest.common.vo.ReturnBasic;
import com.utime.burrowNest.storage.service.StorageService;
import com.utime.burrowNest.storage.vo.BnFile;
import com.utime.burrowNest.storage.vo.FileDto;
import com.utime.burrowNest.storage.vo.PasteItem;
import com.utime.burrowNest.storage.vo.PasteRequest;
import com.utime.burrowNest.user.vo.UserVo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("Files")
public class FileController {
	
	@Autowired
	private StorageService storageService;
	
	@GetMapping("Open/{uid}")
	public Object openOrPreviewFile(UserVo user, HttpServletRequest request, Model model, @PathVariable("uid") String uid)  {
	    uid = URLEncoder.encode(uid, StandardCharsets.UTF_8);
	    final BnFile bnFile = storageService.getFile(user, uid);
	    if (bnFile == null) {
	        return ResponseEntity.notFound().build();
	    }

	    final Path file = new File(bnFile.getFullName()).toPath();
	    if (!Files.exists(file)) {
	        return ResponseEntity.notFound().build();
	    }
	    
	    String mimeType;
		try {
			mimeType = Files.probeContentType(file);
		} catch (IOException e) {
			mimeType = null;
		}
	    
	    switch( bnFile.getFileType() ) {
	    case Basic:
	    case Document:
	    case Archive:{
	    	
	        if (mimeType == null) {
	            mimeType = "application/octet-stream";
	        }
	    	
	        if( mimeType.startsWith("text/") ) {
	        	model.addAttribute("mimeType", mimeType);
	        }else {
	        	try {
	        		// 그 외 일반 다운로드
		        	final Resource resource = new InputStreamResource(Files.newInputStream(file));
		        	final String transName = URLEncoder.encode(file.getFileName().toString(), "utf-8").replaceAll("\\+", "%20");

			        return ResponseEntity.ok()
			            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + transName + "\"")
			            .contentType(MediaType.parseMediaType(mimeType))
			            .body(resource);
				} catch (IOException e) {
					return ResponseEntity.internalServerError().build();
				}
	        }
	    	break;
	    }
	    case Image:
	    case Video:
	    case Audio:{
	    	break;
	    }
	    }

        model.addAttribute("uid", uid);
        model.addAttribute("fileName", file.getFileName().toString());
        model.addAttribute("mimeType", mimeType);

        // 배경용 썸네일 base64
        final byte [] thumbnail = storageService.getThumbnail(user, uid);
        final String base64Thumbnail = thumbnail == null? "":Base64.getEncoder().encodeToString( thumbnail );
        model.addAttribute("base64Thumbnail", base64Thumbnail);

        return "Common/Streaming";
	}

	
	//@GetMapping("Open/{uid}")
	public ResponseEntity<Resource> openFile(UserVo user, HttpServletRequest request, @PathVariable("uid") String uid) {

		uid = URLEncoder.encode(uid, StandardCharsets.UTF_8);
		final BnFile bnFile = storageService.getFile(user, uid);
		if( bnFile == null ) {
			return ResponseEntity.notFound().build();
		}
		
		final Path file = new File(bnFile.getFullName()).toPath();
	    if (!Files.exists(file)) {
	        return ResponseEntity.notFound().build();
	    }
	    
	    try {
	        String mimeType = Files.probeContentType(file);
	        
	        if (mimeType != null && (mimeType.startsWith("video/") || mimeType.startsWith("audio/"))) {
	            // redirect to streaming page
	            final URI uri = URI.create(request.getContextPath() + "/Files/Streaming/" + uid);
	            return ResponseEntity.status(HttpStatus.FOUND).location(uri).build();
	        }
	        
	        // 텍스트 파일이라면 charset=utf-8 명시
	        if (mimeType != null && mimeType.startsWith("text/")) {
	        	mimeType += "; charset=UTF-8";
	        }
	        
	        Resource resource = new InputStreamResource(Files.newInputStream(file));
	        
	        final String transName = URLEncoder.encode(file.getFileName().toString(), "utf-8").replaceAll("\\+","%20");

	        return ResponseEntity.ok()
	                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + transName + "\"")
	                .contentType(MediaType.parseMediaType(mimeType != null ? mimeType : "application/octet-stream"))
	                .body(resource);

	    } catch (IOException e) {
	        return ResponseEntity.internalServerError().build();
	    }
	}
	
	//@GetMapping("Streaming/{uid}")
	public String streamPage(Model model, UserVo user, @PathVariable("uid") String uid) throws IOException {
		uid = URLEncoder.encode(uid, StandardCharsets.UTF_8);
		final BnFile bnFile = storageService.getFile(user, uid);
		if( bnFile == null ) {
			return "";
		}
		
		Path file = new File(bnFile.getFullName()).toPath();
	    if (!Files.exists(file)) {
	        return "";
	    }

	    String mimeType = Files.probeContentType(file);
	    if (mimeType == null) {
	        mimeType = "application/octet-stream";
	    }

	    model.addAttribute("mimeType", mimeType);

	    return "Common/Streaming";
	}
	
	@GetMapping("Stream")
	public ResponseEntity<StreamingResponseBody> openStreamingFile(HttpServletRequest request, UserVo user, @RequestParam String path ) throws IOException {
		System.out.println("Stream User : " + user);
		
	    Path filePath = Paths.get("F:/WorkData/Burrow", path).normalize();

	    if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
	        return ResponseEntity.notFound().build();
	    }

	    long fileLength = Files.size(filePath);
	    String mimeType = Files.probeContentType(filePath);
	    if (mimeType == null) mimeType = "application/octet-stream";

	    // 텍스트 파일이라면 charset=UTF-8 명시
	    if (mimeType.startsWith("text/")) {
	        mimeType += "; charset=UTF-8";
	    }

	    HttpHeaders headers = new HttpHeaders();

	    // 파일 이름 한글 인코딩
	    String fileName = filePath.getFileName().toString();
	    String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");

	    headers.add(HttpHeaders.CONTENT_DISPOSITION, 
	        "inline; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName);
	    headers.add(HttpHeaders.CONTENT_TYPE, mimeType);

	    // Range 지원
	    String range = request.getHeader("Range");
	    long start = 0;
	    long end = fileLength - 1;

	    if (range != null && range.startsWith("bytes=")) {
	        String[] parts = range.replace("bytes=", "").split("-");
	        try {
	            start = Long.parseLong(parts[0]);
	            if (parts.length > 1 && !parts[1].isEmpty()) {
	                end = Long.parseLong(parts[1]);
	            }
	        } catch (NumberFormatException ignored) {}
	    }

	    long finalStart = start;
	    long finalEnd = end;
	    long contentLength = finalEnd - finalStart + 1;

	    headers.set(HttpHeaders.ACCEPT_RANGES, "bytes");
	    headers.set(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength));
	    headers.set(HttpHeaders.CONTENT_RANGE, String.format("bytes %d-%d/%d", finalStart, finalEnd, fileLength));

	    InputStream inputStream = Files.newInputStream(filePath);
	    inputStream.skip(finalStart); // 시작 지점으로 이동

	    StreamingResponseBody responseBody = outputStream -> {
	        try (BufferedInputStream in = new BufferedInputStream(inputStream)) {
	            byte[] buffer = new byte[8192];
	            long bytesToRead = contentLength;
	            int bytesRead;
	            while ((bytesRead = in.read(buffer, 0, (int) Math.min(buffer.length, bytesToRead))) != -1) {
	                outputStream.write(buffer, 0, bytesRead);
	                bytesToRead -= bytesRead;
	                if (bytesToRead <= 0) break;
	            }
	        }
	    };

	    // 206 Partial Content if ranged, 200 OK otherwise
	    return new ResponseEntity<>(responseBody, headers, (range != null ? HttpStatus.PARTIAL_CONTENT : HttpStatus.OK));
	}

//	
//	@GetMapping("Open")
//	public ResponseEntity<StreamingResponseBody> openFile(
//			UserVo user,
//	        @RequestParam String path,
//	        @RequestHeader(value = "Range", required = false) String rangeHeader
//	) throws IOException {
//
//		final Path filePath = Paths.get("F:/WorkData/Burrow", path);
//	    if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
//	        return ResponseEntity.notFound().build();
//	    }
//
//	    final long fileSize = Files.size(filePath);
//	    String mimeType = Files.probeContentType(filePath);
//	    // 텍스트 파일이라면 charset=utf-8 명시
//        if (mimeType != null && mimeType.startsWith("text/")) {
//        	mimeType += "; charset=UTF-8";
//        } else if (mimeType == null) {
//        	mimeType = "application/octet-stream";
//        }
//
//	    long start = 0;
//	    long end = fileSize - 1;
//
//	    if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
//	        String[] ranges = rangeHeader.replace("bytes=", "").split("-");
//	        try {
//	            start = Long.parseLong(ranges[0]);
//	            if (ranges.length > 1 && !ranges[1].isBlank()) {
//	                end = Long.parseLong(ranges[1]);
//	            }
//	        } catch (NumberFormatException ignored) {}
//	    }
//
//	    final long contentLength = end - start + 1;
//	    final InputStream inputStream = Files.newInputStream(filePath);
//	    inputStream.skip(start);
//
//	    final StreamingResponseBody responseBody = outputStream -> {
//	        final byte[] buffer = new byte[8192];
//	        long bytesToRead = contentLength;
//	        int bytesRead;
//
//	        while (bytesToRead > 0 && (bytesRead = inputStream.read(buffer, 0, (int) Math.min(buffer.length, bytesToRead))) != -1) {
//	            outputStream.write(buffer, 0, bytesRead);
//	            bytesToRead -= bytesRead;
//	        }
//
//	        inputStream.close();
//	    };
//
//	    final HttpHeaders headers = new HttpHeaders();
//	    headers.set(HttpHeaders.ACCEPT_RANGES, "bytes");
//	    headers.setContentLength(contentLength);
//	    headers.setContentType(MediaType.parseMediaType(mimeType));
//	    
//	    if (rangeHeader != null) {
//	        headers.set(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + fileSize);
//	        return new ResponseEntity<>(responseBody, headers, HttpStatus.PARTIAL_CONTENT);
//	    } else {
//		    final String transName = URLEncoder.encode(filePath.getFileName().toString(), "utf-8").replaceAll("\\+","%20");
//		    headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + transName + "\"");
//	        return new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
//	    }
//	}

	/**
	 * 대용량 파일 다운로드
	 * @param fileGuid
	 * @return
	 * @throws IOException
	 */
	@GetMapping("StreamDownload/{fileNo}")
	public ResponseEntity<StreamingResponseBody> streamDownload(@PathVariable("fileNo") String fileGuid) throws IOException {
//		// 경로를 환경 변수 또는 설정 파일에서 로드
//	    String storageDirectory = System.getenv("FILE_STORAGE_PATH");
//	    if (storageDirectory == null) {
//	        storageDirectory = "/data/storage"; // 기본 경로
//	    }
		
		// create table test(id int primary key, data uuid default random_uuid());
		
		String storageDirectory="", fileName = fileGuid;

	    Path filePath = Paths.get(storageDirectory).resolve(fileName).normalize();
		

	    // 경로 탐색 공격 방지: 저장소 디렉토리 안의 파일만 허용
	    if (!filePath.startsWith(Paths.get(storageDirectory)) || !Files.exists(filePath)) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body(null);
	    }

	    try {
	        // 파일 크기 확인
	        long fileSize = Files.size(filePath);

	        StreamingResponseBody stream = outputStream -> {
	            try (InputStream inputStream = Files.newInputStream(filePath)) {
	                byte[] buffer = new byte[4096];
	                int bytesRead;
	                while ((bytesRead = inputStream.read(buffer)) != -1) {
	                    outputStream.write(buffer, 0, bytesRead);
	                    outputStream.flush(); // chunk 단위 전송
	                }
	            } catch (IOException e) {
	                // 스트리밍 중 에러 처리
	                throw new RuntimeException("Error occurred during file streaming", e);
	            }
	        };

	        return ResponseEntity.ok()
	                .contentType(MediaType.APPLICATION_OCTET_STREAM)
	                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
	                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileSize)) // 파일 크기 헤더 추가
	                .body(stream);

	    } catch (IOException e) {
	        // 파일 접근 및 크기 확인 중 에러 처리
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(null);
	    }
	}
//	
//	
//	@GetMapping("/stream-download/{fileName:.+}")
//	public ResponseEntity<StreamingResponseBody> streamDownload(@PathVariable String fileName) {
//	    
//	}
	
	@GetMapping("Download")
	public ResponseEntity<Resource> download(@RequestParam String path) throws IOException {
	    Path file = Paths.get("F:\\WorkData\\Burrow", path);
	    if (!Files.exists(file)) {
	        return ResponseEntity.notFound().build();
	    }

	    Resource resource = new UrlResource(file.toUri());
	    String contentType = Files.probeContentType(file);
	    String fileName = file.getFileName().toString();

	    return ResponseEntity.ok()
	        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + URLEncoder.encode(fileName, "UTF-8") + "\"")
	        .contentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"))
	        .body(resource);
	}
	
	@GetMapping("ZipDownload")
	public void zipDownload(HttpServletResponse response, @RequestParam String paths) throws IOException {
		
	    final List<String> filePaths = new ObjectMapper().readValue(paths, new TypeReference<List<String>>() {});
	    
	    response.setContentType("application/zip");
	    response.setHeader("Content-Disposition", "attachment; filename=\"download.zip\"");

	    try (ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())) {
	        for (String relPath : filePaths) {
	            final Path fullPath = Paths.get("F:\\WorkData\\Burrow", relPath).normalize();

	            if (Files.isDirectory(fullPath)) {
	                Files.walk(fullPath).filter(p -> !Files.isDirectory(p)).forEach(p -> {
	                    try {
	                        String entryName = fullPath.relativize(p).toString().replace("\\", "/");
	                        zos.putNextEntry(new ZipEntry(entryName));
	                        Files.copy(p, zos);
	                        zos.closeEntry();
	                    } catch (IOException ignored) {}
	                });
	            } else {
	                zos.putNextEntry(new ZipEntry(fullPath.getFileName().toString()));
	                Files.copy(fullPath, zos);
	                zos.closeEntry();
	            }
	        }
	    }
	}

	@ResponseBody
	@PostMapping("Paste.json")
	public ReturnBasic paste(@RequestBody PasteRequest req) {
		
		final Path sourceBase = Paths.get("F:\\WorkData\\Burrow", req.getFromPath());
		final Path targetBase = Paths.get("F:\\WorkData\\Burrow", req.getToPath());

	    try {
	        for (PasteItem item : req.getItems()) {
	            Path source = sourceBase.resolve(item.getName()).normalize();
	            Path target = targetBase.resolve(item.getName()).normalize();

	            if ("copy".equals(req.getType())) {
	                if (Files.isDirectory(source)) {
	                    FileUtils.copyDirectory(source, target);
	                } else {
	                    Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
	                }
	            } else {
	                Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
	            }
	        }
	        return new ReturnBasic();
	    } catch (Exception e) {
	    	return new ReturnBasic("500", e.getMessage());
	    }
	}

	@ResponseBody
	@PostMapping("Delete.json")
	public ReturnBasic deleteFiles(@RequestBody List<FileDto> files, @RequestParam String path) {
		ReturnBasic result = new ReturnBasic();
	    final Path base = Paths.get("F:/WorkData/Burrow", path);

	    try {
	        for (FileDto file : files) {
	            Path target = base.resolve(file.getName());

	            if (Files.exists(target)) {
	                if (file.isDirectory()) {
	                	FileUtils.deleteDirectory(target);
	                } else {
	                    Files.delete(target);
	                }
	            }
	        }
	    } catch (Exception e) {
	    	result.setCodeMessage("500", e.getMessage());
	    }

	    return result;
	}
	
	@GetMapping("Thumbnail/{fid}")
	public ResponseEntity<byte[]> getThumbnail(UserVo user, @PathVariable String fid) {
	    final byte[] image = storageService.getThumbnail(user, fid);
	    
	    if( image == null ) {
	    	return ResponseEntity.notFound().build();
	    }
	    
	    return ResponseEntity.ok()
	            .contentType(MediaType.IMAGE_JPEG)
	            .contentLength(image.length)
	            .body(image);
	}

}

