package com.utime.burrowNest.storage.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DirectoryDto extends BnDirectory {

	private boolean selected;
    
    List<BnDirectory> childDirectories = new ArrayList<>();
    
    public DirectoryDto() {
    	this(null);
    }
    
    public DirectoryDto(BnDirectory dir){
    	if( dir != null ) {
    		this.creation = dir.creation;
    		this.lastModified = dir.lastModified;
    		this.regDate = dir.regDate;
    		this.updateDate = dir.updateDate;
    		this.enabled = dir.enabled;
    		this.name = dir.name;
    		this.no = dir.no;
    		this.ownerNo = dir.ownerNo;
    		this.parentNo = dir.parentNo;
    		this.uid = dir.uid;
    		this.publicAccessible = dir.publicAccessible;
    		this.hasChild = dir.hasChild;
    		this.absolutePath = dir.absolutePath;
    	}
    	this.selected = false;
    }
}

