package com.utime.burrowNest.storage.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.utime.burrowNest.common.util.BurrowUtils;
import com.utime.burrowNest.common.vo.ReturnBasic;
import com.utime.burrowNest.storage.dao.StorageDao;
import com.utime.burrowNest.storage.dto.ChildrenResponse;
import com.utime.burrowNest.storage.dto.DirContextResponse;
import com.utime.burrowNest.storage.dto.DirNodeDto;
import com.utime.burrowNest.storage.mapper.DirectoryMapper;
import com.utime.burrowNest.storage.mapper.row.DirNodeRow;
import com.utime.burrowNest.storage.service.StorageService;
import com.utime.burrowNest.storage.vo.AbsPath;
import com.utime.burrowNest.storage.vo.BnDirectory;
import com.utime.burrowNest.storage.vo.BnFile;
import com.utime.burrowNest.storage.vo.DirectoryDto;
import com.utime.burrowNest.storage.vo.EBnFileType;
import com.utime.burrowNest.user.dao.UserDao;
import com.utime.burrowNest.user.vo.UserVo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
class StorageServiceImpl implements StorageService {
	
	private final StorageDao storageDao;
	
	private Map<String, EBnFileType> mapFileType;
	
	private final ExecutorService executorThumbnail = Executors.newSingleThreadExecutor();
	
	/**
	 * ApplicationReadyEvent
	 */
	@EventListener(ApplicationReadyEvent.class)
	protected void handleApplicationReadyEvent() {
		this.mapFileType = storageDao.getBnFileType();
	}
	
	@EventListener(ContextClosedEvent.class)
	protected void onShutdown() {
		executorThumbnail.shutdown();
		
		try {
			executorThumbnail.awaitTermination(10, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			log.error("", e);
		}
		
		if( ! executorThumbnail.isShutdown() ) {
			executorThumbnail.shutdownNow();
		}
    }
	
	/**
	 * 기본 관리자 계정의 최상위 Root를 생성한다.
	 */
	@Override
	public ReturnBasic saveRootStorage(UserVo user) {
		final ReturnBasic result = new ReturnBasic();
		
		try {
			// 관련 테이블 생성
			this.storageDao.initStorageTable();
		} catch (Exception e) {
			log.error("", e);
			result.setCodeMessage("E", "기본 Storage Table 생성 실패");
			return result;
		}

		try {
			storageDao.addRootDirectory(user);
		} catch (Exception e) {
			log.error("", e);
			result.setCodeMessage("E", "루트 생성 실패");
		}
		return result;
	}
	
	@Override
	public byte[] getThumbnail(UserVo user, String uid) {
		
		final byte[] result = storageDao.getThumbnail( uid );
		
		if( result == null ) {
			executorThumbnail.execute( () -> {
				final BnFile file = storageDao.getFile(user, uid);
				if( file != null ) {
					
				}
			});
		}
				
		return result;
	}

//	@Override
//	public List<DirectoryDto> getRootDirectory(UserVo user) {
//		List<DirectoryDto> result = new ArrayList<>();
//		List<BnDirectory> list = storageDao.getAdminRootStorage();
//		for( BnDirectory item : list ) {
//			final DirectoryDto add = new DirectoryDto(item);
//			result.add(add);
//		}
//		
//		return result; 
//	}
	
	@Override
	public BnDirectory getParentDirectory(UserVo user, String uid) {
		final BnDirectory result = this.storageDao.getParentDirectory(user, uid);
		
		return result;
	}

	@Override
	public List<AbsPath> getFiles(UserVo user, String uid) {
		
		final long groupNo = user.getGroup().getGroupNo();
		final List<AbsPath> result = new ArrayList<>();
		
		// groupNo, directoryUid 로 Directory 목록 조회
		final List<BnDirectory> dirList = this.storageDao.getDirectories( groupNo, uid );
		result.addAll( dirList );
		
		// groupNo, directoryUid 로 파일 목록 조회
		final List<BnFile> fileList = this.storageDao.getFiles( groupNo, uid );
		result.addAll( fileList );
		
		return result;
	}
	
	@Override
	public BnFile getFile(UserVo user, String uid) {
		
		final BnFile file = this.storageDao.getFile( user, uid );
		
		return file;
	}

	@Override
	public List<BnDirectory> getGroupStorageList(UserVo user, String uid) {
		
		List<BnDirectory> result;
		
		if( BurrowUtils.isEmpty(uid) ) {
			log.info("루트 호출");
			result = this.storageDao.getRootDirectory( user.getGroup().getGroupNo() );
		}else {
			result = storageDao.getGroupStorageList( user.getGroup().getGroupNo(), uid );
		}
		
		return result;
	}
	
}


