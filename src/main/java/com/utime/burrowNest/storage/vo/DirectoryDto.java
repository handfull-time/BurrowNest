package com.utime.burrowNest.storage.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DirectoryDto {
    private String name;
    private String path;
    private boolean hasChildren;
    private boolean selected;
    
    List<DirectoryDto> subDirectories = new ArrayList<>();
    
    public DirectoryDto() {
    	this(null, null, false, false);
    }
    
    public DirectoryDto(String name, String path, boolean hasChildren, boolean selected){
    	this.name = name;
    	this.path = path;
    	this.hasChildren = hasChildren;
    	this.selected = selected;
    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DirectoryDto [");
		if (name != null) {
			builder.append("name=");
			builder.append(name);
			builder.append(", ");
		}
		if (path != null) {
			builder.append("path=");
			builder.append(path);
			builder.append(", ");
		}
		builder.append("hasChildren=");
		builder.append(hasChildren);
		builder.append(", selected=");
		builder.append(selected);
		builder.append(", ");
		if (subDirectories != null) {
			builder.append("\nsubDirectories=");
			builder.append(subDirectories);
		}
		builder.append("]\n");
		return builder.toString();
	}
    
    
}

