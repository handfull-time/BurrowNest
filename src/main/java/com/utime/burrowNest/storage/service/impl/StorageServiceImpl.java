package com.utime.burrowNest.storage.service.impl;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.utime.burrowNest.common.vo.ReturnBasic;
import com.utime.burrowNest.storage.dao.StorageDao;
import com.utime.burrowNest.storage.service.StorageService;
import com.utime.burrowNest.storage.vo.BnDirectory;
import com.utime.burrowNest.storage.vo.MessageDataVo;
import com.utime.burrowNest.user.vo.InitInforReqVo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {
	
	private final SimpMessagingTemplate messagingTemplate;
	
	final static ObjectWriter objMapper = new ObjectMapper().writerWithDefaultPrettyPrinter();
	
	private final ExecutorService executor = Executors.newWorkStealingPool();
	
	
	private final StorageDao storageDao;
	
	// socket 예.
    public void searchFiles(String keyword) {
        // 검색 시작 알림
        messagingTemplate.convertAndSend("/topic/search-status", "🔍 검색 시작: " + keyword);

        // 예: 검색 작업 중간 단계마다 전송
        try {
            Thread.sleep(1000); // 실제 파일 처리
            messagingTemplate.convertAndSend("/topic/search-status", "📁 파일 100개 검색 완료");

            Thread.sleep(1000); // 더 처리
            messagingTemplate.convertAndSend("/topic/search-status", "📁 파일 300개 검색 완료");

            Thread.sleep(1000);
            messagingTemplate.convertAndSend("/topic/search-status", "✅ 검색 완료");

        } catch (InterruptedException e) {
            messagingTemplate.convertAndSend("/topic/search-status", "❌ 검색 중 오류 발생");
        }
    }

    /**
     * Front에 웹 소
     */
    final String KeyToWsFileRecieveStatus = "/toFront/RecieveStatus";
    
    private class InitFileLoad{
        final AtomicLong counterDir = new AtomicLong(0);
        final AtomicLong counterFile = new AtomicLong(0);
        final MessageDataVo message = new MessageDataVo();
        final String wsUserName;
        
        public InitFileLoad(String userName) {
			this.wsUserName = userName;
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
    	
    	public LoadFile(File file, InitFileLoad ifl, BnDirectory parent) {
			this.file = file;
			this.ifl = ifl;
			this.parent = parent;
		}
    	
    	@Override
    	public void run() {
    		// 파일 추가 및 확장자 분석.
    		this.ifl.counterFile.incrementAndGet();
    	}
    }
    
    private void LoadDir(File f, InitFileLoad ifl, BnDirectory parent ) {
    	
    	final File [] files = f.listFiles();
    	if( files == null || files.length < 1 ) {
    		return;
    	}
    	
		{
			final File[] tmp = f.listFiles(file -> file.isFile());
			final int count = tmp != null ? tmp.length : 0;
			if( count > 0 ) ifl.counterDir.addAndGet( count );
		}
    	
    	ifl.message.setMessage(f.getName() + "분석 중");
    	ifl.message.setTotal( ifl.counterDir.get() );
    	ifl.message.setProgress( ifl.counterFile.get() );
		messagingTemplate.convertAndSendToUser(ifl.wsUserName, KeyToWsFileRecieveStatus, ifl.message);

		for( File file : files ) {
			if( file.isDirectory() ) {
				BnDirectory childDir;
				try {
					childDir = storageDao.insertDirectory( file );
				} catch (Exception e) {
					log.error("", e);
					continue;
				}
				
				this.LoadDir( file, ifl, childDir );
			}else {
				executor.execute( new LoadFile( file, ifl, parent ) );
			}
		}
    }
    
	@Override
	public ReturnBasic saveInitStorage(InitInforReqVo req) {

		final InitFileLoad ifl = new InitFileLoad(req.getWsUserName());
		
		final MessageDataVo message = ifl.message;
		message.setDone(false);
		message.setProgress(0);
		message.setProgress(10);
		message.setMessage("파일 로딩 준비");
		
		messagingTemplate.convertAndSendToUser(ifl.wsUserName, KeyToWsFileRecieveStatus, message);
		
		
		new Thread( new Runnable() {
			
			@Override
			public void run() {
				
				try {Thread.sleep(200);} catch (InterruptedException e) {e.printStackTrace();}
				
				List<String> list = req.getRoots();
				for( String s : list ) {
					
					final File file = new File(s);
					BnDirectory parentDir;
					try {
						parentDir = storageDao.insertDirectory( file );
					} catch (Exception e) {
						log.error("", e);
						continue;
					}
					
					LoadDir( file, ifl, parentDir );
				}
				
				executor.shutdown(); // 작업 제출 중단 (기존 작업은 계속 실행됨)

				boolean finished = false;
				try {
					finished = executor.awaitTermination(10000, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				log.info("파일 로딩 종료 작업 : " + finished);
			}
		} ).start();
		
		return new ReturnBasic();
	}
}
