package com.utime.burrowNest.storage.vo;

import lombok.Data;

@Data
public class FileDto {
	private String name;
    private long size;
    private boolean isDirectory;
    private String contentType;
    private String lastModified;
    
    public FileDto() {
		this(null, 0L, false, "", "-");
	}
    
    public FileDto(String name, long size, boolean isDirectory, String contentType, String lastModified) {
		this.name = name;
		this.size = size;
		this.isDirectory = isDirectory;
		this.contentType = contentType;
		this.lastModified = lastModified;
	}
}
