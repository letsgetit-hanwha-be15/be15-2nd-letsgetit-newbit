package com.newbit.newbitfeatureservice.coffeeletter.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;

@Configuration
@EnableWebSocketMessageBroker
@EnableWebSocket
@Primary
@Tag(name = "WebSocket 설정", description = "커피레터 실시간 채팅을 위한 WebSocket 설정")
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        logger.info("WebSocket 메시지 브로커 설정");
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        logger.info("STOMP 엔드포인트 등록: /ws");
        
        logger.info("SockJS 설정: streamBytesLimit=512KB, httpMessageCacheSize=1000, enableWebSocket=true");

        registry.addEndpoint("/ws")
                .withSockJS()
                .setStreamBytesLimit(512 * 1024)
                .setHttpMessageCacheSize(1000)
                .setWebSocketEnabled(true)
                .setSessionCookieNeeded(false);
    }
    
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        logger.info("WebSocket 전송 설정");
        registration.setMessageSizeLimit(128 * 1024);
        registration.setSendBufferSizeLimit(512 * 1024);
        registration.setSendTimeLimit(20000);
    }
} 