package com.utime.burrowNest.user.vo;

import java.time.LocalDateTime;

import com.utime.burrowNest.common.util.BurrowUtils;
import com.utime.burrowNest.common.vo.EJwtRole;

import lombok.Getter;
import lombok.Setter;

/**
 * 사용자 정보
 */
@Setter
@Getter
public class UserVo {
	/** 회원 번호 */
	int userNo;
	/** 생성일 */
	LocalDateTime regDate;
	/** 수정일 */
	LocalDateTime updateDate;
	/** 사용 여부 */
	boolean enabled;
	/** 속한 그룹 */
	GroupVo group;
	/** id */
	String id;
	/** 닉네임 */
	String nickname;
	/** 권한 */
	EJwtRole role;
	/** 비고 */
	String note;
	/** 암호 찾기 값 */
	String authHint;
	/** 하루 업로드 제한 byte 단위 */
	long dailyUploadLimit;
	/** 일일 최대 다운로드 용량 */
	long dailyDownloadLimit;
	/** 저장공간 최대 사용 용량전체 저장소 제한 (사용자별 quota 등) */
	long maxStorageUsage;
	
	@Override
	public String toString() {
		return BurrowUtils.toJson(this);
	}
}
