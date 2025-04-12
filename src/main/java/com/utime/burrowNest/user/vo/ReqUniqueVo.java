package com.utime.burrowNest.user.vo;

public class ReqUniqueVo {

	protected String token;
	protected String ip;
	protected String rsaId;
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
		return "ReqUniqueVo [token=" + token + ", ip=" + ip + ", rsaId=" + rsaId + ", publicKey=" + publicKey + "]";
	}
}
