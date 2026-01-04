package com.utime.burrowNest.dropbox.vo;

import lombok.Getter;

/**
 * Dropbox 컨텍스트 설정 정보
 */
@Getter
public class DropboxContextConfig {
	final String userId;
	final String returnUrl;
	final boolean popup;
	
	public DropboxContextConfig(String userId, String returnUrl, boolean popup) {
		this.userId = userId;
		this.returnUrl = returnUrl;
		this.popup = popup;
	}
}
