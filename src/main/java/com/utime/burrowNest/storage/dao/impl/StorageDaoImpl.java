package com.utime.burrowNest.storage.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.utime.burrowNest.common.mapper.CommonMapper;
import com.utime.burrowNest.common.vo.BinResultVo;
import com.utime.burrowNest.storage.dao.StorageDao;
import com.utime.burrowNest.storage.mapper.StorageBasicMapper;
import com.utime.burrowNest.storage.mapper.StorageMapper;
import com.utime.burrowNest.storage.vo.AbsBnFileInfo;
import com.utime.burrowNest.storage.vo.AbsPath;
import com.utime.burrowNest.storage.vo.BnDirectory;
import com.utime.burrowNest.storage.vo.BnFile;
import com.utime.burrowNest.storage.vo.BnFileArchive;
import com.utime.burrowNest.storage.vo.BnFileAudio;
import com.utime.burrowNest.storage.vo.BnFileDocument;
import com.utime.burrowNest.storage.vo.BnFileExtension;
import com.utime.burrowNest.storage.vo.BnFileImage;
import com.utime.burrowNest.storage.vo.BnFileVideo;
import com.utime.burrowNest.storage.vo.BnPathAccess;
import com.utime.burrowNest.storage.vo.EBnFileType;
import com.utime.burrowNest.user.vo.UserVo;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
class StorageDaoImpl implements StorageDao{
	
	@Autowired
	private CommonMapper common;
	
	@Autowired
	private StorageBasicMapper basic;
	
	@Autowired
	private StorageMapper mapper;
	
	@PostConstruct
	private void postCunstruct() {
		if( common.existTable("BN_FILE_EXTENSION") ) {
			return;
		}
		
		int dbRes = 0;
		
		log.info("BN_FILE_EXTENSION 생성");
		dbRes += basic.CreateFileExtension();
		
		{
			dbRes += basic.insertFileExtension( "pdf",  EBnFileType.Document);
			dbRes += basic.insertFileExtension( "doc",  EBnFileType.Document);
			dbRes += basic.insertFileExtension( "docx", EBnFileType.Document);
			dbRes += basic.insertFileExtension( "xls",  EBnFileType.Document);
			dbRes += basic.insertFileExtension( "xlsx", EBnFileType.Document);
			dbRes += basic.insertFileExtension( "ppt",  EBnFileType.Document);
			dbRes += basic.insertFileExtension( "pptx", EBnFileType.Document);
			dbRes += basic.insertFileExtension( "hwp",  EBnFileType.Document);
			dbRes += basic.insertFileExtension( "hwpx",  EBnFileType.Document);
			dbRes += basic.insertFileExtension( "hwt",  EBnFileType.Document);
			dbRes += basic.insertFileExtension( "hwpml",  EBnFileType.Document);
			dbRes += basic.insertFileExtension( "txt",  EBnFileType.Document);
			dbRes += basic.insertFileExtension( "md",   EBnFileType.Document);
			dbRes += basic.insertFileExtension( "csv",  EBnFileType.Document);
		}
		{
			dbRes += basic.insertFileExtension( "jpg",  EBnFileType.Image);
			dbRes += basic.insertFileExtension( "jpeg", EBnFileType.Image);
			dbRes += basic.insertFileExtension( "png",  EBnFileType.Image);
			dbRes += basic.insertFileExtension( "gif",  EBnFileType.Image);
			dbRes += basic.insertFileExtension( "bmp",  EBnFileType.Image);
			dbRes += basic.insertFileExtension( "webp", EBnFileType.Image);
			dbRes += basic.insertFileExtension( "svg",  EBnFileType.Image);
			dbRes += basic.insertFileExtension( "tiff", EBnFileType.Image);
			dbRes += basic.insertFileExtension( "ico",  EBnFileType.Image);
			dbRes += basic.insertFileExtension( "cr2",  EBnFileType.Image);
			dbRes += basic.insertFileExtension( "nef",  EBnFileType.Image);
			dbRes += basic.insertFileExtension( "raw",  EBnFileType.Image);
		}
		{
			dbRes += basic.insertFileExtension( "mp4",  EBnFileType.Video);
			dbRes += basic.insertFileExtension( "mov",  EBnFileType.Video);
			dbRes += basic.insertFileExtension( "avi",  EBnFileType.Video);
			dbRes += basic.insertFileExtension( "mkv",  EBnFileType.Video);
			dbRes += basic.insertFileExtension( "wmv",  EBnFileType.Video);
			dbRes += basic.insertFileExtension( "flv",  EBnFileType.Video);
			dbRes += basic.insertFileExtension( "webm", EBnFileType.Video);
		}
		{
			dbRes += basic.insertFileExtension( "mp3",  EBnFileType.Audio);
			dbRes += basic.insertFileExtension( "wav",  EBnFileType.Audio);
			dbRes += basic.insertFileExtension( "flac", EBnFileType.Audio);
			dbRes += basic.insertFileExtension( "aac",  EBnFileType.Audio);
			dbRes += basic.insertFileExtension( "ogg",  EBnFileType.Audio);
			dbRes += basic.insertFileExtension( "m4a",  EBnFileType.Audio);
		}
		{
			dbRes += basic.insertFileExtension( "zip", EBnFileType.Archive);
			dbRes += basic.insertFileExtension( "rar", EBnFileType.Archive);
			dbRes += basic.insertFileExtension( "7z",  EBnFileType.Archive);
			dbRes += basic.insertFileExtension( "tar", EBnFileType.Archive);
			dbRes += basic.insertFileExtension( "gz",  EBnFileType.Archive);
			dbRes += basic.insertFileExtension( "jar", EBnFileType.Archive);
		}
		
		log.info("결과 : " + dbRes);
		
	}
	
