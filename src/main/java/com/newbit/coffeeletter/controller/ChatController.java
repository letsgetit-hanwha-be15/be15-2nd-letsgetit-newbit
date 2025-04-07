package com.newbit.coffeeletter.controller;

import com.newbit.coffeeletter.domain.chat.CoffeeLetterRoom;
import com.newbit.coffeeletter.dto.CoffeeLetterRoomDTO;
import com.newbit.coffeeletter.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    // 채팅방 관련 API

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

}