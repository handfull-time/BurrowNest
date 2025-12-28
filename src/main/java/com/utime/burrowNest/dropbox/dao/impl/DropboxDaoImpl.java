package com.utime.burrowNest.dropbox.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.dropbox.core.v2.files.FileMetadata;
import com.utime.burrowNest.common.mapper.CommonMapper;
import com.utime.burrowNest.dropbox.dao.DropboxDao;
import com.utime.burrowNest.dropbox.mapper.DropboxMapper;
import com.utime.burrowNest.user.vo.UserVo;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
class DropboxDaoImpl implements DropboxDao {
	
	private final CommonMapper common;
	private final DropboxMapper mapper;
	
	@PostConstruct
	private void init() {
		if( ! common.existTable("BN_DROPBOX_DATA") ) {
			mapper.createMetadataTable();
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int addMetadata(UserVo user, FileMetadata metadata) throws Exception {
		
		return mapper.insertMetadata(user.getUserNo(), metadata);
	}

	@Override
	public String getMetadataRev(UserVo user, FileMetadata metadata) {
		
		return mapper.selectMetadataRev(user.getUserNo(), metadata);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int modifyMetadataRev(UserVo user, FileMetadata metadata) throws Exception {
		
		return mapper.updateMetadataRev(user.getUserNo(), metadata);
	}
}
