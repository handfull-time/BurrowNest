package com.utime.burrowNest.user.vo;

public class LoginReqVo extends ReqUniqueVo{
	
	private String id;
	private String pw;
	private String userAgent;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPw() {
		return pw;
	}
	public void setPw(String pw) {
		this.pw = pw;
	}
	
	public String getUserAgent() {
		return userAgent;
	}
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LoginReqVo [");
		if (id != null) {
			builder.append("id=");
			builder.append(id);
			builder.append(", ");
		}
		if (pw != null) {
			builder.append("pw=");
			builder.append(pw);
			builder.append(", ");
		}
		if (userAgent != null) {
			builder.append("userAgent=");
			builder.append(userAgent);
		}
		builder.append("]");
		return builder.toString();
	}
}
