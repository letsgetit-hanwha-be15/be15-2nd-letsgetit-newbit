package com.newbit.coffeeletter.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.newbit.coffeeletter.dto.ChatMessageDTO;

public interface MessageService {

    ChatMessageDTO sendMessage(ChatMessageDTO messageDto);
    ChatMessageDTO sendSystemMessage(String roomId, String content);
    List<ChatMessageDTO> getMessagesByRoomId(String roomId);
    Page<ChatMessageDTO> getMessagesByRoomId(String roomId, Pageable pageable);
    List<ChatMessageDTO> getUnreadMessages(String roomId, Long userId);
    ChatMessageDTO getLastMessage(String roomId);
    void markAsRead(String roomId, Long userId);
    int getUnreadMessageCount(String roomId, Long userId);
} 