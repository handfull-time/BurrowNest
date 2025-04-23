package com.utime.burrowNest.storage.vo;

public enum EOrientation {
	
	Horizontal(1, "Horizontal (normal)")
	,MirrorHorizontal(2, "Mirror horizontal")
	,Rotate180(3, "Rotate 180")
	,MirrorVertical(4, "Mirror vertical")
	,MirrorHorizontalR270CW (5, "Mirror horizontal and rotate 270 CW")
	,Rotate90CW(6, "Rotate 90 CW")
	,MirrorHorizontalR90CW(7, "Mirror horizontal and rotate 90 CW")
	,Rotate270CW(8, "Rotate 270 CW");
	
	private final int intVal;
	private final String dscr;
	
	private EOrientation(int i, String s) {
		this.intVal = i;
		this.dscr = s;
	}

	public int getIntVal() {
		return intVal;
	}

	public String getDscr() {
		return dscr;
	}
	
	
}
