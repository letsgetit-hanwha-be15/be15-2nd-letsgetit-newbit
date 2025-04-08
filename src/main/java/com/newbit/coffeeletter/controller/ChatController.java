package com.newbit.coffeeletter.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @PostMapping("/rooms")
    public ResponseEntity<CoffeeLetterRoomDTO> createRoom(@RequestBody CoffeeLetterRoomDTO roomDto) {
        return ResponseEntity.ok(chatService.createRoom(roomDto));
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<CoffeeLetterRoomDTO>> getAllRooms() {
        return ResponseEntity.ok(chatService.getAllRooms());
    }
    
    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<CoffeeLetterRoomDTO> getRoomById(@PathVariable String roomId) {
        return ResponseEntity.ok(chatService.getRoomById(roomId));
    }
    
    @GetMapping("/rooms/users/{userId}")
    public ResponseEntity<List<CoffeeLetterRoomDTO>> getRoomsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(chatService.getRoomsByUserId(userId));
    }
    
    @GetMapping("/rooms/users/{userId}/status/{status}")
    public ResponseEntity<List<CoffeeLetterRoomDTO>> getRoomsByUserIdAndStatus(
            @PathVariable Long userId,
            @PathVariable CoffeeLetterRoom.RoomStatus status) {
        return ResponseEntity.ok(chatService.getRoomsByUserIdAndStatus(userId, status));
    }
    
    @PutMapping("/rooms/{roomId}/end")
    public ResponseEntity<CoffeeLetterRoomDTO> endRoom(@PathVariable String roomId) {
        return ResponseEntity.ok(chatService.endRoom(roomId));
    }
    
    @PutMapping("/rooms/{roomId}/cancel")
    public ResponseEntity<CoffeeLetterRoomDTO> cancelRoom(@PathVariable String roomId) {
        return ResponseEntity.ok(chatService.cancelRoom(roomId));
    }
    
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<ChatMessageDTO>> getMessagesByRoomId(@PathVariable String roomId) {
        return ResponseEntity.ok(chatService.getMessagesByRoomId(roomId));
    }
    
    @GetMapping("/rooms/{roomId}/messages/page")
    public ResponseEntity<Page<ChatMessageDTO>> getMessagesByRoomIdPaged(
            @PathVariable String roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        return ResponseEntity.ok(chatService.getMessagesByRoomId(roomId, pageable));
    }
    
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageDTO messageDTO) {
        chatService.sendMessage(messageDTO);
    }
    
    @MessageMapping("/chat.sendSystemMessage/{roomId}")
    public void sendSystemMessage(@DestinationVariable String roomId, @Payload String content) {
        chatService.sendSystemMessage(roomId, content);
    }
}