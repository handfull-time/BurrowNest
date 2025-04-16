package com.utime.burrowNest.storage.dao.impl;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.utime.burrowNest.storage.dao.StorageDao;
import com.utime.burrowNest.storage.mapper.StorageBasicMapper;
import com.utime.burrowNest.storage.mapper.StorageMapper;
import com.utime.burrowNest.storage.vo.BnDirectory;
import com.utime.burrowNest.storage.vo.BnFile;
import com.utime.burrowNest.storage.vo.BnFileArchive;
import com.utime.burrowNest.storage.vo.BnFileAudio;
import com.utime.burrowNest.storage.vo.BnFileDocument;
import com.utime.burrowNest.storage.vo.BnFileImage;
import com.utime.burrowNest.storage.vo.BnFileVideo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
class StorageDaoImpl implements StorageDao{
	
	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	
	@Autowired
	private StorageBasicMapper basic;
	
	@Autowired
	private StorageMapper mapper;

	@Override
	public BnDirectory insertDirectory(File dir) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BnFile insertFile(File file) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int insertFileDocument(BnFileDocument file) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insertFileImage(BnFileImage file) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insertFileVideo(BnFileVideo file) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insertFileAudio(BnFileAudio file) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insertFileArchive(BnFileArchive file) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
}
