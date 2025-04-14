package com.utime.burrowNest.storage.vo;

public enum EArchiveType {
	ZIP("zip")
	, _7Z("7z")
	, RAR("rar")
	, TAR("tar")
	, GZ("gz")
	;
	
	final String archive;
	
	private EArchiveType(String s) {
		this.archive = s;
	}
	
	public String getArchive() {
		return archive;
	}
}