	@Override
	public int initTable() throws Exception {
		int result = 0;
		
		if( ! common.existTable("BN_DIRECTORY") ) {
			log.info("BN_DIRECTORY 생성");
			result += basic.CreateDirectory();
			result += common.createIndex("BN_DIRECTORY_PARENT_NO_INDX", "BN_DIRECTORY", "PARENT_NO");
			result += common.createUniqueIndex("BN_DIRECTORY_UID_INDX", "BN_DIRECTORY", "UID");
			result += common.createIndex("BN_DIRECTORY_NAME_INDX", "BN_DIRECTORY", "NAME");
		}
		
		if( ! common.existTable("BN_DIRECTORY_ACCESS") ) {
			log.info("BN_DIRECTORY_ACCESS 생성");
			result += basic.CreateDirectoryAccess();
		}
		
		if( ! common.existTable("BN_FILE") ) {
			log.info("BN_FILE 생성");
			result += basic.CreateFile();
			result += common.createIndex("BN_FILE_PARENT_NO_INDX", "BN_FILE", "PARENT_NO");
			result += common.createUniqueIndex("BN_FILE_UID_INDX", "BN_FILE", "UID");
			result += common.createIndex("BN_FILE_NAME_INDX", "BN_FILE", "NAME");
		}

		if( ! common.existTable("BN_FILE_ACCESS") ) {
			log.info("BN_FILE_ACCESS 생성");
			result += basic.CreateFileAccess();
		}


		if( ! common.existTable("BN_FILE_THUMBNAIL") ) {
			log.info("BN_FILE_THUMBNAIL 생성");
			result += basic.CreateFileThumbnail();
		}

		if( ! common.existTable("BN_FILE_DOCUMENT") ) {
			log.info("BN_FILE_DOCUMENT 생성");
			result += basic.CreateFileDocument();
			result += common.createIndex("BN_FILE_DOCUMENT_TITLE_INDX", "BN_FILE_DOCUMENT", "TITLE");
			result += common.createIndex("BN_FILE_DOCUMENT_SUBJECT_INDX", "BN_FILE_DOCUMENT", "SUBJECT");
			result += common.createIndex("BN_FILE_DOCUMENT_CREATOR_INDX", "BN_FILE_DOCUMENT", "CREATOR");
			result += common.createIndex("BN_FILE_DOCUMENT_KEYWORDS_INDX", "BN_FILE_DOCUMENT", "KEYWORDS");
			result += common.createIndex("BN_FILE_DOCUMENT_CREATE_DATE_INDX", "BN_FILE_DOCUMENT", "CREATE_DATE");
		}

		if( ! common.existTable("BN_FILE_IMAGE") ) {
			log.info("BN_FILE_IMAGE 생성");
			result += basic.CreateFileImage();
			result += common.createIndex("BN_FILE_IMAGE_CAMERA_MODEL_NAME_INDX", "BN_FILE_IMAGE", "CAMERA_MODEL_NAME");
			result += common.createIndex("BN_FILE_IMAGE_CREATE_DATE_INDX", "BN_FILE_IMAGE", "CREATE_DATE");
			result += common.createIndex("BN_FILE_IMAGE_GPS_INDX", "BN_FILE_IMAGE", "GPS_LATITUDE,GPS_LONGITUDE");
		}

		if( ! common.existTable("BN_FILE_VIDEO") ) {
			log.info("BN_FILE_VIDEO 생성");
			result += basic.CreateFileVideo();
			result += common.createIndex("BN_FILE_VIDEO_AUTHOR_INDX", "BN_FILE_VIDEO", "AUTHOR");
			result += common.createIndex("BN_FILE_VIDEO_CREATE_DATE_INDX", "BN_FILE_VIDEO", "CREATE_DATE");
			result += common.createIndex("BN_FILE_VIDEO_GPS_INDX", "BN_FILE_VIDEO", "GPS_LATITUDE,GPS_LONGITUDE");
		}

		if( ! common.existTable("BN_FILE_AUDIO") ) {
			log.info("BN_FILE_AUDIO 생성");
			result += basic.CreateFileAudio();
			result += common.createIndex("BN_FILE_AUDIO_TITLE_INDX", "BN_FILE_AUDIO", "TITLE");
			result += common.createIndex("BN_FILE_AUDIO_ARTIST_INDX", "BN_FILE_AUDIO", "ARTIST");
			result += common.createIndex("BN_FILE_AUDIO_ALBUM_INDX", "BN_FILE_AUDIO", "ALBUM");
			result += common.createIndex("BN_FILE_AUDIO_GENRE_INDX", "BN_FILE_AUDIO", "GENRE");
		}

		if( ! common.existTable("BN_FILE_ARCHIVE") ) {
			log.info("BN_FILE_ARCHIVE 생성");
			result += basic.CreateFileArchive();
		}

		if( ! common.existTable("BN_FILE_DENIED_EXTENSION") ) {
			log.info("BN_FILE_DENIED_EXTENSION 생성");
			result += basic.CreateDeniedFileExtension();
		}

		return result;
	}
	
