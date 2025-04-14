package com.utime.burrowNest.storage.dao.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.utime.burrowNest.storage.dao.StorageDao;
import com.utime.burrowNest.storage.mapper.StorageBasicMapper;
import com.utime.burrowNest.storage.mapper.StorageMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
class StorageDaoImpl implements StorageDao{
	
	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	
	@Autowired
	private StorageBasicMapper basic;
	
	@Autowired
	private StorageMapper mapper;
	
	
}
