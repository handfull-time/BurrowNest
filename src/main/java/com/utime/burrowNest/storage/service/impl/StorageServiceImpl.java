package com.utime.burrowNest.storage.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.utime.burrowNest.storage.dao.StorageDao;
import com.utime.burrowNest.storage.service.StorageService;
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
public class StorageServiceImpl implements StorageService {
	
	final static ObjectWriter objMapper = new ObjectMapper().writerWithDefaultPrettyPrinter();
	
	private final UserDao userDao;

	private final StorageDao storageDao;
	
	private Map<String, EBnFileType> mapFileType;
	
	/**
	 * ApplicationReadyEvent
	 */
	@EventListener(ApplicationReadyEvent.class)
	protected void handleApplicationReadyEvent() {
		this.mapFileType = storageDao.getBnFileType();
	}

	@Override
	public byte[] getThumbnail(String fid) {
		return storageDao.getThumbnail( fid );
	}

	@Override
	public DirectoryDto getRootDirectory(UserVo user) {
		final DirectoryDto result = null;
		
//		storageDao.get
		/*
	SELECT 
		DR.NO 
		, DR.REG_DATE 
		, DR.UPDATE_DATE 
		, DR.UID 
		, DR.PARENT_NO	
		, DR.OWNER_NO 
		, DR.HAS_CHILD	
		, DR.CREATION 
		, DR.LAST_MODIFIED 
		, DR.NAME CHARACTER 
		, DR.ABSOLUTE_PATH	
		, DA.ACCESS_FLAGS
	FROM BN_DIRECTORY DR
	INNER JOIN BN_DIRECTORY_ACCESS DA
	    ON DR.NO = DA.DIR_NO
	INNER JOIN BN_USER_GROUP GR
	    ON GR.NO = DA.GROUP_NO
	WHERE 1=1
	    AND GR.NO = 1
	    AND BITAND( DA.ACCESS_FLAGS, 1) = 1
		AND DR.ENABLED = TRUE
		AND GR.ENABLED = TRUE
	ORDER BY GR.NO

	-- SELECT * FROM BN_USER_GROUP;

	-- SELECT * FROM BN_DIRECTORY_ACCESS ;
		*/
		return result;
	}

	@Override
	public DirectoryDto getDirectory(UserVo user, String path) {
		final DirectoryDto result = null;
		
		return result;
	}

	@Override
	public List<BnFile> getFiles(UserVo user, BnDirectory dir) {
		// TODO Auto-generated method stub
		return null;
	}
}
