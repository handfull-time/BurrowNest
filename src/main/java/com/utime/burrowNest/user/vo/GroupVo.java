package com.utime.burrowNest.user.vo;

import java.time.LocalDateTime;

import com.utime.burrowNest.common.vo.EJwtRole;
import com.utime.burrowNest.storage.vo.EAccessType;

import lombok.Data;

/**
 * 사용자 정보
 */
@Data
public class GroupVo {
	/** 회원 번호 */
	int groupNo;
	/** 생성일 */
	LocalDateTime regDate;
	/** 수정일 */
	LocalDateTime updateDate;
	/** 사용 여부 */
	boolean enabled;
	/** 닉네임 */
	String name;
	/** 권한 */
	EJwtRole role;
	/** 저장소 접근 권한 */
	EAccessType accType;
	/** 비고 */
	String note;
	/** 하루 업로드 제한 byte 단위 */
	long dailyUploadLimit;
	/** 일일 최대 다운로드 용량 */
	long dailyDownloadLimit;
	/** 저장공간 최대 사용 용량전체 저장소 제한 (사용자별 quota 등) */
	long maxStorageUsage;
}
