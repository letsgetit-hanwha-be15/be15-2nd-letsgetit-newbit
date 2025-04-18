package com.newbit.newbitfeatureservice.coffeeletter.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newbit.newbitfeatureservice.coffeeletter.dto.ChatMessageDTO;
import com.newbit.newbitfeatureservice.coffeeletter.service.MessageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/coffeeletter/messages")
@Tag(name = "커피레터(채팅 기능) API", description = "커피레터 채팅 메시지 관련 API")
public class MessageController {

    private final MessageService messageService;
    
    public MessageController(@Qualifier("messageServiceImpl") MessageService messageService) {
        this.messageService = messageService;
    }
    
    @Operation(summary = "채팅방 메시지 조회", description = "특정 채팅방의 모든 메시지를 조회합니다.")
    @GetMapping("/{roomId}")
    public ResponseEntity<List<ChatMessageDTO>> getMessagesByRoomId(
            @Parameter(description = "채팅방 ID") @PathVariable String roomId) {
        return ResponseEntity.ok(messageService.getMessagesByRoomId(roomId));
    }
    
    @Operation(summary = "채팅방 메시지 페이징 조회", description = "특정 채팅방의 메시지를 페이징하여 조회합니다.")
    @GetMapping("/{roomId}/paging")
    public ResponseEntity<Page<ChatMessageDTO>> getMessagesByRoomIdPaging(
            @Parameter(description = "채팅방 ID") @PathVariable String roomId,
            @Parameter(description = "페이징 정보") @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(messageService.getMessagesByRoomId(roomId, pageable));
    }

    @Operation(summary = "읽지 않은 메시지 조회", description = "특정 채팅방에서 사용자가 읽지 않은 메시지를 조회합니다.")
    @GetMapping("/{roomId}/unread/{userId}")
    public ResponseEntity<List<ChatMessageDTO>> getUnreadMessages(
            @Parameter(description = "채팅방 ID") @PathVariable String roomId,
            @Parameter(description = "사용자 ID") @PathVariable Long userId) {
        return ResponseEntity.ok(messageService.getUnreadMessages(roomId, userId));
    }

    @Operation(summary = "읽지 않은 메시지 수 조회", description = "특정 채팅방에서 사용자가 읽지 않은 메시지 수를 조회합니다.")
    @GetMapping("/{roomId}/unread-count/{userId}")
    public ResponseEntity<Integer> getUnreadMessageCount(
            @Parameter(description = "채팅방 ID") @PathVariable String roomId,
            @Parameter(description = "사용자 ID") @PathVariable Long userId) {
        return ResponseEntity.ok(messageService.getUnreadMessageCount(roomId, userId));
    }

    @Operation(summary = "메시지 읽음 처리", description = "특정 채팅방의 메시지를 읽음 상태로 변경합니다.")
    @PostMapping("/{roomId}/mark-as-read/{userId}")
    public ResponseEntity<Void> markAsRead(
            @Parameter(description = "채팅방 ID") @PathVariable String roomId,
            @Parameter(description = "사용자 ID") @PathVariable Long userId) {
        messageService.markAsRead(roomId, userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "마지막 메시지 조회", description = "특정 채팅방의 가장 최근 메시지를 조회합니다.")
    @GetMapping("/{roomId}/last")
    public ResponseEntity<ChatMessageDTO> getLastMessage(
            @Parameter(description = "채팅방 ID") @PathVariable String roomId) {
        return ResponseEntity.ok(messageService.getLastMessage(roomId));
    }
} 