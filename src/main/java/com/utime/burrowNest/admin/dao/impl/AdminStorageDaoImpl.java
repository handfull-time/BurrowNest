package com.utime.burrowNest.admin.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.utime.burrowNest.admin.dao.AdminStorageDao;
import com.utime.burrowNest.admin.mapper.AdminStorageMapper;
import com.utime.burrowNest.storage.vo.BnDirectory;

@Repository
class AdminStorageDaoImpl implements AdminStorageDao{
	
	@Autowired
	private AdminStorageMapper mapper;
	
	@Override
	public BnDirectory getRootDirectory() {
		return mapper.selectRootDirectory();
	}
	
	@Override
	public List<BnDirectory> getAdminRootStorage() {
		
		return mapper.selectBnDirectoryParentNo(1L);
	}
	
	@Override
	public List<BnDirectory> selectUnIncludeRootDirectories(long groupNo) {
		final List<BnDirectory> directories = mapper.selectUnIncludeRootDirectories( groupNo );
		
		return directories;
	}
	
	@Override
	public List<BnDirectory> getRootDirectory(long groupNo) {
		final List<BnDirectory> directories = mapper.selectRootDirectories( groupNo );
		
		return directories;
	}
	
	@Override
	public List<BnDirectory> getGroupStorageList(long groupNo, long dirNo) {
		
		if( dirNo < 1L ) {
			BnDirectory dir = this.getRootDirectory();
			dirNo = dir.getNo();
		}
		
		return mapper.getGroupStorageList(groupNo, dirNo);
	}
	
	@Override
	public List<BnDirectory> getGroupStorageList(long groupNo, String parentUid) {
		
		return mapper.getGroupStorageListUid(groupNo, parentUid);
	}

	@Override
	public int removeGroupStorage(long groupNo, long dirNo) throws Exception {
		return mapper.removeGroupStorage(groupNo, dirNo);
	}
	
	@Override
	public List<BnDirectory> getOwnerGroupStorageList(long groupNo) {
		
		return mapper.getOwnerGroupStorageList(groupNo);
	}
}
