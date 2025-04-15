package com.utime.burrowNest.common.interceptor;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("SocketChannelInterceptor")
class WebsocketChannelInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        
    	final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            log.info("🔌 STOMP CONNECT: sessionId={}, headers={}",
                     accessor.getSessionId(), accessor.toNativeHeaderMap());
        }

        if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            log.info("❌ STOMP DISCONNECT: sessionId={}", accessor.getSessionId());
        }

        return message;
    }
    
    
    // 원래는 이런 용도로 사용함.
//    @Override
//    public Message<?> preSend(Message<?> message, MessageChannel channel) {
//        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//        if (StompCommand.SEND.equals(accessor.getCommand())) {
//            Principal user = accessor.getUser();
//            if (user == null) {
//                throw new AccessDeniedException("로그인된 사용자만 전송할 수 있습니다.");
//            }
//        }
//        return message;
//    }
}
