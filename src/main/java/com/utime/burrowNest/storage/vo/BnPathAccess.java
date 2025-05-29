package com.utime.burrowNest.storage.vo;

import lombok.Data;

@Data
public class BnPathAccess {
	/** 고유 번호 */
	long no;
	/** 그룹 번호 */
	int groupNo;
	/** 엑세스 권한 */
	int accessFlags;
}
