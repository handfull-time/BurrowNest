package com.utime.burrowNest.storage.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.utime.burrowNest.storage.dao.StorageDao;
import com.utime.burrowNest.storage.mapper.StorageBasicMapper;
import com.utime.burrowNest.storage.mapper.StorageMapper;
import com.utime.burrowNest.storage.vo.AbsBnFileInfo;
import com.utime.burrowNest.storage.vo.BnDirectory;
import com.utime.burrowNest.storage.vo.BnFile;
import com.utime.burrowNest.storage.vo.BnFileArchive;
import com.utime.burrowNest.storage.vo.BnFileAudio;
import com.utime.burrowNest.storage.vo.BnFileDocument;
import com.utime.burrowNest.storage.vo.BnFileExtension;
import com.utime.burrowNest.storage.vo.BnFileImage;
import com.utime.burrowNest.storage.vo.BnFileVideo;
import com.utime.burrowNest.storage.vo.EBnFileType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
class StorageDaoImpl implements StorageDao{
	
	@Autowired
	private StorageBasicMapper basic;
	
	@Autowired
	private StorageMapper mapper;

	@Override
	public Map<String, EBnFileType> getBnFileType() {
		
		final Map<String, EBnFileType> result = new HashMap<>();
		final List<BnFileExtension> list = basic.selectFileExtensionAll();
		for( BnFileExtension item : list ) {
			result.put(item.getExtension(), item.getFileType());
		}
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int saveDirectory(BnDirectory dir) throws Exception {
		
		final int result;
		if( dir.getNo() < 0L ) {
			result = mapper.insertBnDirectory(dir);
		}else {
			result = mapper.updateBnDirectory(dir);
		}
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int saveFile(BnFile file) throws Exception {
		
		final int result;
		if( file.getNo() < 0L ) {
			result = mapper.insertBnFile(file);
		}else {
			result = mapper.updateBnFile(file);
		}
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int saveFileInfor(AbsBnFileInfo file) throws Exception {

		final int result;
		if( file instanceof BnFileImage ) {
			if( file.getFileNo() < 0L ) {
				result = mapper.insertBnFileImage((BnFileImage)file);
			}else {
				result = mapper.updateBnFileImage((BnFileImage)file);
			}
		}else if (file instanceof BnFileAudio ) {
			if( file.getFileNo() < 0L ) {
				result = mapper.insertBnFileAudio((BnFileAudio)file);
			}else {
				result = mapper.updateBnFileAudio((BnFileAudio)file);
			}
		}else if (file instanceof BnFileDocument ) {
			if( file.getFileNo() < 0L ) {
				result = mapper.insertBnFileDocument((BnFileDocument)file);
			}else {
				result = mapper.updateBnFileDocument((BnFileDocument)file);
			}
		}else if (file instanceof BnFileVideo ) {
			if( file.getFileNo() < 0L ) {
				result = mapper.insertBnFileVideo((BnFileVideo)file);
			}else {
				result = mapper.updateBnFileVideo((BnFileVideo)file);
			}
		}else if (file instanceof BnFileArchive ) {
			if( file.getFileNo() < 0L ) {
				result = mapper.insertBnFileArchive((BnFileArchive)file);
			}else {
				result = mapper.updateBnFileArchive((BnFileArchive)file);
			}
		}else {
			result = -1;
		}

		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int saveThumbnail(BnFile file, String base64) {
		
		int result;
		if( mapper.existThumbnail( file.getNo() ) ) {
			result = mapper.insertThumbnail( file.getNo(), base64 );
		}else {
			result = mapper.updateThumbnail( file.getNo(), base64 );
		}
		return result;
	}
	
	@Override
	public int deleteDirectory(BnDirectory dir) throws Exception {
		
		return mapper.deleteBnDirectoryByNo(dir.getNo());
	}
	
	@Override
	public int deleteFile(BnFile file) throws Exception {
		
		return mapper.deleteBnFileById(file.getNo());
	}

	@Override
	public BnDirectory getDirectory(long dirNo) {
		return mapper.selectBnDirectoryByNo(dirNo);
	}

	@Override
	public BnFile getFile(long fileNo) {
		return mapper.selectBnFileByNo(fileNo);
	}

	@Override
	public String getFileThumbnail(long fileNo) {
		return "data:image/jpeg;base64," + mapper.selectThumbnail(fileNo);
	}

	@Override
	public AbsBnFileInfo getFileInfor(BnFile file) {
		AbsBnFileInfo result = null;
		
		switch( file.getFileType() ) {
		case Basic: result = null; break;
		case Document: result = mapper.selectBnFileDocumentByFileNo(file.getNo()); break;
		case Image: result = mapper.selectBnFileImageByFileNo(file.getNo()); break;
		case Video: result = mapper.selectBnFileVideoByFileNo(file.getNo()); break;
		case Audio: result = mapper.selectBnFileAudioByFileNo(file.getNo()); break;
		case Archive: result = mapper.selectBnFileArchiveByFileNo(file.getNo()); break;
		}

		return result;
	}

	
}
