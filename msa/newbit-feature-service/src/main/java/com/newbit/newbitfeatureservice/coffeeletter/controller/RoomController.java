package com.newbit.newbitfeatureservice.coffeeletter.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newbit.newbitfeatureservice.coffeeletter.domain.chat.CoffeeLetterRoom;
import com.newbit.newbitfeatureservice.coffeeletter.dto.CoffeeLetterRoomDTO;
import com.newbit.newbitfeatureservice.coffeeletter.service.RoomService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/coffeeletter/rooms")
@Tag(name = "채팅방 API", description = "커피레터 채팅방 관련 API")
public class RoomController {

    private final RoomService roomService;
    
    public RoomController(@Qualifier("roomServiceImpl") RoomService roomService) {
        this.roomService = roomService;
    }

    @Operation(summary = "모든 채팅방 조회", description = "시스템에 등록된 모든 채팅방 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<CoffeeLetterRoomDTO>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }
    
    @Operation(summary = "특정 채팅방 조회", description = "채팅방 ID로 특정 채팅방 정보를 조회합니다.")
    @GetMapping("/{roomId}")
    public ResponseEntity<CoffeeLetterRoomDTO> getRoomById(
            @Parameter(description = "채팅방 ID") @PathVariable String roomId) {
        return ResponseEntity.ok(roomService.getRoomById(roomId));
    }
    
    @Operation(summary = "사용자별 채팅방 조회", description = "특정 사용자가 참여한 모든 채팅방을 조회합니다.")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CoffeeLetterRoomDTO>> getRoomsByUserId(
            @Parameter(description = "사용자 ID") @PathVariable Long userId) {
        return ResponseEntity.ok(roomService.getRoomsByUserId(userId));
    }
    
    @Operation(summary = "사용자별/상태별 채팅방 조회", description = "특정 사용자가 참여한 특정 상태의 채팅방을 조회합니다.")
    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<List<CoffeeLetterRoomDTO>> getRoomsByUserIdAndStatus(
            @Parameter(description = "사용자 ID") @PathVariable Long userId, 
            @Parameter(description = "채팅방 상태 (ACTIVE, INACTIVE, CANCELED)") @PathVariable CoffeeLetterRoom.RoomStatus status) {
        return ResponseEntity.ok(roomService.getRoomsByUserIdAndStatus(userId, status));
    }
    
    @Operation(summary = "커피챗 ID로 채팅방 ID 조회", description = "커피챗 ID로 채팅방 ID를 조회합니다.")
    @GetMapping("/coffeechat/{coffeeChatId}/roomId")
    public ResponseEntity<String> getRoomIdByCoffeeChatId(
            @Parameter(description = "커피챗 ID") @PathVariable Long coffeeChatId) {
        return ResponseEntity.ok(roomService.findRoomIdByCoffeeChatId(coffeeChatId));
    }
    
    @Operation(summary = "커피챗 ID로 채팅방 조회", description = "커피챗 ID로 채팅방 정보를 조회합니다.")
    @GetMapping("/coffeechat/{coffeeChatId}")
    public ResponseEntity<CoffeeLetterRoomDTO> getRoomByCoffeeChatId(
            @Parameter(description = "커피챗 ID") @PathVariable Long coffeeChatId) {
        return ResponseEntity.ok(roomService.getRoomByCoffeeChatId(coffeeChatId));
    }
    
    @Operation(summary = "채팅방 생성", description = "새로운 채팅방을 생성합니다.")
    @PostMapping
    public ResponseEntity<CoffeeLetterRoomDTO> createRoom(
            @Parameter(description = "채팅방 정보") @RequestBody CoffeeLetterRoomDTO roomDto) {
        return ResponseEntity.ok(roomService.createRoom(roomDto));
    }
    
    @Operation(summary = "채팅방 종료", description = "특정 채팅방을 종료 상태로 변경합니다.")
    @PutMapping("/{roomId}/end")
    public ResponseEntity<CoffeeLetterRoomDTO> endRoom(
            @Parameter(description = "채팅방 ID") @PathVariable String roomId) {
        return ResponseEntity.ok(roomService.endRoom(roomId));
    }
    
    @Operation(summary = "채팅방 취소", description = "특정 채팅방을 취소 상태로 변경합니다.")
    @PutMapping("/{roomId}/cancel")
    public ResponseEntity<CoffeeLetterRoomDTO> cancelRoom(
            @Parameter(description = "채팅방 ID") @PathVariable String roomId) {
        return ResponseEntity.ok(roomService.cancelRoom(roomId));
    }
}