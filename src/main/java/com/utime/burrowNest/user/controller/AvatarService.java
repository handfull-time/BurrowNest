package com.utime.burrowNest.user.controller;

import org.springframework.stereotype.Service;

/**
 * 캐시 이벤트 예제.
 */
@Service
public class AvatarService {
	
//  private final UserThumbRepo repo;
//
//  public record ThumbDTO(String mime, String etag, byte[] img) {}
//
//  @Cacheable(cacheNames="avatarThumb", key="T(String).format('u:%d:%s', #userNo, #etag)", unless="#result==null")
//  public ThumbDTO getThumb(long userNo, String etagHint) {
//    var t = repo.findById(userNo).orElse(null);
//    if (t == null) return null;
//    return new ThumbDTO(t.getMime(), t.getEtag(), t.getImg());
//  }
//
//  @CacheEvict(cacheNames="avatarThumb", allEntries=true) // 단순화
//  @Transactional
//  public void updateThumb(long userNo, MultipartFile file) { /* 썸네일 생성->DB 저장, etag 갱신 */ }
}


/*
 * 
 * 
 *컨트롤러(304 처리) 
@GetMapping("/users/{userNo}/avatar")
public ResponseEntity<byte[]> get(@PathVariable long userNo,
        @RequestHeader(value="If-None-Match", required=false) String inm) {

  // etag 힌트 없이도 OK: 서비스에서 최신 ETag 리턴
  var dto = avatarService.getThumb(userNo, null);
  if (dto == null) return ResponseEntity.notFound().build();

  var etag = "\"" + dto.etag() + "\"";
  if (etag.equals(inm)) {
    return ResponseEntity.status(HttpStatus.NOT_MODIFIED).eTag(etag).build();
  }
  return ResponseEntity.ok()
      .contentType(MediaType.parseMediaType(dto.mime()))
      .eTag(etag)
      .cacheControl(CacheControl.maxAge(Duration.ofDays(30)).cachePublic())
      .body(dto.img());
}


 */