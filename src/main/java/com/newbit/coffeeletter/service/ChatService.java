package com.newbit.coffeeletter.service;


import com.newbit.coffeeletter.domain.chat.CoffeeLetterRoom;
import com.newbit.coffeeletter.domain.chat.MessageType;
import com.newbit.coffeeletter.dto.ChatMessageDTO;
import com.newbit.coffeeletter.dto.CoffeeLetterRoomDTO;
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

}
