package com.utime.burrowNest.user.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.utime.burrowNest.common.util.BurrowUtils;

/**
 * 고유 요청 정보
 */
public class ReqUniqueVo {

	protected String token;
	protected String ip;
	protected String rsaId;
	@JsonIgnore
	protected String publicKey;
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getRsaId() {
		return rsaId;
	}
	public void setRsaId(String rsaId) {
		this.rsaId = rsaId;
	}
	public String getPublicKey() {
		return publicKey;
	}
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
	@Override
	public String toString() {
		return BurrowUtils.toJson(this);
	}
}
