package com.utime.burrowNest.storage.service.impl;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.utime.burrowNest.common.util.CommandUtil;
import com.utime.burrowNest.common.vo.ReturnBasic;
import com.utime.burrowNest.storage.dao.StorageDao;
import com.utime.burrowNest.storage.service.StorageService;
import com.utime.burrowNest.storage.vo.AbsBnFileInfo;
import com.utime.burrowNest.storage.vo.BnDirectory;
import com.utime.burrowNest.storage.vo.BnFile;
import com.utime.burrowNest.storage.vo.EAccessType;
import com.utime.burrowNest.storage.vo.EBnFileType;
import com.utime.burrowNest.storage.vo.MessageDataVo;
import com.utime.burrowNest.user.dao.UserDao;
import com.utime.burrowNest.user.vo.InitInforReqVo;
import com.utime.burrowNest.user.vo.UserVo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {
	
	private final SimpMessagingTemplate messagingTemplate;
	
	final static ObjectWriter objMapper = new ObjectMapper().writerWithDefaultPrettyPrinter();
	
	private final ExecutorService executor = Executors.newWorkStealingPool();
	
	private final UserDao userDao;

	private final StorageDao storageDao;
	
	private Map<String, EBnFileType> mapFileType;
	
	private void delay() {
		try {Thread.sleep(200);} catch (InterruptedException e) {e.printStackTrace();}
	}
	
	/**
	 * ApplicationReadyEvent
	 */
	@EventListener(ApplicationReadyEvent.class)
	protected void handleApplicationReadyEvent() {
		this.mapFileType = storageDao.getBnFileType();
	}
	
	@Override
	public boolean IsInit() {
		
		return storageDao.IsInit();
	}
	
    /**
     * Front에 웹 소켓 키워드
     */
    final String KeyToWsFileRecieveStatus = "/toFront/RecieveStatus";
    
    private class InitFileLoad{
        final AtomicLong counterDir = new AtomicLong(0);
        final AtomicLong counterFile = new AtomicLong(0);
        final MessageDataVo message = new MessageDataVo();
        final String wsUserName;
        final UserVo owner;
        
        public InitFileLoad(String userName, UserVo owner) {
			this.wsUserName = userName;
			this.owner = owner;
		}
    }
    
    /*
		AtomicLong counter = new AtomicLong(0);
		counter.incrementAndGet();// 증가
		counter.decrementAndGet();// 감소
		long value = counter.get();// 현재 값 읽기
		counter.set(100L);// 값 설정
     */
    
    private class LoadFile implements Runnable{
    	private final File file;
    	private final InitFileLoad ifl;
    	private final BnDirectory parent;
    	private final EAccessType at;
    	
    	public LoadFile(File file, InitFileLoad ifl, BnDirectory parent, EAccessType at) {
			this.file = file;
			this.ifl = ifl;
			this.parent = parent;
			this.at = at;
		}
    	
    	@Override
    	public void run() {
    		// 파일 추가 및 확장자 분석.
    		final long fileCount = this.ifl.counterFile.incrementAndGet();
    		
    		{
    			// 파일 읽을 때도 가끔 메시지 쏘자.
        		if( fileCount % 10L == 0L ) {
        			ifl.message.setProgress( fileCount );
        			messagingTemplate.convertAndSendToUser(ifl.wsUserName, KeyToWsFileRecieveStatus, ifl.message);
        		}
    		}
    		
    		final BnFile bnFile;
    		try {
				bnFile = StorageUtils.getFileInfo(file);
			} catch (Exception e) {
				log.error("", e);
				return;
			}
    		
    		bnFile.setParentNo(this.parent.getNo());
    		bnFile.setEnabled(true);
    		bnFile.setOwnerNo(ifl.owner.getUserNo());
    		
    		final EBnFileType fileType = mapFileType.containsKey( bnFile.getExtension() )? mapFileType.get(bnFile.getExtension()):EBnFileType.Basic;
    		bnFile.setFileType(fileType);
    		
    		try {
				if( storageDao.saveFile(bnFile, ifl.owner, at) < 0 ) {
					log.warn("파일 저장 실패: " + bnFile);
					return;
				}
			} catch (Exception e) {
				log.error("", e);
				return;
			}
    		
    		{
    			// 파일 섬네일 추출
    			try {
    				final byte [] thumbnail = StorageUtils.getFileThumbnail(file, bnFile);
    				if( thumbnail != null ) {
    					storageDao.saveThumbnail(bnFile, thumbnail);
    				}
				} catch (Exception e) {
					log.error("섬네일 실패", e);
				}
    		}
    		
    		{
    			// 확장 정보 저장
        		AbsBnFileInfo fileInfo = null;
        		try {
            		switch( fileType ) {
            		case Basic: fileInfo = null; break;
            		case Document: fileInfo = StorageUtils.getFileInfoDocument(file, bnFile); break;
            		case Image: fileInfo = StorageUtils.getFileInfoImage(file, bnFile); break;
            		case Video: fileInfo = StorageUtils.getFileInfoVideo(file, bnFile); break;
            		case Audio: fileInfo = StorageUtils.getFileInfoAudio(file, bnFile); break;
            		case Archive: fileInfo = StorageUtils.getFileInfoArchive(file, bnFile); break;
            		}
    			} catch (Exception e) {
    				log.error("확장 정보 추출 실패", e);
    				fileInfo = null;
    			}
        		
        		if( fileInfo != null ) {
        			bnFile.setInfo(fileInfo);
        			try {
    					storageDao.saveFileInfor(bnFile);
    				} catch (Exception e) {
    					log.error("확장 정보 저장 실패", e);
    				}
        		}
    		}
    		
    	}
    }
    
    private void procLoadDir(File f, InitFileLoad ifl, BnDirectory parent, EAccessType at ) {
    	
    	final File [] files = f.listFiles();
    	if( files == null || files.length < 1 ) {
    		return;
    	}
    	
		{
			final File[] tmp = f.listFiles(file -> file.isFile());
			final int count = tmp != null ? tmp.length : 0;
			if( count > 0 ) ifl.counterDir.addAndGet( count );
		}
    	
    	ifl.message.setMessage("Loading... [" + f.getName() + "]");
    	ifl.message.setTotal( ifl.counterDir.get() );
    	ifl.message.setProgress( ifl.counterFile.get() );
		messagingTemplate.convertAndSendToUser(ifl.wsUserName, KeyToWsFileRecieveStatus, ifl.message);

		final long parentDirNo = parent.getNo();
		final int ownerUserNo = ifl.owner.getUserNo();
		final UserVo owner = ifl.owner;
		
		for( File file : files ) {
			if( file.isDirectory() ) {
				// directory 추가.
				final BnDirectory childDir;
				
				try {
					childDir = StorageUtils.getDirectoryInfo(file);
					childDir.setEnabled(true);
					childDir.setPublicAccessible(true);
					childDir.setParentNo( parentDirNo );
					childDir.setOwnerNo( ownerUserNo );
					if( storageDao.saveDirectory( childDir, owner, at ) < 1 ) {
						log.warn("Dir 저장 실패: " + childDir);
					};
					
				} catch (Exception e) {
					log.error("", e);
					continue;
				}

				this.procLoadDir( file, ifl, childDir, at );
			}else {
				// 파일 추가.
				executor.execute( new LoadFile( file, ifl, parent, at ) );
			}
		}
    }
    
    @SuppressWarnings("unused")
	private class LibDownLoad implements Runnable{
    	final InitInforReqVo req;
    	final InitFileLoad ifl;
    	
    	public LibDownLoad(InitInforReqVo req, InitFileLoad ifl) {
			this.req = req;
			this.ifl = ifl;
		}
    	
    	@Override
    	public void run() {
        	final MessageDataVo message = ifl.message;

        	final String os = System.getProperty("os.name").toLowerCase();
    		log.info( "현재 OS: " + os );
            
            if (os.contains("windows")) {
            	message.setMessage("파일 로딩 준비");
        		
        		messagingTemplate.convertAndSendToUser(ifl.wsUserName, KeyToWsFileRecieveStatus, message);
        		delay();
            	new Thread( new BeginFileLoad(req, ifl) ).start();
            	return;
            }
            
            {
            	// ffmpeg 설치 여부 확인
                final List<String> result = CommandUtil.workExe("ffmpeg", "-version");

                if (result.isEmpty() || result.get(0).contains("command not found")) {
            		log.info( "ffmpeg 설치 진행" );
                    message.setMessage("ffmpeg를 설치합니다...");
            		
            		messagingTemplate.convertAndSendToUser(ifl.wsUserName, KeyToWsFileRecieveStatus, message);
            		delay();
                    
                    // ffmpeg 설치 명령 실행
            		final List<String> installResult = CommandUtil.workExe("apt", "install", "-y", "ffmpeg");

                    if (installResult.isEmpty()) {
                        message.setMessage("ffmpeg를 설치 실패했습니다.");
                		
                		messagingTemplate.convertAndSendToUser(ifl.wsUserName, KeyToWsFileRecieveStatus, message);
                		delay();
                        return;
                    }else {
                    	log.info( "ffmpeg 설치: " + result.get(0) );
                    }
                }else {
                	log.info( "ffmpeg 설치된 버전: " + result.get(0) );
                }
            }
    		
            {
            	// exiftool 설치 여부 확인
                final List<String> result = CommandUtil.workExe("exiftool", "-ver");
                
                if( result != null && !result.isEmpty() ) {
                	final String line = result.get(0);
                	
                    if ( line.contains("command not found")) {
                		log.info( "exiftool 설치 진행" );
                        message.setMessage("exiftool를 설치합니다...");
                		
                		messagingTemplate.convertAndSendToUser(ifl.wsUserName, KeyToWsFileRecieveStatus, message);
                		delay();
                        
                        // ffmpeg 설치 명령 실행
                		final List<String> installResult = CommandUtil.workExe("apt", "install", "-y", "exiftool");

                        if (installResult.isEmpty()) {
                            message.setMessage("exiftool를 설치 실패했습니다.");
                    		
                    		messagingTemplate.convertAndSendToUser(ifl.wsUserName, KeyToWsFileRecieveStatus, message);
                    		delay();
                            return;
                        }else {
                        	log.info( "exiftool 설치: " + line );
                        }
                    }else {
                    	log.info( "exiftool 설치된 버전: " + line );
                    }
                	
                }else {
                	log.info("설치 오류...");
                }

            }
            
            new Thread( new BeginFileLoad(req, ifl) ).start();
    	}
    }

    /**
     * 최초 시작 파일 로드
     */
    private class BeginFileLoad implements Runnable{
    	
    	final InitInforReqVo req;
    	final InitFileLoad ifl;
    	
    	public BeginFileLoad(InitInforReqVo req, InitFileLoad ifl) {
			this.req = req;
			this.ifl = ifl;
		}
    	
    	@Override
		public void run() {
			
    		final MessageDataVo message = ifl.message;
    		delay();
    		
    		final UserVo owner = ifl.owner;
    		final EAccessType at = EAccessType.NONE;
    		
    		BnDirectory rootDir;
			try {
				rootDir = storageDao.InsertRootDirectory(owner, at);
			} catch (Exception e) {
				log.error("", e);
				message.setMessage(e.getMessage());
				
				messagingTemplate.convertAndSendToUser(ifl.wsUserName, KeyToWsFileRecieveStatus, message);
				delay();
				return;			
			}
			
			final long parentNo = rootDir.getNo();
			final int ownerUserNo = ifl.owner.getUserNo();
			final List<String> list = req.getRoots();
			for( String s : list ) {
				log.info("Begin FileLoad : " + s);
				
				final File file = new File(s);
				final BnDirectory parentDir;
				
				try {
					parentDir = StorageUtils.getDirectoryInfo(file);
					parentDir.setEnabled(true);
					parentDir.setPublicAccessible(true);
					parentDir.setParentNo( parentNo );
					parentDir.setOwnerNo( ownerUserNo );
					if( storageDao.saveDirectory( parentDir, owner, at ) < 1 ) {
						log.warn("Dir 저장 실패: " + parentDir);
					};
					
				} catch (Exception e) {
					log.error("", e);
					continue;
				}

				procLoadDir( file, ifl, parentDir, at );
			}
			
			log.info("FileLoad Complete.");
			
			executor.shutdown(); // 작업 제출 중단 (기존 작업은 계속 실행됨)

			boolean finished = false;
			try {
				finished = executor.awaitTermination(10000, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			log.info("파일 로딩 종료 작업 : " + finished);
			
			message.setMessage("작업 완료");
			message.setDone(finished);
			
			messagingTemplate.convertAndSendToUser(ifl.wsUserName, KeyToWsFileRecieveStatus, message);
		}
	}
    
    
	@Override
	public ReturnBasic saveInitStorage(InitInforReqVo req) {
		
		try {
			this.storageDao.initTable();
		} catch (Exception e) {
			log.error("", e);
			return new ReturnBasic("E", e.getMessage() );
		}

		final InitFileLoad ifl = new InitFileLoad(req.getWsUserName(), userDao.getManageUser());
		
		final MessageDataVo message = ifl.message;
		message.setDone(false);
		message.setProgress(0);
		message.setProgress(10);
		message.setMessage("준비");
		
		messagingTemplate.convertAndSendToUser(ifl.wsUserName, KeyToWsFileRecieveStatus, message);
		delay();
		
//		new Thread( new LibDownLoad(req, ifl) ).start();
		new Thread( new BeginFileLoad(req, ifl) ).start();
		
		return new ReturnBasic();
	}

	@Override
	public byte[] getThumbnail(String fid) {
		return storageDao.getThumbnail( fid );
	}
}
