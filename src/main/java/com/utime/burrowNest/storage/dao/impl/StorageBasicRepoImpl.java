package com.utime.burrowNest.storage.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.utime.burrowNest.common.mapper.CommonMapper;
import com.utime.burrowNest.storage.mapper.StorageBasicMapper;
import com.utime.burrowNest.storage.vo.EBnFileType;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
class StorageBasicRepoImpl {

	@Autowired
	private CommonMapper common;
	
	@Autowired
	private StorageBasicMapper mapper;
	
	@PostConstruct
	private void construct() {
		try {
			
			if( ! common.existTable("BN_DIRECTORY") ) {
				log.info("BN_DIRECTORY 생성");
				mapper.CreateDirectory();
				common.createIndex("BN_DIRECTORY_PARENT_NO_INDX", "BN_DIRECTORY", "PARENT_NO");
				common.createUniqueIndex("BN_DIRECTORY_UID_INDX", "BN_DIRECTORY", "UID");
				common.createIndex("BN_DIRECTORY_NAME_INDX", "BN_DIRECTORY", "NAME");
			}
			
			if( ! common.existTable("BN_DIRECTORY_ACCESS") ) {
				log.info("BN_DIRECTORY_ACCESS 생성");
				mapper.CreateDirectoryAccess();
			}

			if( ! common.existTable("BN_FILE") ) {
				log.info("BN_FILE 생성");
				mapper.CreateFile();
				common.createIndex("BN_FILE_PARENT_NO_INDX", "BN_FILE", "PARENT_NO");
				common.createUniqueIndex("BN_FILE_UID_INDX", "BN_FILE", "UID");
				common.createIndex("BN_FILE_NAME_INDX", "BN_FILE", "NAME");
			}

			if( ! common.existTable("BN_FILE_ACCESS") ) {
				log.info("BN_FILE_ACCESS 생성");
				mapper.CreateFileAccess();
			}

			if( ! common.existTable("BN_FILE_EXTENSION") ) {
				log.info("BN_FILE_EXTENSION 생성");
				mapper.CreateFileExtension();
				
				{
					mapper.insertFileExtension( "pdf" , EBnFileType.Document);
					mapper.insertFileExtension( "doc" , EBnFileType.Document);
					mapper.insertFileExtension( "docx", EBnFileType.Document);
					mapper.insertFileExtension( "xls" , EBnFileType.Document);
					mapper.insertFileExtension( "xlsx", EBnFileType.Document);
					mapper.insertFileExtension( "ppt" , EBnFileType.Document);
					mapper.insertFileExtension( "pptx", EBnFileType.Document);
					mapper.insertFileExtension( "hwp" , EBnFileType.Document);
				}
				{
					mapper.insertFileExtension( "jpg" , EBnFileType.Image);
					mapper.insertFileExtension( "jpeg", EBnFileType.Image);
					mapper.insertFileExtension( "png" , EBnFileType.Image);
					mapper.insertFileExtension( "gif" , EBnFileType.Image);
					mapper.insertFileExtension( "bmp" , EBnFileType.Image);
					mapper.insertFileExtension( "webp", EBnFileType.Image);
					mapper.insertFileExtension( "svg" , EBnFileType.Image);
					mapper.insertFileExtension( "tiff", EBnFileType.Image);
					mapper.insertFileExtension( "ico" , EBnFileType.Image);
					mapper.insertFileExtension( "cr2" , EBnFileType.Image);
				}
				{
					mapper.insertFileExtension( "mp4" , EBnFileType.Video);
					mapper.insertFileExtension( "mov" , EBnFileType.Video);
					mapper.insertFileExtension( "avi" , EBnFileType.Video);
					mapper.insertFileExtension( "mkv" , EBnFileType.Video);
					mapper.insertFileExtension( "wmv" , EBnFileType.Video);
					mapper.insertFileExtension( "flv" , EBnFileType.Video);
					mapper.insertFileExtension( "webm", EBnFileType.Video);
				}
				{
					mapper.insertFileExtension( "mp3" , EBnFileType.Audio);
					mapper.insertFileExtension( "wav" , EBnFileType.Audio);
					mapper.insertFileExtension( "flac", EBnFileType.Audio);
					mapper.insertFileExtension( "aac" , EBnFileType.Audio);
					mapper.insertFileExtension( "ogg" , EBnFileType.Audio);
					mapper.insertFileExtension( "m4a" , EBnFileType.Audio);
				}
				{
					mapper.insertFileExtension( "zip", EBnFileType.Archive);
					mapper.insertFileExtension( "rar", EBnFileType.Archive);
					mapper.insertFileExtension( "7z" , EBnFileType.Archive);
					mapper.insertFileExtension( "tar", EBnFileType.Archive);
					mapper.insertFileExtension( "gz" , EBnFileType.Archive);
					mapper.insertFileExtension( "jar", EBnFileType.Archive);
				}
			}

			if( ! common.existTable("BN_FILE_THUMBNAIL") ) {
				log.info("BN_FILE_THUMBNAIL 생성");
				mapper.CreateFileThumbnail();
			}

			if( ! common.existTable("BN_FILE_DOCUMENT") ) {
				log.info("BN_FILE_DOCUMENT 생성");
				mapper.CreateFileDocument();
				common.createIndex("BN_FILE_DOCUMENT_TITLE_INDX", "BN_FILE_DOCUMENT", "TITLE");
				common.createIndex("BN_FILE_DOCUMENT_SUBJECT_INDX", "BN_FILE_DOCUMENT", "SUBJECT");
				common.createIndex("BN_FILE_DOCUMENT_CREATOR_INDX", "BN_FILE_DOCUMENT", "CREATOR");
				common.createIndex("BN_FILE_DOCUMENT_KEYWORDS_INDX", "BN_FILE_DOCUMENT", "KEYWORDS");
				common.createIndex("BN_FILE_DOCUMENT_CREATE_DATE_INDX", "BN_FILE_DOCUMENT", "CREATE_DATE");
			}

			if( ! common.existTable("BN_FILE_IMAGE") ) {
				log.info("BN_FILE_IMAGE 생성");
				mapper.CreateFileImage();
				common.createIndex("BN_FILE_IMAGE_CAMERA_MODEL_NAME_INDX", "BN_FILE_IMAGE", "CAMERA_MODEL_NAME");
				common.createIndex("BN_FILE_IMAGE_CAMERA_MANUFACTURER_INDX", "BN_FILE_IMAGE", "CAMERA_MANUFACTURER");
				common.createIndex("BN_FILE_IMAGE_CREATE_DATE_INDX", "BN_FILE_IMAGE", "CREATE_DATE");
				common.createIndex("BN_FILE_IMAGE_GPS_INDX", "BN_FILE_IMAGE", "GPS_LATITUDE,GPS_LONGITUDE");
			}

			if( ! common.existTable("BN_FILE_VIDEO") ) {
				log.info("BN_FILE_VIDEO 생성");
				mapper.CreateFileVideo();
				common.createIndex("BN_FILE_VIDEO_AUTHOR_INDX", "BN_FILE_VIDEO", "AUTHOR");
				common.createIndex("BN_FILE_VIDEO_CREATE_DATE_INDX", "BN_FILE_VIDEO", "CREATE_DATE");
				common.createIndex("BN_FILE_VIDEO_GPS_INDX", "BN_FILE_VIDEO", "GPS_LATITUDE,GPS_LONGITUDE");
			}

			if( ! common.existTable("BN_FILE_AUDIO") ) {
				log.info("BN_FILE_AUDIO 생성");
				mapper.CreateFileAudio();
				common.createIndex("BN_FILE_AUDIO_TITLE_INDX", "BN_FILE_AUDIO", "TITLE");
				common.createIndex("BN_FILE_AUDIO_ARTIST_INDX", "BN_FILE_AUDIO", "ARTIST");
				common.createIndex("BN_FILE_AUDIO_ALBUM_INDX", "BN_FILE_AUDIO", "ALBUM");
				common.createIndex("BN_FILE_AUDIO_GENRE_INDX", "BN_FILE_AUDIO", "GENRE");
			}

			if( ! common.existTable("BN_FILE_ARCHIVE") ) {
				log.info("BN_FILE_ARCHIVE 생성");
				mapper.CreateFileArchive();
			}

			if( ! common.existTable("BN_FILE_DENIED_EXTENSION") ) {
				log.info("BN_FILE_DENIED_EXTENSION 생성");
				mapper.CreateDeniedFileExtension();
			}

		} catch (Exception e) {
			log.error("", e);
		}
	}
}
