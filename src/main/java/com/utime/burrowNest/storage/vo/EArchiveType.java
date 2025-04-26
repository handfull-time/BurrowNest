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
	
	public static EArchiveType getArchiveType( String ext ) {
		EArchiveType result = null;
		
		final EArchiveType [] values = EArchiveType.values();
		for( EArchiveType item : values ) {
			if( item.archive.equals(ext) ) {
				result = item;
				break;
			}
		}
		
		return result;
	}
}
