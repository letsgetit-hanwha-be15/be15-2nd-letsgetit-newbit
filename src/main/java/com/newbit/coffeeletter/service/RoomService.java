package com.newbit.coffeeletter.service;

import java.util.List;

import com.newbit.coffeeletter.domain.chat.CoffeeLetterRoom;
import com.newbit.coffeeletter.dto.CoffeeLetterRoomDTO;

public interface RoomService {
    // 채팅방 관련 메서드
    CoffeeLetterRoomDTO createRoom(CoffeeLetterRoomDTO roomDto);
    CoffeeLetterRoomDTO endRoom(String roomId);
    CoffeeLetterRoomDTO cancelRoom(String roomId);

    List<CoffeeLetterRoomDTO> getAllRooms();
    CoffeeLetterRoomDTO getRoomById(String roomId);
    List<CoffeeLetterRoomDTO> getRoomsByUserId(Long userId);
    List<CoffeeLetterRoomDTO> getRoomsByUserIdAndStatus(Long userId, CoffeeLetterRoom.RoomStatus status);
    
    String findRoomIdByCoffeeChatId(Long coffeeChatId);
    CoffeeLetterRoomDTO getRoomByCoffeeChatId(Long coffeeChatId);
} 