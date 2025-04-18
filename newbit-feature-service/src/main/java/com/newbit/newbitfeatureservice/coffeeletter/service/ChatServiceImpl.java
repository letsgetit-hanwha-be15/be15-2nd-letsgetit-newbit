package com.newbit.newbitfeatureservice.coffeeletter.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.newbit.newbitfeatureservice.coffeeletter.domain.chat.CoffeeLetterRoom;
import com.newbit.newbitfeatureservice.coffeeletter.dto.ChatMessageDTO;
import com.newbit.newbitfeatureservice.coffeeletter.dto.CoffeeLetterRoomDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Primary
public class ChatServiceImpl implements ChatService {

    private final RoomService roomService;
    private final MessageService messageService;
    
    public ChatServiceImpl(
            @Qualifier("roomServiceImpl") RoomService roomService,
            @Qualifier("messageServiceImpl") MessageService messageService) {
        this.roomService = roomService;
        this.messageService = messageService;
    }

    // RoomService 메서드 위임
    @Override
    public CoffeeLetterRoomDTO createRoom(CoffeeLetterRoomDTO roomDto) {
        return roomService.createRoom(roomDto);
    }

    @Override
    public CoffeeLetterRoomDTO endRoom(String roomId) {
        return roomService.endRoom(roomId);
    }

    @Override
    public CoffeeLetterRoomDTO cancelRoom(String roomId) {
        return roomService.cancelRoom(roomId);
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
        return messageService.sendMessage(messageDto);
    }

    @Override
    public ChatMessageDTO sendSystemMessage(String roomId, String content) {
        return messageService.sendSystemMessage(roomId, content);
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
}