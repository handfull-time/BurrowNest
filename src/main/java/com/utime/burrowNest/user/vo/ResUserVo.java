package com.utime.burrowNest.user.vo;

import com.utime.burrowNest.common.vo.ReturnBasic;

public class ResUserVo extends ReturnBasic {

	private UserVo user;
	
	public ResUserVo(String c, String m) {
		super(c, m);
	}
	
	public ResUserVo() {
		super();
	}

	public UserVo getUser() {
		return user;
	}

	public void setUser(UserVo user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "ResUserVo [user=" + user + ", code=" + code + ", message=" + message + "]";
	}
	
}
