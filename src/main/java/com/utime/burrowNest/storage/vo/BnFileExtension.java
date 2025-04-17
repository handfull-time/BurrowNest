package com.utime.burrowNest.storage.vo;

import lombok.Data;

/**
 * 파일 종류
 * @author utime
 *
 */
@Data
public class BnFileExtension {
	/** 파일 확장자 */
	private String extension;
	/** 파일 종류 */
	private EBnFileType fileType;
}
