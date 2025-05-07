package com.newbit.newbitfeatureservice.coffeeletter.config;

import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.messaging.simp.config.ChannelRegistration;
import com.newbit.newbitfeatureservice.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompCommand;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;

import io.swagger.v3.oas.annotations.tags.Tag;

@Configuration
@EnableWebSocketMessageBroker
@Primary
@Tag(name = "WebSocket 설정", description = "커피레터 실시간 채팅을 위한 WebSocket 설정")
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private StompHandler stompHandler;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
                
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS()
                .setStreamBytesLimit(512 * 1024)
                .setHttpMessageCacheSize(1000)
                .setWebSocketEnabled(true)
                .setSessionCookieNeeded(false);
    }
    
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(128 * 1024);
        registration.setSendBufferSizeLimit(512 * 1024);
        registration.setSendTimeLimit(20000);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler);
    }
}

@Slf4j
@Component
class StompHandler implements ChannelInterceptor {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        log.debug("StompHandler preSend - Command: {}, Headers: {}", accessor.getCommand(), accessor.toNativeHeaderMap());

        if (accessor.getCommand() == StompCommand.CONNECT || accessor.getCommand() == StompCommand.SEND) {
            String token = null;
            if (accessor.getNativeHeader("Authorization") != null) {
                token = accessor.getFirstNativeHeader("Authorization");
                log.debug("Found Authorization header: {}", token);
            }

            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                log.debug("Extracted Bearer token: {}", token);
            } else {
                 log.warn("No valid Bearer token found in Authorization header");
                 if (accessor.getCommand() != StompCommand.CONNECT) {
                     throw new IllegalArgumentException("WebSocket 인증 실패: Bearer 토큰이 없습니다.");
                 }
            }

            if (token != null) {
                try {
                    boolean isValid = jwtTokenProvider.validateToken(token);
                    if (isValid) {
                        log.debug("Token validation successful in StompHandler");
                    } else {
                        log.warn("validateToken returned false, which is unexpected.");
                        throw new IllegalArgumentException("WebSocket 인증 실패: 유효하지 않은 토큰 (validateToken returned false)");
                    }
                } catch (BusinessException e) {
                    log.error("Token validation failed in StompHandler: {}", e.getMessage());
                    throw new IllegalArgumentException("WebSocket 인증 실패: " + e.getErrorCode().getMessage());
                } catch (Exception e) {
                    log.error("Unexpected error during token validation in StompHandler: {}", e.getMessage(), e);
                    throw new IllegalArgumentException("WebSocket 인증 중 예상치 못한 오류 발생", e);
                }
            } else if (accessor.getCommand() != StompCommand.CONNECT) {
                log.warn("Attempting to send message without token.");
                 throw new IllegalArgumentException("WebSocket 인증 실패: 메시지 전송 시 토큰이 필요합니다.");
            }
        }
        return message;
    }
} 