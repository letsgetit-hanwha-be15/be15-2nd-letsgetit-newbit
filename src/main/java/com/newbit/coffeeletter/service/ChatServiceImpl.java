package com.newbit.coffeeletter.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newbit.coffeeletter.domain.chat.CoffeeLetterRoom;
import com.newbit.coffeeletter.dto.CoffeeLetterRoomDTO;
import com.newbit.coffeeletter.repository.CoffeeLetterRoomRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final CoffeeLetterRoomRepository roomRepository;
    private final ModelMapper modelMapper;

    @Override
    public CoffeeLetterRoomDTO createRoom(CoffeeLetterRoomDTO roomDto) {
        Optional<CoffeeLetterRoom> existingRoom = roomRepository.findByCoffeeChatId(roomDto.getCoffeeChatId());
        if (existingRoom.isPresent()) {
            throw new IllegalStateException("이미 존재하는 채팅방입니다.");
        }

        CoffeeLetterRoom room = modelMapper.map(roomDto, CoffeeLetterRoom.class);
        room.getParticipants().add(room.getMentorId().toString());
        room.getParticipants().add(room.getMenteeId().toString());
        
        CoffeeLetterRoom savedRoom = roomRepository.save(room);
        return modelMapper.map(savedRoom, CoffeeLetterRoomDTO.class);
    }

    @Override
    @Transactional
    public CoffeeLetterRoomDTO endRoom(String roomId) {
        CoffeeLetterRoom room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다: " + roomId));

        room.setStatus(CoffeeLetterRoom.RoomStatus.INACTIVE);
        room.setEndTime(LocalDateTime.now());
        CoffeeLetterRoom savedRoom = roomRepository.save(room);

        return modelMapper.map(savedRoom, CoffeeLetterRoomDTO.class);
    }

}