	@Override
	public boolean IsInit() {
		
		if( ! common.existTable("BN_DIRECTORY") ) {
			return false;
		}
		
		return mapper.IsInit();
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public BnDirectory InsertRootDirectory(UserVo owner ) throws Exception {
		
		if( basic.InsertRootDirectory(owner) < 1 ) {
			log.warn("root 생성 실패");
			throw new Exception("Root 생성 실패");
		}
		
		final BnDirectory result = mapper.selectDirectoryByNo(1L);

		this.insertAccess(owner, result);
		
		return result;
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
	public int saveDirectory(BnDirectory dir, UserVo owner ) throws Exception {
		
		final int result;
		if( dir.getNo() < 0L ) {
			result = mapper.insertDirectory(dir);
		}else {
			result = mapper.updateDirectory(dir);
		}
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int saveFile(BnFile file, UserVo owner) throws Exception {
		
		final int result;
		if( file.getNo() < 0L ) {
			result = mapper.insertFile(file);
		}else {
			result = mapper.updateFile(file);
		}
		return result;
	}

	@Transactional(rollbackFor = Exception.class)
	private int updateAccess(UserVo owner, AbsPath obj) throws Exception  {
		
		int result;
		if( obj instanceof BnFile ) {
			result = mapper.updateFileAccess(obj.getNo(), owner.getGroup().getGroupNo(), owner.getGroup().getAccType().getBit());
		}else {
			result = mapper.updateDirectoryAccess(obj.getNo(), owner.getGroup().getGroupNo(), owner.getGroup().getAccType().getBit());
		}
		
		return result;
	}

	@Transactional(rollbackFor = Exception.class)
	private int insertAccess(UserVo owner, AbsPath obj) throws Exception  {
		
		int result;
		if( obj instanceof BnFile ) {
			result = mapper.insertFileAccess(obj.getNo(), owner.getGroup().getGroupNo(), owner.getGroup().getAccType().getBit());
		}else {
			result = mapper.insertDirectoryAccess(obj.getNo(), owner.getGroup().getGroupNo(), owner.getGroup().getAccType().getBit());
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
				result = mapper.updateFileDocument((BnFileDocument)fileInfo);
			}else {
				result = mapper.insertFileDocument((BnFileDocument)fileInfo);
			}
			break;
		}
		case Image: { 
			if( mapper.existFileInfo( "BN_FILE_IMAGE", file.getNo() ) ) {
				result = mapper.updateFileImage((BnFileImage)fileInfo);
			}else {
				result = mapper.insertFileImage((BnFileImage)fileInfo);
			}
			break;
		}
		case Video:{
			if( mapper.existFileInfo( "BN_FILE_VIDEO", file.getNo() ) ) {
				result = mapper.updateFileVideo((BnFileVideo)fileInfo);
			}else {
				result = mapper.insertFileVideo((BnFileVideo)fileInfo);
			}
			break;
		} 
		case Audio:{
			if( mapper.existFileInfo( "BN_FILE_AUDIO", file.getNo() ) ) {
				result = mapper.updateFileAudio((BnFileAudio)fileInfo);
			}else {
				result = mapper.insertFileAudio((BnFileAudio)fileInfo);
			}
			break;
		} 
		case Archive:{
			if( mapper.existFileInfo( "BN_FILE_ARCHIVE", file.getNo() ) ) {
				result = mapper.updateFileArchive((BnFileArchive)fileInfo);
			}else {
				result = mapper.insertFileArchive((BnFileArchive)fileInfo);
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
		
		return mapper.deleteDirectoryByNo(dir.getNo());
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int deleteFile(BnFile file) throws Exception {
		
		return mapper.deleteFileByNo(file.getNo());
	}

	@Override
	public BnDirectory getDirectory(long dirNo) {
		return mapper.selectDirectoryByNo(dirNo);
	}
	
	@Override
	public BnDirectory getDirectory(UserVo user, String uid) {
		final BnDirectory result = mapper.selectDirectoryByGuid(user.getGroup(), uid);
		return result;
	}

	@Override
	public BnFile getFile(long fileNo) {
		final BnFile result = mapper.selectFileByNo(fileNo);
		
		if( result != null ) {
			result.setInfo( this.getFileInfor(result) );
		}
		
		return result;
	}
	
	@Override
	public BnFile getFile(UserVo user, String uid) {
		final BnFile result = mapper.selectFileByUid(uid);
		
		if( result != null ) {
			result.setInfo( this.getFileInfor(result) );
		}
		
		return result;
	}

	@Override
	public AbsBnFileInfo getFileInfor(BnFile file) {
		AbsBnFileInfo result = null;
		
		switch( file.getFileType() ) {
		case Basic: result = null; break;
		case Document: result = mapper.selectFileDocumentByFileNo(file.getNo()); break;
		case Image: result = mapper.selectFileImageByFileNo(file.getNo()); break;
		case Video: result = mapper.selectFileVideoByFileNo(file.getNo()); break;
		case Audio: result = mapper.selectFileAudioByFileNo(file.getNo()); break;
		case Archive: result = mapper.selectFileArchiveByFileNo(file.getNo()); break;
		}

		return result;
	}
	
	@Override
	public byte[] getThumbnail(String uid) {
		final BinResultVo result = mapper.selectThumbnail(uid);
		
		return result.getBinary();
	}

//	@Override
//	public DirectoryDto getRootDirectory(UserVo user) {
//		final BnDirectory directory = mapper.selectRootDirectory( user );
//		
//		if( directory == null ) {
//			return null;
//		}
//		
//		final DirectoryDto result = new DirectoryDto(directory);
//		
//		result.setChildDirectories( mapper.selectChildDirectory(user.getGroup(), directory.getNo() ));
//		result.setSelected(true);
//		
//		return result;
//	}
	
	@Override
	public List<BnDirectory> getRootDirectory(int groupNo) {
		final List<BnDirectory> directories = mapper.selectRootDirectories( groupNo );
		
		return directories;
	}

	@Override
	public List<BnFile> getFiles(UserVo user, BnDirectory dir) {
		return mapper.selectFiles(user.getGroup(), dir.getNo());
	}
	
	@Override
	public List<BnDirectory> getDirectories(UserVo user, BnDirectory dir) {
		
		return mapper.selectDirectories(user.getGroup(), dir.getNo());
	}
	
	@Override
	public List<String> getPaths(UserVo user, BnDirectory dir) {
		return mapper.selectPaths(user.getGroup(), dir.getNo());
	}
	
	@Override
	public BnDirectory getParentDirectory(UserVo user, String uid) {
		
		return null;
	}
	
	@Override
	public List<BnDirectory> getAllDirectory() {
		
		return mapper.selectAllDirectory();
	}
	
	@Override
	public List<BnPathAccess> getAllDirectoryAccess() {
		
		return mapper.selectBnDirectoryAccess();
	}
}
