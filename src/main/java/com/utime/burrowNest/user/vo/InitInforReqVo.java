package com.utime.burrowNest.user.vo;

import java.util.List;

public class InitInforReqVo extends ReqUniqueVo{
	
	private String pw;
	
	List<String> roots;

	public String getPw() {
		return pw;
	}

	public void setPw(String pw) {
		this.pw = pw;
	}

	public List<String> getRoots() {
		return roots;
	}

	public void setRoots(List<String> roots) {
		this.roots = roots;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("InitInforReq [");
		if (pw != null) {
			builder.append("pw=");
			builder.append(pw);
			builder.append(", ");
		}
		if (roots != null) {
			builder.append("roots=");
			builder.append(roots);
			builder.append(", ");
		}
		if (token != null) {
			builder.append("token=");
			builder.append(token);
			builder.append(", ");
		}
		if (ip != null) {
			builder.append("ip=");
			builder.append(ip);
			builder.append(", ");
		}
		if (rsaId != null) {
			builder.append("rsaId=");
			builder.append(rsaId);
			builder.append(", ");
		}
		if (publicKey != null) {
			builder.append("publicKey=");
			builder.append(publicKey);
		}
		builder.append("]");
		return builder.toString();
	}
	
	
}
