package com.newbit.newbitfeatureservice.coffeeletter.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.newbit.newbitfeatureservice.coffeeletter.dto.ChatMessageDTO;
import com.newbit.newbitfeatureservice.coffeeletter.service.MessageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/coffeeletter/messages")
@Tag(name = "커피레터(채팅 기능) API", description = "커피레터 채팅 메시지 관련 API")
public class MessageController {

    private final MessageService messageService;
    
    public MessageController(@Qualifier("messageServiceImpl") MessageService messageService) {
        this.messageService = messageService;
    }
    
    // TODO : 향후 삭제 예정 웹소켓 연결 전 테스트 용도의 api 
    @Operation(summary = "메시지 전송", description = "새로운 메시지를 전송합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "메시지 전송 성공", 
                    content = @Content(schema = @Schema(implementation = ChatMessageDTO.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping
    public ResponseEntity<ChatMessageDTO> sendMessage(@Validated @RequestBody ChatMessageDTO messageDto) {
        ChatMessageDTO sentMessage = messageService.sendMessage(messageDto);
        return ResponseEntity.ok(sentMessage);
    }
    
    @Operation(summary = "채팅방 메시지 조회", description = "특정 채팅방의 모든 메시지를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "메시지 조회 성공"),
        @ApiResponse(responseCode = "404", description = "채팅방이 존재하지 않음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{roomId}")
    public ResponseEntity<List<ChatMessageDTO>> getMessagesByRoomId(
            @Parameter(description = "채팅방 ID") @PathVariable String roomId) {
        return ResponseEntity.ok(messageService.getMessagesByRoomId(roomId));
    }
    
    @Operation(summary = "채팅방 메시지 페이징 조회", description = "특정 채팅방의 메시지를 페이징하여 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "메시지 페이징 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        @ApiResponse(responseCode = "404", description = "채팅방이 존재하지 않음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{roomId}/paging")
    public ResponseEntity<Page<ChatMessageDTO>> getMessagesByRoomIdPaging(
            @Parameter(description = "채팅방 ID") @PathVariable String roomId,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(required = false, defaultValue = "30") int size,
            @Parameter(description = "정렬 방향 (ASC, DESC)") @RequestParam(required = false, defaultValue = "DESC") String direction) {
        
        Sort.Direction sortDirection = "DESC".equalsIgnoreCase(direction) ? 
                                      Sort.Direction.DESC : Sort.Direction.ASC;
        
        Sort sort = Sort.by(sortDirection, "timestamp");
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return ResponseEntity.ok(messageService.getMessagesByRoomId(roomId, pageable));
    }

    @Operation(summary = "읽지 않은 메시지 조회", description = "특정 채팅방에서 사용자가 읽지 않은 메시지를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "읽지 않은 메시지 조회 성공"),
        @ApiResponse(responseCode = "404", description = "채팅방 또는 사용자가 존재하지 않음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{roomId}/unread/{userId}")
    public ResponseEntity<List<ChatMessageDTO>> getUnreadMessages(
            @Parameter(description = "채팅방 ID") @PathVariable String roomId,
            @Parameter(description = "사용자 ID") @PathVariable Long userId) {
        return ResponseEntity.ok(messageService.getUnreadMessages(roomId, userId));
    }

    @Operation(summary = "읽지 않은 메시지 수 조회", description = "특정 채팅방에서 사용자가 읽지 않은 메시지 수를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "읽지 않은 메시지 수 조회 성공"),
        @ApiResponse(responseCode = "404", description = "채팅방 또는 사용자가 존재하지 않음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{roomId}/unread-count/{userId}")
    public ResponseEntity<Integer> getUnreadMessageCount(
            @Parameter(description = "채팅방 ID") @PathVariable String roomId,
            @Parameter(description = "사용자 ID") @PathVariable Long userId) {
        return ResponseEntity.ok(messageService.getUnreadMessageCount(roomId, userId));
    }

    @Operation(summary = "메시지 읽음 처리", description = "특정 채팅방의 메시지를 읽음 상태로 변경합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "읽음 처리 성공"),
        @ApiResponse(responseCode = "404", description = "채팅방 또는 사용자가 존재하지 않음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/{roomId}/mark-as-read/{userId}")
    public ResponseEntity<Void> markAsRead(
            @Parameter(description = "채팅방 ID") @PathVariable String roomId,
            @Parameter(description = "사용자 ID") @PathVariable Long userId) {
        messageService.markAsRead(roomId, userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "마지막 메시지 조회", description = "특정 채팅방의 가장 최근 메시지를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "마지막 메시지 조회 성공", 
                    content = @Content(schema = @Schema(implementation = ChatMessageDTO.class))),
        @ApiResponse(responseCode = "404", description = "채팅방이 존재하지 않거나 메시지가 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{roomId}/last")
    public ResponseEntity<ChatMessageDTO> getLastMessage(
            @Parameter(description = "채팅방 ID") @PathVariable String roomId) {
        return ResponseEntity.ok(messageService.getLastMessage(roomId));
    }
} 