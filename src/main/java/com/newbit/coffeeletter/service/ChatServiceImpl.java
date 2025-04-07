package com.newbit.coffeeletter.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Override
    @Transactional
    public CoffeeLetterRoomDTO cancelRoom(String roomId) {
        CoffeeLetterRoom room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다: " + roomId));

        room.setStatus(CoffeeLetterRoom.RoomStatus.CANCELED);
        CoffeeLetterRoom savedRoom = roomRepository.save(room);

        return modelMapper.map(savedRoom, CoffeeLetterRoomDTO.class);
    }

    @Override
    public List<CoffeeLetterRoomDTO> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(room -> modelMapper.map(room, CoffeeLetterRoomDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public CoffeeLetterRoomDTO getRoomById(String roomId) {
        CoffeeLetterRoom room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다: " + roomId));
        return modelMapper.map(room, CoffeeLetterRoomDTO.class);
    }

    @Override
    public List<CoffeeLetterRoomDTO> getRoomsByUserId(Long userId) {
        String userIdStr = userId.toString();
        return roomRepository.findByParticipantsContaining(userIdStr).stream()
                .map(room -> modelMapper.map(room, CoffeeLetterRoomDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<CoffeeLetterRoomDTO> getRoomsByUserIdAndStatus(Long userId, CoffeeLetterRoom.RoomStatus status) {
        String userIdStr = userId.toString();
        return roomRepository.findByParticipantsContainingAndStatus(userIdStr, status).stream()
                .map(room -> modelMapper.map(room, CoffeeLetterRoomDTO.class))
                .collect(Collectors.toList());
    }

}