package com.utime.burrowNest.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * /BurrowNest/src/main/resources/static/js/websocket/BurrowSocket.js 참고
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
//        config.enableSimpleBroker("/topic"); // 클라이언트가 구독할 주소
//        config.setApplicationDestinationPrefixes("/app"); // 클라이언트 → 서버 전송 시 prefix
        
    	// 메시지브로커를 등록하는 코드
    	// Back -> Front로 호출 할 때 앞에 붙는 접두어.
    	// Front에서 특정 사용자 별로 받을 때는 /user 를 붙여 준다.(front에서 작업해야 함.)
    	config.enableSimpleBroker("/toFront");
    	
    	// 도착한 경로에 대한 prefix를 설정
    	// Front -> Back로 호출 할 때 앞에 붙는 접두어
    	config.setApplicationDestinationPrefixes("/toBack");
    	
    	// Front에서 특정 사용자 별로 받을 때는 /user 를 붙여 준다.(front에서 작업해야 함.)
    	config.setUserDestinationPrefix("/user");
        
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
    	
    	final String [] endPoint = {"/BackEvent", "/ws-stomp"};
    	
        registry.addEndpoint(endPoint).setAllowedOrigins("*").withSockJS();
    }
}
