package com.utime.burrowNest.storage.service.impl;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.utime.burrowNest.common.vo.ReturnBasic;
import com.utime.burrowNest.storage.service.StorageService;
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

	@Override
	public ReturnBasic saveInitStorage(InitInforReqVo req) {
		
		new Thread( new Runnable(){
			public void run() {
				
				final String wsUserName = req.getWsUserName();
				log.info("thread call");
				
				final MessageDataVo message = new MessageDataVo();
				message.setMessage("간다 가즈아~~~GO!!!");
				message.setProgress(10);
				message.setTotal(1000);

//	            messagingTemplate.convertAndSend("/toFront/RecieveStatus", message);
				messagingTemplate.convertAndSendToUser(wsUserName, "/toFront/RecieveStatus", message);  

	            try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
	            
	            message.setProgress(300);
	            messagingTemplate.convertAndSendToUser(wsUserName, "/toFront/RecieveStatus", message);
	            
	            try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
	            
	            message.setProgress(400);
	            messagingTemplate.convertAndSend("/toFront/RecieveStatus", message);
	            
	            try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
	            
	            message.setProgress(900);
	            messagingTemplate.convertAndSend("/toFront/RecieveStatus", message);
	            
	            try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
	            
				message.setMessage("다 됐다~");
	            message.setProgress(1000);
	            messagingTemplate.convertAndSend("/toFront/RecieveStatus", message);
	            
	            try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
	            
	            message.setDone(true);
	            messagingTemplate.convertAndSendToUser(wsUserName, "/toFront/RecieveStatus", message);

			};
		}).start();
		
		return new ReturnBasic();
	}
}
