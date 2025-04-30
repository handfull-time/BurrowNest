package com.utime.burrowNest.admin.vo;

import java.time.LocalDateTime;

import com.utime.burrowNest.user.vo.UserVo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString(callSuper = true)
public class ManageUserVo extends UserVo {
	/**
	 * 총 로그인 횟수
	 */
	int loginCount;
	/** 마지막 로그인 날짜 */
	LocalDateTime lastLoginTime;
	/** 로그인 실패 횟수 */
	int loginFailCount;
}
