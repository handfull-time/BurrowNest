package com.utime.burrowNest.storage.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.utime.burrowNest.common.vo.BinResultVo;
import com.utime.burrowNest.storage.vo.BnDirectory;
import com.utime.burrowNest.storage.vo.BnFile;
import com.utime.burrowNest.storage.vo.BnFileArchive;
import com.utime.burrowNest.storage.vo.BnFileAudio;
import com.utime.burrowNest.storage.vo.BnFileDocument;
import com.utime.burrowNest.storage.vo.BnFileImage;
import com.utime.burrowNest.storage.vo.BnFileVideo;

@Mapper
public interface StorageMapper {
	
	/**
	 * 관리자 초기화 했는지 여부 
	 * @return true:했음.
	 */
	boolean IsInit();
	
	/**
	 * dir 추가
	 * @param dir
	 * @return
	 */
	int insertBnDirectory( BnDirectory dir );
	
	/**
	 * dir 수정
	 * @param dir
	 * @return
	 */
	int updateBnDirectory( BnDirectory dir );
	
	/**
	 * dir 조회
	 * @param dirNo
	 * @return
	 */
	BnDirectory selectBnDirectoryByNo( @Param("dirNo") long dirNo );
	
	/**
	 * 삭제
	 * @param dirNo
	 * @return
	 */
	int deleteBnDirectoryByNo( @Param("dirNo") long dirNo );
	
	/**
	 * 파일 추가.
	 * @param file
	 * @return
	 */
	int insertBnFile( BnFile file );
	
	/**
	 * 파일 조회
	 * @param fileNo
	 * @return
	 */
	BnFile selectBnFileByNo( @Param("fileNo") long fileNo );
	
	/**
	 * 파일 수정
	 * @param file
	 * @return
	 */
	int updateBnFile( BnFile file );
	
	/**
	 * 파일 삭제
	 * @param fileNo
	 * @return
	 */
	int deleteBnFileByNo( @Param("fileNo") long fileNo );
	
	/**
	 * Dir 권한 추가하기
	 * @param dirNo
	 * @param groupNo
	 * @param accType
	 * @return
	 */
	int insertDirectoryAccess(@Param("dirNo") long dirNo, @Param("groupNo") int groupNo, @Param("accType") int accType);

	/**
	 * Dir 권한 수정
	 * @param dirNo
	 * @param groupNo
	 * @param accType
	 * @return
	 */
	int updateDirectoryAccess(@Param("dirNo") long dirNo, @Param("groupNo") int groupNo, @Param("accType") int accType);

	/**
	 * 파일 권한 추가하기
	 * @param dirNo
	 * @param groupNo
	 * @param accType
	 * @return
	 */
	int insertFileAccess(@Param("fileNo") long fileNo, @Param("groupNo") int groupNo, @Param("accType") int accType);

	/**
	 * 파일 권한 수정하기
	 * @param fileNo
	 * @param groupNo
	 * @param accType
	 * @return
	 */
	int updateFileAccess(@Param("fileNo") long fileNo, @Param("groupNo") int groupNo, @Param("accType") int accType);

	/**
	 * 확장 정보 유무
	 * @param tableName
	 * @param fileNo
	 * @return true:있다.
	 */
	boolean existFileInfo( @Param("tableName") String tableName, @Param("fileNo") long fileNo );
	
	/**
	 * file doc 추가.
	 * @param document
	 * @return
	 */
	int insertBnFileDocument( BnFileDocument document );
	
	/**
	 * file doc 조회
	 * @param fileNo
	 * @return
	 */
	BnFileDocument selectBnFileDocumentByFileNo( @Param("fileNo") long fileNo );
	
	/**
	 * 파일 doc 수정
	 * @param document
	 * @return
	 */
	int updateBnFileDocument( BnFileDocument document );
	
	/**
	 * 파일 이미지 추가
	 * @param image
	 * @return
	 */
	int insertBnFileImage( BnFileImage image );
	
	/**
	 * 파일 이미지 조회
	 * @param fileNo
	 * @return
	 */
	BnFileImage selectBnFileImageByFileNo( @Param("fileNo") long fileNo );
	
	/**
	 * 파일 이미지 수정
	 * @param image
	 * @return
	 */
	int updateBnFileImage( BnFileImage image );
	
	/**
	 * 파일 비디오 추가
	 * @param video
	 * @return
	 */
	int insertBnFileVideo( BnFileVideo video );
	
	/**
	 * 파일 비디오 조회
	 * @param fileNo
	 * @return
	 */
	BnFileVideo selectBnFileVideoByFileNo( @Param("fileNo") long fileNo );
	
	/**
	 * 파일 비디오 수정
	 * @param video
	 * @return
	 */
	int updateBnFileVideo( BnFileVideo video );
	
	/**
	 * 오디오 파일 추가
	 * @param audio
	 * @return
	 */
	int insertBnFileAudio( BnFileAudio audio );
	
	/**
	 * 오디오 파일 조회
	 * @param fileNo
	 * @return
	 */
	BnFileAudio selectBnFileAudioByFileNo( @Param("fileNo") long fileNo );
	/**
	 * 오디오 파일 수정
	 * @param audio
	 * @return
	 */
	int updateBnFileAudio( BnFileAudio audio );
	
	/**
	 * 압축 파일 추가
	 * @param archive
	 * @return
	 */
	int insertBnFileArchive( BnFileArchive archive );
	
	/**
	 * 압축 파일 조회
	 * @param fileNo
	 * @return
	 */
	BnFileArchive selectBnFileArchiveByFileNo( @Param("fileNo") long fileNo );
	
