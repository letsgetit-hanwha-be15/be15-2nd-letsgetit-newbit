package com.newbit.newbitfeatureservice.coffeeletter.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import com.newbit.newbitfeatureservice.coffeeletter.domain.chat.CoffeeLetterRoom;
import com.newbit.newbitfeatureservice.coffeeletter.dto.ChatMessageDTO;
import com.newbit.newbitfeatureservice.coffeeletter.dto.CoffeeLetterRoomDTO;
import com.newbit.newbitfeatureservice.coffeeletter.repository.CoffeeLetterRoomRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Primary
public class ChatServiceImpl implements ChatService {

    private final RoomService roomService;
    private final MessageService messageService;
    private final SimpMessageSendingOperations messagingTemplate;
    private final CoffeeLetterRoomRepository roomRepository;
    
    public ChatServiceImpl(
            @Qualifier("roomServiceImpl") RoomService roomService,
            @Qualifier("messageServiceImpl") MessageService messageService,
            SimpMessageSendingOperations messagingTemplate,
            CoffeeLetterRoomRepository roomRepository) {
        this.roomService = roomService;
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
        this.roomRepository = roomRepository;
    }

    // RoomService 메서드 위임
    @Override
    public CoffeeLetterRoomDTO createRoom(CoffeeLetterRoomDTO roomDto) {
        CoffeeLetterRoomDTO createdRoom = roomService.createRoom(roomDto);
        // 채팅방 생성 후 웹소켓으로 알림
        notifyRoomListUpdate(createdRoom.getId());
        return createdRoom;
    }

    @Override
    public CoffeeLetterRoomDTO endRoom(String roomId) {
        CoffeeLetterRoomDTO endedRoom = roomService.endRoom(roomId);
        // 채팅방 종료 후 웹소켓으로 알림
        notifyRoomListUpdate(roomId);
        return endedRoom;
    }

    @Override
    public CoffeeLetterRoomDTO cancelRoom(String roomId) {
        CoffeeLetterRoomDTO canceledRoom = roomService.cancelRoom(roomId);
        // 채팅방 취소 후 웹소켓으로 알림
        notifyRoomListUpdate(roomId);
        return canceledRoom;
    }

    @Override
    public List<CoffeeLetterRoomDTO> getAllRooms() {
        return roomService.getAllRooms();
    }

    @Override
    public CoffeeLetterRoomDTO getRoomById(String roomId) {
        return roomService.getRoomById(roomId);
    }

    @Override
    public List<CoffeeLetterRoomDTO> getRoomsByUserId(Long userId) {
        return roomService.getRoomsByUserId(userId);
    }

    @Override
    public List<CoffeeLetterRoomDTO> getRoomsByUserIdAndStatus(Long userId, CoffeeLetterRoom.RoomStatus status) {
        return roomService.getRoomsByUserIdAndStatus(userId, status);
    }
    
    @Override
    public String findRoomIdByCoffeeChatId(Long coffeeChatId) {
        return roomService.findRoomIdByCoffeeChatId(coffeeChatId);
    }
    
    @Override
    public CoffeeLetterRoomDTO getRoomByCoffeeChatId(Long coffeeChatId) {
        return roomService.getRoomByCoffeeChatId(coffeeChatId);
    }

    // MessageService 메서드 위임
    @Override
    public ChatMessageDTO sendMessage(ChatMessageDTO messageDto) {
        ChatMessageDTO sentMessage = messageService.sendMessage(messageDto);
        // 메시지 전송 후 채팅방 목록 업데이트 웹소켓으로 알림
        notifyRoomListUpdate(messageDto.getRoomId());
        return sentMessage;
    }

    @Override
    public ChatMessageDTO sendSystemMessage(String roomId, String content) {
        ChatMessageDTO systemMessage = messageService.sendSystemMessage(roomId, content);
        // 시스템 메시지 전송 후 채팅방 목록 업데이트 웹소켓으로 알림
        notifyRoomListUpdate(roomId);
        return systemMessage;
    }

    @Override
    public List<ChatMessageDTO> getMessagesByRoomId(String roomId) {
        return messageService.getMessagesByRoomId(roomId);
    }

    @Override
    public Page<ChatMessageDTO> getMessagesByRoomId(String roomId, Pageable pageable) {
        return messageService.getMessagesByRoomId(roomId, pageable);
    }

    @Override
    public List<ChatMessageDTO> getUnreadMessages(String roomId, Long userId) {
        return messageService.getUnreadMessages(roomId, userId);
    }

    @Override
    public ChatMessageDTO getLastMessage(String roomId) {
        return messageService.getLastMessage(roomId);
    }

    @Override
    public void markAsRead(String roomId, Long userId) {
        messageService.markAsRead(roomId, userId);
    }

    @Override
    public int getUnreadMessageCount(String roomId, Long userId) {
        return messageService.getUnreadMessageCount(roomId, userId);
    }

    // 채팅방 목록 업데이트 알림 메서드
    private void notifyRoomListUpdate(String roomId) {
        try {
            CoffeeLetterRoom room = roomRepository.findById(roomId).orElse(null);
            if (room != null) {
                messagingTemplate.convertAndSend("/topic/rooms", room);
            }
        } catch (Exception e) {
            log.error("채팅방 목록 업데이트 알림 실패: {}", e.getMessage());
        }
    }
}