package com.utime.burrowNest.user.vo;

import java.util.List;

public class InitInforReqVo extends ReqUniqueVo{
	
	private String pw;
	
	private List<String> roots;
	
	private String wsUserName;

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

	public String getWsUserName() {
		return wsUserName;
	}

	public void setWsUserName(String wsUserName) {
		this.wsUserName = wsUserName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("InitInforReqVo [");
		if (pw != null)
			builder.append("pw=").append(pw).append(", ");
		if (roots != null)
			builder.append("roots=").append(roots).append(", ");
		if (wsUserName != null)
			builder.append("wsUserName=").append(wsUserName).append(", ");
		if (token != null)
			builder.append("token=").append(token).append(", ");
		if (ip != null)
			builder.append("ip=").append(ip).append(", ");
		if (rsaId != null)
			builder.append("rsaId=").append(rsaId).append(", ");
		if (publicKey != null)
			builder.append("publicKey=").append(publicKey);
		builder.append("]");
		return builder.toString();
	}
	
	
}
