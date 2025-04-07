package com.newbit.coffeeletter.controller;

import com.newbit.coffeeletter.dto.CoffeeLetterRoomDTO;
import com.newbit.coffeeletter.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

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

}