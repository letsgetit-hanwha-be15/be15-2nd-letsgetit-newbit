package com.newbit.coffeeletter.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.bind.annotation.RestController;

import com.newbit.coffeeletter.domain.chat.CoffeeLetterRoom;
import com.newbit.coffeeletter.domain.chat.MessageType;
import com.newbit.coffeeletter.dto.ChatMessageDTO;
import com.newbit.coffeeletter.dto.CoffeeLetterRoomDTO;
import com.newbit.coffeeletter.service.ChatService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/coffeeletter")
@Tag(name = "채팅 API", description = "커피레터 채팅 관련 API")
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    // === 채팅방 관련 API ===
    
    @Operation(summary = "모든 채팅방 조회", description = "시스템에 등록된 모든 채팅방 목록을 조회합니다.")
    @GetMapping("/rooms")
    public ResponseEntity<List<CoffeeLetterRoomDTO>> getAllRooms() {
        return ResponseEntity.ok(chatService.getAllRooms());
    }
    
    @Operation(summary = "특정 채팅방 조회", description = "채팅방 ID로 특정 채팅방 정보를 조회합니다.")
    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<CoffeeLetterRoomDTO> getRoomById(
            @Parameter(description = "채팅방 ID") @PathVariable String roomId) {
        return ResponseEntity.ok(chatService.getRoomById(roomId));
    }
    
    @Operation(summary = "사용자별 채팅방 조회", description = "특정 사용자가 참여한 모든 채팅방을 조회합니다.")
    @GetMapping("/rooms/user/{userId}")
    public ResponseEntity<List<CoffeeLetterRoomDTO>> getRoomsByUserId(
            @Parameter(description = "사용자 ID") @PathVariable Long userId) {
        return ResponseEntity.ok(chatService.getRoomsByUserId(userId));
    }
    
    @Operation(summary = "사용자별/상태별 채팅방 조회", description = "특정 사용자가 참여한 특정 상태의 채팅방을 조회합니다.")
    @GetMapping("/rooms/user/{userId}/status/{status}")
    public ResponseEntity<List<CoffeeLetterRoomDTO>> getRoomsByUserIdAndStatus(
            @Parameter(description = "사용자 ID") @PathVariable Long userId, 
            @Parameter(description = "채팅방 상태 (ACTIVE, INACTIVE, CANCELED)") @PathVariable CoffeeLetterRoom.RoomStatus status) {
        return ResponseEntity.ok(chatService.getRoomsByUserIdAndStatus(userId, status));
    }
    
    @Operation(summary = "채팅방 생성", description = "새로운 채팅방을 생성합니다.")
    @PostMapping("/rooms")
    public ResponseEntity<CoffeeLetterRoomDTO> createRoom(
            @Parameter(description = "채팅방 정보") @RequestBody CoffeeLetterRoomDTO roomDto) {
        return ResponseEntity.ok(chatService.createRoom(roomDto));
    }
    
    @Operation(summary = "채팅방 종료", description = "특정 채팅방을 종료 상태로 변경합니다.")
    @PutMapping("/rooms/{roomId}/end")
    public ResponseEntity<CoffeeLetterRoomDTO> endRoom(
            @Parameter(description = "채팅방 ID") @PathVariable String roomId) {
        return ResponseEntity.ok(chatService.endRoom(roomId));
    }
    
    @Operation(summary = "채팅방 취소", description = "특정 채팅방을 취소 상태로 변경합니다.")
    @PutMapping("/rooms/{roomId}/cancel")
    public ResponseEntity<CoffeeLetterRoomDTO> cancelRoom(
            @Parameter(description = "채팅방 ID") @PathVariable String roomId) {
        return ResponseEntity.ok(chatService.cancelRoom(roomId));
    }
    
    // 채팅 메시지 관련 API
    
    @Operation(summary = "채팅방 메시지 조회", description = "특정 채팅방의 모든 메시지를 조회합니다.")
    @GetMapping("/messages/{roomId}")
    public ResponseEntity<List<ChatMessageDTO>> getMessagesByRoomId(
            @Parameter(description = "채팅방 ID") @PathVariable String roomId) {
        return ResponseEntity.ok(chatService.getMessagesByRoomId(roomId));
    }
    
    @Operation(summary = "채팅방 메시지 페이징 조회", description = "특정 채팅방의 메시지를 페이징하여 조회합니다.")
    @GetMapping("/messages/{roomId}/paging")
    public ResponseEntity<Page<ChatMessageDTO>> getMessagesByRoomIdPaging(
            @Parameter(description = "채팅방 ID") @PathVariable String roomId,
            @Parameter(description = "페이징 정보") @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(chatService.getMessagesByRoomId(roomId, pageable));
    }

    @Operation(summary = "읽지 않은 메시지 조회", description = "특정 채팅방에서 사용자가 읽지 않은 메시지를 조회합니다.")
    @GetMapping("/messages/{roomId}/unread/{userId}")
    public ResponseEntity<List<ChatMessageDTO>> getUnreadMessages(
            @Parameter(description = "채팅방 ID") @PathVariable String roomId,
            @Parameter(description = "사용자 ID") @PathVariable Long userId) {
        return ResponseEntity.ok(chatService.getUnreadMessages(roomId, userId));
    }

    @Operation(summary = "읽지 않은 메시지 수 조회", description = "특정 채팅방에서 사용자가 읽지 않은 메시지 수를 조회합니다.")
    @GetMapping("/messages/{roomId}/unread-count/{userId}")
    public ResponseEntity<Integer> getUnreadMessageCount(
            @Parameter(description = "채팅방 ID") @PathVariable String roomId,
            @Parameter(description = "사용자 ID") @PathVariable Long userId) {
        return ResponseEntity.ok(chatService.getUnreadMessageCount(roomId, userId));
    }

    @Operation(summary = "메시지 읽음 처리", description = "특정 채팅방의 메시지를 읽음 상태로 변경합니다.")
    @PostMapping("/messages/{roomId}/mark-as-read/{userId}")
    public ResponseEntity<Void> markAsRead(
            @Parameter(description = "채팅방 ID") @PathVariable String roomId,
            @Parameter(description = "사용자 ID") @PathVariable Long userId) {
        chatService.markAsRead(roomId, userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "마지막 메시지 조회", description = "특정 채팅방의 가장 최근 메시지를 조회합니다.")
    @GetMapping("/messages/{roomId}/last")
    public ResponseEntity<ChatMessageDTO> getLastMessage(
            @Parameter(description = "채팅방 ID") @PathVariable String roomId) {
        return ResponseEntity.ok(chatService.getLastMessage(roomId));
    }

    // WebSocket 메시지 핸들러
    
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