package com.utime.burrowNest.user.vo;

import java.util.Date;
import java.util.List;

import com.utime.burrowNest.common.vo.EJwtRole;

import lombok.Data;

@Data
public class UserVo {
	/** 회원 번호 */
	int userNo;
	/** 생성일 */
	Date regDate;
	/** 수정일 */
	Date updateDate;
	/** id */
	String id;
	/** 사용 여부 */
	boolean enabled;
	/** 닉네임 */
	String nickname;
	/** 내 이미지 */
	String profileImg;
	/** 관리 패스 */
	List<Long> rootPath;
	/** 권한 */
	EJwtRole role;
	/** 비고 */
	String note;
	/** 하루 업로드 제한 byte 단위 */
	long dailyUploadLimit;
	/** 일일 최대 다운로드 용량 */
	long dailyDownloadLimit;
	/** 저장공간 최대 사용 용량전체 저장소 제한 (사용자별 quota 등) */
	long maxStorageUsage;
}
