package com.newbit.newbitfeatureservice.coffeeletter.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import com.newbit.newbitfeatureservice.coffeeletter.domain.chat.MessageType;
import com.newbit.newbitfeatureservice.coffeeletter.dto.ChatMessageDTO;
import com.newbit.newbitfeatureservice.coffeeletter.service.ChatService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@Tag(name = "WebSocket 채팅 API", description = "WebSocket을 통한 커피레터 실시간 채팅 API")
public class WebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketController.class);
    
    private final ChatService chatService;
    
    @Autowired
    public WebSocketController(@Qualifier("chatServiceImpl") ChatService chatService) {
        this.chatService = chatService;
    }
    
    @Operation(summary = "메시지 전송", description = "WebSocket을 통해 메시지를 전송합니다.")
    @MessageMapping("/chat.sendMessage")
    public ChatMessageDTO sendMessage(@Payload ChatMessageDTO chatMessage) {
        logger.info("WebSocket 메시지 수신: {}", chatMessage);
        try {
            // 프론트엔드에서 받는 type 필드가 있다면 설정, 없다면 기본값 사용
            // chatMessage.setType(MessageType.CHAT); // 필요시 구현
            return chatService.sendMessage(chatMessage);
        } catch (Exception e) {
            logger.error("WebSocket 메시지 처리 중 오류: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    @Operation(summary = "사용자 입장", description = "WebSocket을 통해 채팅방에 사용자를 추가합니다.")
    @MessageMapping("/chat.addUser/{roomId}")
    public void addUser(
            @Parameter(description = "채팅방 ID") @DestinationVariable String roomId, 
            @Payload ChatMessageDTO chatMessage) {
        logger.info("사용자 입장: roomId={}, user={}", roomId, chatMessage);
        try {
            // 시스템 메시지 전송 - DTO 구조에 맞춰 발신자 이름 가져오기
            String senderName = chatMessage.toString(); // 임시로 toString() 사용
            chatService.sendSystemMessage(roomId, "새로운 사용자가 입장하셨습니다.");
        } catch (Exception e) {
            logger.error("사용자 입장 처리 중 오류: {}", e.getMessage(), e);
        }
    }
}