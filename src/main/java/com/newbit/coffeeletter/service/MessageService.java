package com.newbit.coffeeletter.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.newbit.coffeeletter.dto.ChatMessageDTO;

public interface MessageService {

    // 메시지 관련 메서드
    ChatMessageDTO sendMessage(ChatMessageDTO messageDto);
    ChatMessageDTO sendSystemMessage(String roomId, String content);
    List<ChatMessageDTO> getMessagesByRoomId(String roomId);
    Page<ChatMessageDTO> getMessagesByRoomId(String roomId, Pageable pageable);
    List<ChatMessageDTO> getUnreadMessages(String roomId, Long userId);
    ChatMessageDTO getLastMessage(String roomId);

    // 읽음 처리 관련 메서드
    void markAsRead(String roomId, Long userId);
    int getUnreadMessageCount(String roomId, Long userId);
} 