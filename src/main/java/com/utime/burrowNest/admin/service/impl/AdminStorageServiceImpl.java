package com.utime.burrowNest.admin.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.utime.burrowNest.admin.dao.AdminStorageDao;
import com.utime.burrowNest.admin.service.AdminStorageService;
import com.utime.burrowNest.common.util.BurrowUtils;
import com.utime.burrowNest.common.vo.ReturnBasic;
import com.utime.burrowNest.storage.vo.BnDirectory;
import com.utime.burrowNest.user.vo.UserVo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
class AdminStorageServiceImpl implements AdminStorageService{

	private final AdminStorageDao storageDao;
	
	@Override
	public BnDirectory getAdminTopStorage() {
		return storageDao.getRootDirectory();
	}
	
	@Override
	public List<BnDirectory> getAdminRootStorage() {
		return this.storageDao.getAdminRootStorage();
	}

	@Override
	public List<BnDirectory> getGroupStorageList(long groupNo) {
		
		//return this.storageDao.selectUnIncludeRootDirectories( groupNo );
		return this.storageDao.getRootDirectory( groupNo );
	}
	
	@Override
	public List<BnDirectory> getGroupStorageList(long groupNo, long dirNo) {
		List<BnDirectory> result = storageDao.getGroupStorageList( groupNo, dirNo );
		return result;
	}
	
	@Override
	public ReturnBasic removeGroupStorage(long groupNo, long dirNo) {
		final ReturnBasic result = new ReturnBasic();
		
		try {
			storageDao.removeGroupStorage( groupNo, dirNo );
		} catch (Exception e) {
			result.setCodeMessage("E", e.getMessage());
		}
		
		return result;
	}
	
	@Override
	public List<BnDirectory> getGroupStorageList(UserVo user, String uid) {
		
		if( BurrowUtils.isEmpty(uid) ) {
			log.info("루트 호출");
			return this.storageDao.getRootDirectory( user.getGroup().getGroupNo() );	
		}
		
		List<BnDirectory> result = storageDao.getGroupStorageList( user.getGroup().getGroupNo(), uid );
		return result;
	}
	
	@Override
	public List<BnDirectory> getOwnerGroupStorageList(long groupNo) {
		
		return storageDao.getOwnerGroupStorageList( groupNo );
	}
}
