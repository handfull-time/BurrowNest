package com.utime.burrowNest.storage.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.utime.burrowNest.storage.mapper.BnPathAccess;
import com.utime.burrowNest.storage.vo.BnDirectory;
import com.utime.burrowNest.storage.vo.DirectoryDto;

public class DirecotryManager {

	final Map<Long, DirectoryDto> mapDirNoBnDirectory = new HashMap<>();
	final Map<String, DirectoryDto> mapUidBnDirectory = new HashMap<>();
	final Map<Long, BnPathAccess> mapDirNoPathAccess = new HashMap<>();
	final DirectoryDto root;
	
	public DirecotryManager(List<BnDirectory> directorylist, List<BnPathAccess> accessList) {
		
		final Map< Long, List<DirectoryDto> > mapListDirectory = new HashMap<>();
		final BnDirectory rootDir = directorylist.remove(0);
		
		this.root = new DirectoryDto( rootDir );
		
		this.init(directorylist, accessList, mapListDirectory);
		
		final Long rootNo = 1L;
		
		List<DirectoryDto> child = mapListDirectory.remove(rootNo);
		this.root.getChild().addAll(child);
		
		for( DirectoryDto sub : this.root.getChild() ) {
			this.procChild( sub, mapListDirectory);
		}
		
	}
	
	private void procChild( DirectoryDto dir, final Map< Long, List<DirectoryDto> > mapListDirectory ) {
		for( DirectoryDto item : dir.getChild() ) {
			final List<DirectoryDto> child = mapListDirectory.remove(item.getOwner().getNo());
			
			if( child == null || child.isEmpty() ) {
				continue;
			}
			
			item.getChild().addAll(child);
			
			for( DirectoryDto sub : item.getChild() ) {
				this.procChild( sub, mapListDirectory);
			}
		}
	}
	
	private void init(List<BnDirectory> directorylist, List<BnPathAccess> accessList, Map< Long, List<DirectoryDto> > mapListDirectory) {
		
		if( directorylist.size() < 1 )
			return;
		
		for( BnPathAccess item : accessList) {
			mapDirNoPathAccess.put(item.getNo(), item);
		}
		
		for( BnDirectory item : directorylist ) {
			final Long dirNo = item.getNo();
			final DirectoryDto dto = new DirectoryDto(item);
			
			this.mapDirNoBnDirectory.put(dirNo, dto);
			this.mapUidBnDirectory.put(item.getUid(), dto);
			
			final List<DirectoryDto> child;
			final Long parentNo = item.getParentNo();
			
			if( mapListDirectory.containsKey(parentNo) ) {
				child = mapListDirectory.get(parentNo);
			}else {
				child = new ArrayList<>();
				mapListDirectory.put(parentNo, child);
			}
			
			child.add(dto);
		}
	}
	
	public DirectoryDto allDirecotry() {
		return this.root;
	}
}
