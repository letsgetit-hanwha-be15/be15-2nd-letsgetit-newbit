package com.newbit.newbitfeatureservice.coffeeletter.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.newbit.newbitfeatureservice.client.user.MentorFeignClient;
import com.newbit.newbitfeatureservice.client.user.UserFeignClient;
import com.newbit.newbitfeatureservice.client.user.dto.MentorDTO;
import com.newbit.newbitfeatureservice.client.user.dto.UserDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newbit.newbitfeatureservice.coffeeletter.domain.chat.CoffeeLetterRoom;
import com.newbit.newbitfeatureservice.coffeeletter.dto.CoffeeLetterRoomDTO;
import com.newbit.newbitfeatureservice.coffeeletter.repository.CoffeeLetterRoomRepository;
import com.newbit.newbitfeatureservice.coffeeletter.util.RoomUtils;
import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RoomServiceImpl implements RoomService {

    private final CoffeeLetterRoomRepository roomRepository;
    private final MessageService messageService;
    private final ModelMapper modelMapper;
    private final UserFeignClient userFeignClient;
    private final MentorFeignClient mentorFeignClient;
    
    public RoomServiceImpl(
            CoffeeLetterRoomRepository roomRepository,
            @Qualifier("messageServiceImpl") MessageService messageService,
            ModelMapper modelMapper,
            UserFeignClient userFeignClient,
            MentorFeignClient mentorFeignClient) {
        this.roomRepository = roomRepository;
        this.messageService = messageService;
        this.modelMapper = modelMapper;
        this.userFeignClient = userFeignClient;
        this.mentorFeignClient = mentorFeignClient;
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
        CoffeeLetterRoomDTO roomDTO = modelMapper.map(room, CoffeeLetterRoomDTO.class);
        
        // 사용자 정보 (프로필 이미지, 닉네임) 설정
        enrichRoomWithUserInfo(roomDTO);
        
        return roomDTO;
    }

    @Override
    public List<CoffeeLetterRoomDTO> getRoomsByUserId(Long userId) {
        List<CoffeeLetterRoom> rooms = roomRepository.findByMentorIdOrMenteeId(userId, userId);
        List<CoffeeLetterRoomDTO> roomDTOs = rooms.stream()
                .map(room -> {
                    CoffeeLetterRoomDTO dto = modelMapper.map(room, CoffeeLetterRoomDTO.class);
                    enrichRoomWithLastMessage(dto);
                    enrichRoomWithUserInfo(dto);
                    return dto;
                })
                .collect(Collectors.toList());
        
        // 최신 메시지 시간 기준으로 정렬
        return roomDTOs.stream()
                .sorted((r1, r2) -> {
                    if (r1.getLastMessageTime() == null) return 1;
                    if (r2.getLastMessageTime() == null) return -1;
                    return r2.getLastMessageTime().compareTo(r1.getLastMessageTime());
                })
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

    // 마지막 메시지 정보를 채팅방 DTO에 설정하는 메서드 추가
    private void enrichRoomWithLastMessage(CoffeeLetterRoomDTO roomDTO) {
        if (roomDTO.getId() == null) return;
        var lastMessage = messageService.getLastMessage(roomDTO.getId());
        if (lastMessage != null) {
            roomDTO.setLastMessageContent(lastMessage.getContent());
            roomDTO.setLastMessageTime(lastMessage.getTimestamp());
            roomDTO.setLastMessageType(lastMessage.getType());
            roomDTO.setLastMessageSenderId(lastMessage.getSenderId());
        }
    }

    // 사용자 정보 (프로필 이미지, 닉네임)를 채팅방 DTO에 설정하는 메서드
    private void enrichRoomWithUserInfo(CoffeeLetterRoomDTO roomDTO) {
        try {
            // 멘토 정보 가져오기
            if (roomDTO.getMentorId() != null) {
                try {
                    var mentorInfoResponse = mentorFeignClient.getMentorInfo(roomDTO.getMentorId());
                    if (mentorInfoResponse != null && mentorInfoResponse.getData() != null) {
                        var userIdResponse = mentorFeignClient.getUserIdByMentorId(roomDTO.getMentorId());
                        Long mentorUserId = (userIdResponse != null) ? userIdResponse.getData() : null;
                        if (mentorUserId != null) {
                            var userResponse = userFeignClient.getUserByUserId(mentorUserId);
                            if (userResponse != null && userResponse.getData() != null) {
                                var userDTO = userResponse.getData();
                                roomDTO.setMentorProfileImageUrl(userDTO.getProfileImageUrl());
                                roomDTO.setMentorNickname(userDTO.getNickname());
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("멘토 정보 조회 실패: {}", e.getMessage());
                }
            }

            // 멘티 정보 가져오기
            if (roomDTO.getMenteeId() != null) {
                try {
                    var userResponse = userFeignClient.getUserByUserId(roomDTO.getMenteeId());
                    if (userResponse != null && userResponse.getData() != null) {
                        var userDTO = userResponse.getData();
                        roomDTO.setMenteeProfileImageUrl(userDTO.getProfileImageUrl());
                        roomDTO.setMenteeNickname(userDTO.getNickname());
                    }
                } catch (Exception e) {
                    log.error("멘티 정보 조회 실패: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("사용자 정보 설정 중 오류 발생: {}", e.getMessage());
        }
    }
} 