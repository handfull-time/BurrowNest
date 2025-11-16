package com.utime.burrowNest.storage.vo;

import lombok.Data;

@Data
public class StorageIOItem {
	/** true: file, false: directory */
	boolean file;
	
	/** uid */ 
	String uid;
}
