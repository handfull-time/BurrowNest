package com.utime.burrowNest.storage.vo;

import java.util.ArrayList;
import java.util.List;

import com.utime.burrowNest.common.util.BurrowUtils;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DirectoryDto extends BnDirectory {

	boolean selected;
    
    List<BnDirectory> childs = new ArrayList<>();
    
    public DirectoryDto() {
    	this(null);
    }
    
    public DirectoryDto(BnDirectory dir){
    	if( dir != null ) {
    		this.creation = dir.creation;
    		this.lastModified = dir.lastModified;
    		this.name = dir.name;
    		this.ownerNo = dir.ownerNo;
    		this.parentNo = dir.parentNo;
    		this.uid = dir.uid;
    		this.publicAccessible = dir.publicAccessible;
    		this.hasChild = dir.hasChild;
    		this.absolutePath = dir.absolutePath;
    	}
    	this.selected = false;
    }
    
	@Override
	public String toString() {
		return BurrowUtils.toJson(this);
	}
}

