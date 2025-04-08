package com.newbit.coffeeletter.service;


import com.newbit.coffeeletter.domain.chat.CoffeeLetterRoom;
import com.newbit.coffeeletter.domain.chat.MessageType;
import com.newbit.coffeeletter.dto.ChatMessageDTO;
import com.newbit.coffeeletter.dto.CoffeeLetterRoomDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.List;

public interface ChatService {

    CoffeeLetterRoomDTO createRoom(CoffeeLetterRoomDTO roomDto);
    CoffeeLetterRoomDTO endRoom(String roomId);
    CoffeeLetterRoomDTO cancelRoom(String roomId);

    List<CoffeeLetterRoomDTO> getAllRooms();
    CoffeeLetterRoomDTO getRoomById(String roomId);
    List<CoffeeLetterRoomDTO> getRoomsByUserId(Long userId);
    List<CoffeeLetterRoomDTO> getRoomsByUserIdAndStatus(Long userId, CoffeeLetterRoom.RoomStatus status);

    ChatMessageDTO sendMessage(ChatMessageDTO messageDto);
    ChatMessageDTO sendSystemMessage(String roomId, String content);
    List<ChatMessageDTO> getMessagesByRoomId(String roomId);
    Page<ChatMessageDTO> getMessagesByRoomId(String roomId, Pageable pageable);
    List<ChatMessageDTO> getUnreadMessages(String roomId, Long userId);

    // 읽음 처리 관련 메서드
    void markAsRead(String roomId, Long userId);
    int getUnreadMessageCount(String roomId, Long userId);

}