	/**
	 * 압축 파일 수정
	 * @param archive
	 * @return
	 */
	int updateBnFileArchive( BnFileArchive archive );
	
	/**
	 * 섬네일 추가
	 * @param fileNo
	 * @param thumbnail
	 * @return
	 */
	int insertThumbnail( @Param("fileNo") long fileNo, @Param("thumbnail") byte [] thumbnail);
	
	/**
	 * 섬네일 조회
	 * @param fileNo
	 * @param thumbnail
	 * @return
	 */
	int updateThumbnail( @Param("fileNo") long fileNo, @Param("thumbnail") byte [] thumbnail);
	
	/**
	 * 섬네일 조회
	 * @param fileNo
	 * @return
	 */
	BinResultVo selectThumbnail( @Param("fileId") String fid );

}

/*
 좋습니다! 현재 구조는 매우 깔끔하고 확장 가능한 권한 모델을 잘 설계하셨습니다.  
말씀하신 것처럼 "A 사용자만 접근 가능한 폴더", "B 사용자만 접근 가능한 폴더",  
"하위 디렉토리에 접근 가능한지 여부", "public/private 구분" 등 다양한 시나리오를 처리할 수 있습니다.

---

## ✅ 사용 시나리오별 설계 및 쿼리 예

---

### 📌 1. 사용자 A가 특정 디렉토리(DIR_NO)에 `READ` 권한이 있는가?

```sql
SELECT 1
FROM BN_DIRECTORY_ACCESS
WHERE USER_NO = #{userNo}
  AND DIR_NO = #{dirNo}
  AND (ACCESS_FLAGS & #{EAccessType.READ.getBit()}) != 0
```

---

### 📌 2. 사용자 A가 특정 디렉토리(DIR_NO)에 `EXECUTE` 권한이 있어 진입 가능한가?

```sql
SELECT 1
FROM BN_DIRECTORY_ACCESS
WHERE USER_NO = #{userNo}
  AND DIR_NO = #{dirNo}
  AND (ACCESS_FLAGS & #{EAccessType.EXECUTE.getBit()}) != 0
```

---

### 📌 3. 사용자가 접근 가능한 모든 디렉토리 목록 (READ 이상)

```sql
SELECT D.*
FROM BN_DIRECTORY D
JOIN BN_DIRECTORY_ACCESS A ON D.NO = A.DIR_NO
WHERE A.USER_NO = #{userNo}
  AND (A.ACCESS_FLAGS & 1) != 0 -- READ 권한
```

---

### 📌 4. 특정 디렉토리의 하위 디렉토리 중 접근 가능한 목록

```sql
SELECT D.*
FROM BN_DIRECTORY D
JOIN BN_DIRECTORY_ACCESS A ON D.NO = A.DIR_NO
WHERE A.USER_NO = #{userNo}
  AND D.PARENT_NO = #{parentDirNo}
  AND (A.ACCESS_FLAGS & 1) != 0
```

---

### 📌 5. 사용자 A가 접근 가능한 디렉토리 내 파일 목록

```sql
SELECT F.*
FROM BN_FILE F
JOIN BN_DIRECTORY_ACCESS DA ON F.PARENT_NO = DA.DIR_NO
WHERE DA.USER_NO = #{userNo}
  AND (DA.ACCESS_FLAGS & 1) != 0
```

> ※ 파일 자체 접근 권한(`BN_FILE_ACCESS`)이 필요하다면 JOIN 추가

---

### 📌 6. 사용자 A가 파일에 직접 `READ` 권한이 있는가?

```sql
SELECT 1
FROM BN_FILE_ACCESS
WHERE FILE_NO = #{fileNo}
  AND USER_NO = #{userNo}
  AND (ACCESS_FLAGS & #{EAccessType.READ.getBit()}) != 0
```

---

### 📌 7. public/private 디렉토리 구분 전략

#### 👉 제안

- `BN_DIRECTORY`에 `IS_PUBLIC BOOLEAN DEFAULT FALSE` 컬럼 추가
- 접근 제어 쿼리에서 `IS_PUBLIC = TRUE` 조건을 추가로 반영

#### 예시

```sql
-- 사용자에게 직접 권한이 있거나, public 디렉토리인 경우
SELECT D.*
FROM BN_DIRECTORY D
LEFT JOIN BN_DIRECTORY_ACCESS A ON D.NO = A.DIR_NO AND A.USER_NO = #{userNo}
WHERE D.IS_PUBLIC = TRUE
   OR (A.ACCESS_FLAGS & 1) != 0
```

---

### 📌 8. 사용자 가입 시 개인 private 디렉토리 생성 로직 예

```java
// 회원 등록 후 자동 private 디렉토리 생성
long dirNo = directoryDao.insertPrivateDirectoryForUser(userNo);
directoryAccessDao.grantAccess(dirNo, userNo, READ | WRITE | EXECUTE);
```

---

## ✅ 추천: 권한 유틸 메서드 (Java)

```java
public static boolean hasPermission(int flags, EAccessType type) {
    return (flags & type.getBit()) != 0;
}
```

---

## ✅ 향후 확장 고려

| 기능 | 테이블/컬럼 추가 |
|------|------------------|
| 공개 공유 링크 | BN_DIRECTORY.SHARE_TOKEN |
| 공유 만료 시간 | BN_DIRECTORY.SHARE_EXPIRES |
| 그룹별 권한 관리 | BN_GROUP, BN_GROUP_MEMBER, BN_GROUP_ACCESS 등 |
| 소유자/관리자 구분 | BN_DIRECTORY.OWNER_NO |

---
 */