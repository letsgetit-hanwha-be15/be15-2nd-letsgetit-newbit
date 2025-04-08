package com.newbit.coffeeletter.controller;

import java.util.List;

import com.newbit.coffeeletter.domain.chat.MessageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.newbit.coffeeletter.domain.chat.CoffeeLetterRoom;
import com.newbit.coffeeletter.dto.ChatMessageDTO;
import com.newbit.coffeeletter.dto.CoffeeLetterRoomDTO;
import com.newbit.coffeeletter.service.ChatService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/coffeeletter")
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    // === 채팅방 관련 API ===
    
    @GetMapping("/rooms")
    public ResponseEntity<List<CoffeeLetterRoomDTO>> getAllRooms() {
        return ResponseEntity.ok(chatService.getAllRooms());
    }
    
    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<CoffeeLetterRoomDTO> getRoomById(@PathVariable String roomId) {
        return ResponseEntity.ok(chatService.getRoomById(roomId));
    }
    
    @GetMapping("/rooms/user/{userId}")
    public ResponseEntity<List<CoffeeLetterRoomDTO>> getRoomsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(chatService.getRoomsByUserId(userId));
    }
    
    @GetMapping("/rooms/user/{userId}/status/{status}")
    public ResponseEntity<List<CoffeeLetterRoomDTO>> getRoomsByUserIdAndStatus(
            @PathVariable Long userId, 
            @PathVariable CoffeeLetterRoom.RoomStatus status) {
        return ResponseEntity.ok(chatService.getRoomsByUserIdAndStatus(userId, status));
    }
    
    @PostMapping("/rooms")
    public ResponseEntity<CoffeeLetterRoomDTO> createRoom(@RequestBody CoffeeLetterRoomDTO roomDto) {
        return ResponseEntity.ok(chatService.createRoom(roomDto));
    }
    
    @PutMapping("/rooms/{roomId}/end")
    public ResponseEntity<CoffeeLetterRoomDTO> endRoom(@PathVariable String roomId) {
        return ResponseEntity.ok(chatService.endRoom(roomId));
    }
    
    @PutMapping("/rooms/{roomId}/cancel")
    public ResponseEntity<CoffeeLetterRoomDTO> cancelRoom(@PathVariable String roomId) {
        return ResponseEntity.ok(chatService.cancelRoom(roomId));
    }
    
    // 채팅 메시지 관련 API
    
    @GetMapping("/messages/{roomId}")
    public ResponseEntity<List<ChatMessageDTO>> getMessagesByRoomId(@PathVariable String roomId) {
        return ResponseEntity.ok(chatService.getMessagesByRoomId(roomId));
    }
    
    @GetMapping("/messages/{roomId}/paging")
    public ResponseEntity<Page<ChatMessageDTO>> getMessagesByRoomIdPaging(
            @PathVariable String roomId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(chatService.getMessagesByRoomId(roomId, pageable));
    }

    @GetMapping("/messages/{roomId}/unread/{userId}")
    public ResponseEntity<List<ChatMessageDTO>> getUnreadMessages(
            @PathVariable String roomId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(chatService.getUnreadMessages(roomId, userId));
    }

    @GetMapping("/messages/{roomId}/unread-count/{userId}")
    public ResponseEntity<Integer> getUnreadMessageCount(
            @PathVariable String roomId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(chatService.getUnreadMessageCount(roomId, userId));
    }

    @PostMapping("/messages/{roomId}/mark-as-read/{userId}")
    public ResponseEntity<Void> markAsRead(
            @PathVariable String roomId,
            @PathVariable Long userId) {
        chatService.markAsRead(roomId, userId);
        return ResponseEntity.ok().build();
    }


    // WebSocket 메시지 핸들러
    
    @MessageMapping("/chat.sendMessage")
    public ChatMessageDTO sendMessage(@Payload ChatMessageDTO chatMessage) {
        return chatService.sendMessage(chatMessage);
    }
    
    @MessageMapping("/chat.addUser/{roomId}")
    public void addUser(@DestinationVariable String roomId, @Payload ChatMessageDTO chatMessage) {
        chatMessage.setType(MessageType.ENTER);
        chatService.sendSystemMessage(roomId, chatMessage.getSenderName() + "님이 입장하셨습니다.");
    }
}