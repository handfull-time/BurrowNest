package com.utime.burrowNest.storage.dao.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Repository;

import com.utime.burrowNest.storage.dao.StorageDao;

@Repository
class StorageDaoImpl implements StorageDao{
	
	private final ExecutorService executor = Executors.newSingleThreadExecutor();
}
