package com.utime.burrowNest.storage.vo;

import com.utime.burrowNest.common.util.BurrowUtils;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BnPathAccess {
	/** 고유 번호 */
	long no;
	/** 그룹 번호 */
	long groupNo;
	/** 엑세스 권한 */
	int accessFlags;
	
	@Override
	public String toString() {
		return BurrowUtils.toJson(this);
	}
}
