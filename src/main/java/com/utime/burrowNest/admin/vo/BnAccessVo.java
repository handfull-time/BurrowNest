package com.utime.burrowNest.admin.vo;

import lombok.Data;

/**
 * 엑세스 정보
 */
@Data
public class BnAccessVo {
	/**
	 * The primary key of the directory(file) entity.
	 */
	long no;
	/**
	 * group no
	 */
	int groupNo;
	/**
	 * File 또는 Directory 접근 권한
	 */
	int accType;
	
	public BnAccessVo() {
		
	}
	
	public BnAccessVo( long no, int groupNo, int accType) {
		this.no = no;
		this.groupNo = groupNo;
		this.accType = accType;
	}
}
