package com.utime.burrowNest.storage.vo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RenameItem extends StorageIOItem {
	
	/** new name */ 
	String name;
	
	@Override
	public String toString() {
		
		return "RenameItem [file=" + isFile() + ", uid=" + getUid() + ", name=" + name + "]";
	}
}
