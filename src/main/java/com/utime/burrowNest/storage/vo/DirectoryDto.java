package com.utime.burrowNest.storage.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DirectoryDto {

	private boolean selected;
	
	private final BnDirectory owner;
    
    List<DirectoryDto> child = new ArrayList<>();
    
    public DirectoryDto() {
    	this(null);
    }
    
    public DirectoryDto(BnDirectory dir){
   		this.owner = dir;
    	this.selected = false;
    }
}

