package com.newbit.newbitfeatureservice.coffeeletter.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newbit.newbitfeatureservice.coffeeletter.domain.chat.CoffeeLetterRoom;
import com.newbit.newbitfeatureservice.coffeeletter.dto.CoffeeLetterRoomDTO;
import com.newbit.newbitfeatureservice.coffeeletter.repository.CoffeeLetterRoomRepository;
import com.newbit.newbitfeatureservice.coffeeletter.util.RoomUtils;
import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RoomServiceImpl implements RoomService {

    private final CoffeeLetterRoomRepository roomRepository;
    private final MessageService messageService;
    private final ModelMapper modelMapper;
    
    public RoomServiceImpl(
            CoffeeLetterRoomRepository roomRepository,
            @Qualifier("messageServiceImpl") MessageService messageService,
            ModelMapper modelMapper) {
        this.roomRepository = roomRepository;
        this.messageService = messageService;
        this.modelMapper = modelMapper;
    }

    @Override
    public CoffeeLetterRoomDTO createRoom(CoffeeLetterRoomDTO roomDto) {
        CoffeeLetterRoom existingRoom = RoomUtils.findRoomByCoffeeChatId(roomRepository, roomDto.getCoffeeChatId());
        if (existingRoom != null) {
            throw new BusinessException(ErrorCode.COFFEELETTER_ALREADY_EXIST);
        }

        CoffeeLetterRoom room = modelMapper.map(roomDto, CoffeeLetterRoom.class);
        room.getParticipants().add(room.getMentorId().toString());
        room.getParticipants().add(room.getMenteeId().toString());

        CoffeeLetterRoom savedRoom = roomRepository.save(room);

        messageService.sendSystemMessage(savedRoom.getId(), "채팅방이 개설되었습니다.");

        return modelMapper.map(savedRoom, CoffeeLetterRoomDTO.class);
    }

    @Override
    @Transactional
    public CoffeeLetterRoomDTO endRoom(String roomId) {
        CoffeeLetterRoom room = RoomUtils.getRoomById(roomRepository, roomId);

        room.setStatus(CoffeeLetterRoom.RoomStatus.INACTIVE);
        room.setEndTime(LocalDateTime.now());
        CoffeeLetterRoom savedRoom = roomRepository.save(room);

        messageService.sendSystemMessage(roomId, "채팅방이 종료되었습니다.");

        return modelMapper.map(savedRoom, CoffeeLetterRoomDTO.class);
    }

    @Override
    @Transactional
    public CoffeeLetterRoomDTO cancelRoom(String roomId) {
        CoffeeLetterRoom room = RoomUtils.getRoomById(roomRepository, roomId);

        room.setStatus(CoffeeLetterRoom.RoomStatus.CANCELED);
        CoffeeLetterRoom savedRoom = roomRepository.save(room);

        messageService.sendSystemMessage(roomId, "채팅방이 취소되었습니다.");

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
        CoffeeLetterRoom room = RoomUtils.getRoomById(roomRepository, roomId);
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
    
    @Override
    public String findRoomIdByCoffeeChatId(Long coffeeChatId) {
        CoffeeLetterRoom room = RoomUtils.getRoomByCoffeeChatId(roomRepository, coffeeChatId);
        return room.getId();
    }
    
    @Override
    public CoffeeLetterRoomDTO getRoomByCoffeeChatId(Long coffeeChatId) {
        CoffeeLetterRoom room = RoomUtils.getRoomByCoffeeChatId(roomRepository, coffeeChatId);
        return modelMapper.map(room, CoffeeLetterRoomDTO.class);
    }
} 