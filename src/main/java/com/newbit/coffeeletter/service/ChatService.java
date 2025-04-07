package com.newbit.coffeeletter.service;


import com.newbit.coffeeletter.dto.CoffeeLetterRoomDTO;

public interface ChatService {

    CoffeeLetterRoomDTO createRoom(CoffeeLetterRoomDTO roomDto);
    CoffeeLetterRoomDTO endRoom(String roomId);

}
