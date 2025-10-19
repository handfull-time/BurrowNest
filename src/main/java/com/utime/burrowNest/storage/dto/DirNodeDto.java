package com.utime.burrowNest.storage.dto;

import java.util.List;

import lombok.Data;

@Data
public class DirNodeDto {
	
	@Data
	public static class Owner {
        public String uid;
        public String name;
    }
	
    private Owner owner;         // { uid, name }
    private boolean hasChild;    // 자식 있는지
    private boolean selected;    // 강조 표시용 (선택 노드)
    private List<DirNodeDto> child; // 지연로딩 전에는 null/빈배열
    
    public void setOwner( String uid, String name) {
    	if( this.owner == null ) {
    		this.owner = new Owner();
    	}
    	
    	this.owner.uid = uid;
    	this.owner.name = name;
    }
}