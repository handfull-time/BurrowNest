package com.utime.burrowNest.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import jakarta.annotation.Resource;

/**
 * /BurrowNest/src/main/resources/static/js/websocket/BurrowSocket.js 참고
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	
	
	@Resource(name="SocketChannelInterceptor")
	private ChannelInterceptor stompIntercept;
	
	/**
	 * Principal 값 생성
	 * @see HandshakeHandlerImpl.java
	 */
	@Resource(name="StompHandshakeHandler")
	private HandshakeHandler handshakeHandler;

	/*
	🧑‍💻 Client
	   ├─ send("/app/hello") ─────► 🧑‍💻 Server (@MessageMapping("/hello"))
	   └─ subscribe("/topic/xxx") ◄──── 🧑‍💻 Server (convertAndSend("/topic/xxx"))
	*/
	
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
//        config.enableSimpleBroker("/topic"); // 클라이언트가 구독할 주소
//        config.setApplicationDestinationPrefixes("/app"); // 클라이언트 → 서버 전송 시 prefix
        
    	// 메시지브로커를 등록하는 코드
    	// Back -> Front로 호출 할 때 앞에 붙는 접두어.
    	// Front에서 특정 사용자 별로 받을 때는 /user 를 붙여 준다.(front에서 작업해야 함.)
    	config.enableSimpleBroker("/toFront", "/topic");
    	
    	// 프론트에서 메시지를 서버로 보낼 때
    	config.setApplicationDestinationPrefixes("/toBack", "/app");
    	
    	// Front에서 특정 사용자 별로 받을 때는 /user 를 붙여 준다.(front에서 작업해야 함.)
    	config.setUserDestinationPrefix("/user");
        
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
    	
    	// HandshakeInterceptor 의 목적.
    	// WebSocket 연결 요청이 들어올 때 실행되는 인터셉터를 등록합니다.
    	// 주로 HTTP 세션 정보, 쿠키, 헤더, 인증 정보 등을 WebSocket 세션에 전달하고자 할 때 사용합니다.
    	// HTTP 세션의 user 정보나 Authorization 헤더를 STOMP 세션으로 넘길 때
    	// 로그인을 기반으로 사용자 식별하고자 할 때
    	// 접속 로그를 남기거나, 조건부 연결 차단하고 싶을 때
//    	final HandshakeInterceptor hi = new OriginHandshakeInterceptor();
    	final HandshakeInterceptor hi = new HttpSessionHandshakeInterceptor();
    	
    	// WhiteAddressList.java 에도 웹소켓 관련 예외를 추가 해야 한다.
    	final String [] endPoint = {"/BackEvent"};
    	
    	// 접속 허용 도메인. 정확한 포트까지 명시해야 합니다 (localhost는 포트가 다르면 다른 출처로 간주됨)
    	final String [] allowedOriginPatterns = { "http://localhost:*", "http://local.pointpark.com:*", "https://myspring.iptime.org/*"};
    	
//        registry.addEndpoint(endPoint).setAllowedOrigins("*").withSockJS();
        registry
        	.addEndpoint(endPoint)
        	.addInterceptors(hi)
        	.setHandshakeHandler( this.handshakeHandler )
        	.setAllowedOriginPatterns(allowedOriginPatterns)
        	.withSockJS()
        	.setSessionCookieNeeded(true); // SockJS 세션 쿠키 사용 여부 설정
    }
    
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors( stompIntercept );
    }
    
    /**
     * Configure web socket transport.
     *
     * @param registration the registration
     */
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
    	
    	// Max incoming message size, default : 64 * 1024
        registration.setMessageSizeLimit(160 * 64 * 1024);    
        
        // default : 10 * 10000
        registration.setSendTimeLimit(20 * 10000);
        
        // Max outgoing buffer size, default : 512 * 1024
        registration.setSendBufferSizeLimit(10 * 512 * 1024); 
    }
}
