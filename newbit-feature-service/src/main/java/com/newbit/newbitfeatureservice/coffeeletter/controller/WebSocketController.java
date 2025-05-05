package com.newbit.newbitfeatureservice.coffeeletter.controller;

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
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Controller
@Tag(name = "WebSocket 채팅 API", description = "WebSocket을 통한 커피레터 실시간 채팅 API")
public class WebSocketController {

    private final ChatService chatService;
    
    @Autowired
    public WebSocketController(@Qualifier("chatServiceImpl") ChatService chatService) {
        this.chatService = chatService;
    }
    
    @Operation(summary = "메시지 전송", description = "WebSocket을 통해 메시지를 전송합니다.")
    @MessageMapping("/chat.sendMessage")
    public ChatMessageDTO sendMessage(@Payload ChatMessageDTO chatMessage) {
        return chatService.sendMessage(chatMessage);
    }
    
    @Operation(summary = "사용자 입장", description = "WebSocket을 통해 채팅방에 사용자를 추가합니다.")
    @MessageMapping("/chat.addUser/{roomId}")
    public void addUser(
            @Parameter(description = "채팅방 ID") @DestinationVariable String roomId, 
            @Payload ChatMessageDTO chatMessage) {
        chatMessage.setType(MessageType.ENTER);
        chatService.sendSystemMessage(roomId, chatMessage.getSenderName() + "님이 입장하셨습니다.");
    }
}