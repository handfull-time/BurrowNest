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
	public boolean IsInit() {
		return mapper.IsInit();
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public BnDirectory InsertRootDirectory() throws Exception {
		
		if( basic.InsertRootDirectory() < 1 ) {
			log.warn("root 생성 실패");
			throw new Exception("Root 생성 실패");
		}
		
		return mapper.selectBnDirectoryByNo(1L);
	}

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
	public int saveFileInfor(BnFile file) throws Exception {

		final AbsBnFileInfo fileInfo = file.getInfo();
		if( fileInfo == null ) {
			return 0;
		}
		
		if( fileInfo.getFileNo() < 0L ) {
			fileInfo.setFileNo( file.getNo() );
		}
		
		int result = 0;
		
		switch( file.getFileType() ) {
		case Basic: result = 0; break;
		case Document: {
			if( mapper.existFileInfo( "BN_FILE_DOCUMENT", file.getNo() ) ) {
				result = mapper.updateBnFileDocument((BnFileDocument)fileInfo);
			}else {
				result = mapper.insertBnFileDocument((BnFileDocument)fileInfo);
			}
			break;
		}
		case Image: { 
			if( mapper.existFileInfo( "BN_FILE_IMAGE", file.getNo() ) ) {
				result = mapper.updateBnFileImage((BnFileImage)fileInfo);
			}else {
				result = mapper.insertBnFileImage((BnFileImage)fileInfo);
			}
			break;
		}
		case Video:{
			if( mapper.existFileInfo( "BN_FILE_VIDEO", file.getNo() ) ) {
				result = mapper.updateBnFileVideo((BnFileVideo)fileInfo);
			}else {
				result = mapper.insertBnFileVideo((BnFileVideo)fileInfo);
			}
			break;
		} 
		case Audio:{
			if( mapper.existFileInfo( "BN_FILE_AUDIO", file.getNo() ) ) {
				result = mapper.updateBnFileAudio((BnFileAudio)fileInfo);
			}else {
				result = mapper.insertBnFileAudio((BnFileAudio)fileInfo);
			}
			break;
		} 
		case Archive:{
			if( mapper.existFileInfo( "BN_FILE_ARCHIVE", file.getNo() ) ) {
				result = mapper.updateBnFileArchive((BnFileArchive)fileInfo);
			}else {
				result = mapper.insertBnFileArchive((BnFileArchive)fileInfo);
			}
			break;
		} 
		}// case end

		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int saveThumbnail(BnFile file, byte [] bArray) {
		
		int result;
		if( mapper.existFileInfo( "BN_FILE_THUMBNAIL", file.getNo() ) ) {
			result = mapper.insertThumbnail( file.getNo(), bArray );
		}else {
			result = mapper.updateThumbnail( file.getNo(), bArray );
		}
		return result;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int deleteDirectory(BnDirectory dir) throws Exception {
		
		return mapper.deleteBnDirectoryByNo(dir.getNo());
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int deleteFile(BnFile file) throws Exception {
		
		return mapper.deleteBnFileByNo(file.getNo());
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
	
	@Override
	public byte[] getThumbnail(String fid) {
		
		return mapper.selectThumbnail(fid);
	}

	
}
