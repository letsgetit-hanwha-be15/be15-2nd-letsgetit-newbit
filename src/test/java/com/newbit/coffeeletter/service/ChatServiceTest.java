package com.newbit.coffeeletter.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.newbit.coffeeletter.domain.chat.CoffeeLetterRoom;
import com.newbit.coffeeletter.dto.CoffeeLetterRoomDTO;
import com.newbit.coffeeletter.repository.CoffeeLetterRoomRepository;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private CoffeeLetterRoomRepository roomRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ChatServiceImpl chatService;

    private CoffeeLetterRoomDTO roomDTO;
    private CoffeeLetterRoom room;
    private List<CoffeeLetterRoom> rooms;

    @BeforeEach
    void setUp() {
        roomDTO = new CoffeeLetterRoomDTO();
        roomDTO.setMentorId(1L);
        roomDTO.setMenteeId(2L);
        roomDTO.setMentorName("멘토");
        roomDTO.setMenteeName("멘티");
        roomDTO.setCoffeeChatId(100L);

        room = new CoffeeLetterRoom();
        room.setId("test-room-id");
        room.setMentorId(1L);
        room.setMenteeId(2L);
        room.setMentorName("멘토");
        room.setMenteeName("멘티");
        room.setCoffeeChatId(100L);
        room.setStatus(CoffeeLetterRoom.RoomStatus.ACTIVE);
        room.getParticipants().add("1");
        room.getParticipants().add("2");

        rooms = Arrays.asList(room);
    }

    @Test
    void createRoom_새로운_채팅방_생성_성공() {
        // given
        when(roomRepository.findByCoffeeChatId(anyLong()))
                .thenReturn(Optional.empty());
        when(modelMapper.map(roomDTO, CoffeeLetterRoom.class)).thenReturn(room);
        when(roomRepository.save(any(CoffeeLetterRoom.class))).thenReturn(room);
        when(modelMapper.map(room, CoffeeLetterRoomDTO.class)).thenReturn(roomDTO);

        // when
        CoffeeLetterRoomDTO result = chatService.createRoom(roomDTO);

        // then
        assertNotNull(result);
        assertEquals(roomDTO.getMentorId(), result.getMentorId());
        assertEquals(roomDTO.getMenteeId(), result.getMenteeId());
        verify(roomRepository, times(1)).save(any(CoffeeLetterRoom.class));
    }

    @Test
    void createRoom_이미_존재하는_채팅방_예외발생() {
        // given
        when(roomRepository.findByCoffeeChatId(anyLong()))
                .thenReturn(Optional.of(room));

        // when & then
        assertThrows(IllegalStateException.class, () -> {
            chatService.createRoom(roomDTO);
        });
        verify(roomRepository, never()).save(any(CoffeeLetterRoom.class));
    }
    
    @Test
    void endRoom_채팅방_비활성화_성공() {
        // given
        String roomId = "test-room-id";
        CoffeeLetterRoom activeRoom = new CoffeeLetterRoom();
        activeRoom.setId(roomId);
        activeRoom.setStatus(CoffeeLetterRoom.RoomStatus.ACTIVE);
        
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(activeRoom));
        when(roomRepository.save(any(CoffeeLetterRoom.class))).thenReturn(activeRoom);
        when(modelMapper.map(activeRoom, CoffeeLetterRoomDTO.class)).thenReturn(roomDTO);
        
        // when
        CoffeeLetterRoomDTO result = chatService.endRoom(roomId);
        
        // then
        assertNotNull(result);
        verify(roomRepository, times(1)).findById(roomId);
        verify(roomRepository, times(1)).save(any(CoffeeLetterRoom.class));
        assertEquals(CoffeeLetterRoom.RoomStatus.INACTIVE, activeRoom.getStatus());
    }
    
    @Test
    void endRoom_존재하지_않는_채팅방_예외발생() {
        // given
        String roomId = "non-existent-room-id";
        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());
        
        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            chatService.endRoom(roomId);
        });
        
        verify(roomRepository, times(1)).findById(roomId);
        verify(roomRepository, never()).save(any(CoffeeLetterRoom.class));
    }
    
    @Test
    void cancelRoom_채팅방_취소_성공() {
        // given
        String roomId = "test-room-id";
        CoffeeLetterRoom activeRoom = new CoffeeLetterRoom();
        activeRoom.setId(roomId);
        activeRoom.setStatus(CoffeeLetterRoom.RoomStatus.ACTIVE);
        
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(activeRoom));
        when(roomRepository.save(any(CoffeeLetterRoom.class))).thenReturn(activeRoom);
        when(modelMapper.map(activeRoom, CoffeeLetterRoomDTO.class)).thenReturn(roomDTO);
        
        // when
        CoffeeLetterRoomDTO result = chatService.cancelRoom(roomId);
        
        // then
        assertNotNull(result);
        verify(roomRepository, times(1)).findById(roomId);
        verify(roomRepository, times(1)).save(any(CoffeeLetterRoom.class));
        assertEquals(CoffeeLetterRoom.RoomStatus.CANCELED, activeRoom.getStatus());
    }
    
    @Test
    void cancelRoom_존재하지_않는_채팅방_예외발생() {
        // given
        String roomId = "non-existent-room-id";
        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());
        
        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            chatService.cancelRoom(roomId);
        });
        
        verify(roomRepository, times(1)).findById(roomId);
        verify(roomRepository, never()).save(any(CoffeeLetterRoom.class));
    }
    
    @Test
    void getAllRooms_모든_채팅방_조회_성공() {
        // given
        when(roomRepository.findAll()).thenReturn(rooms);
        when(modelMapper.map(any(CoffeeLetterRoom.class), eq(CoffeeLetterRoomDTO.class))).thenReturn(roomDTO);
        
        // when
        List<CoffeeLetterRoomDTO> result = chatService.getAllRooms();
        
        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(roomRepository, times(1)).findAll();
    }
    
    @Test
    void getAllRooms_빈_목록_반환() {
        // given
        when(roomRepository.findAll()).thenReturn(Collections.emptyList());
        
        // when
        List<CoffeeLetterRoomDTO> result = chatService.getAllRooms();
        
        // then
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(roomRepository, times(1)).findAll();
    }
    
    @Test
    void getRoomById_채팅방_조회_성공() {
        // given
        String roomId = "test-room-id";
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(modelMapper.map(room, CoffeeLetterRoomDTO.class)).thenReturn(roomDTO);
        
        // when
        CoffeeLetterRoomDTO result = chatService.getRoomById(roomId);
        
        // then
        assertNotNull(result);
        assertEquals(roomDTO.getMentorId(), result.getMentorId());
        assertEquals(roomDTO.getMenteeId(), result.getMenteeId());
        verify(roomRepository, times(1)).findById(roomId);
    }
    
    @Test
    void getRoomById_존재하지_않는_채팅방_예외발생() {
        // given
        String roomId = "non-existent-room-id";
        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());
        
        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            chatService.getRoomById(roomId);
        });
        
        verify(roomRepository, times(1)).findById(roomId);
    }
    
    @Test
    void getRoomsByUserId_사용자_채팅방_조회_성공() {
        // given
        Long userId = 1L;
        String userIdStr = userId.toString();
        when(roomRepository.findByParticipantsContaining(userIdStr)).thenReturn(rooms);
        when(modelMapper.map(any(CoffeeLetterRoom.class), eq(CoffeeLetterRoomDTO.class))).thenReturn(roomDTO);
        
        // when
        List<CoffeeLetterRoomDTO> result = chatService.getRoomsByUserId(userId);
        
        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(roomRepository, times(1)).findByParticipantsContaining(userIdStr);
    }
    
    @Test
    void getRoomsByUserId_빈_목록_반환() {
        // given
        Long userId = 999L;
        String userIdStr = userId.toString();
        when(roomRepository.findByParticipantsContaining(userIdStr)).thenReturn(Collections.emptyList());
        
        // when
        List<CoffeeLetterRoomDTO> result = chatService.getRoomsByUserId(userId);
        
        // then
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(roomRepository, times(1)).findByParticipantsContaining(userIdStr);
    }
    
    @Test
    void getRoomsByUserIdAndStatus_사용자와_상태별_채팅방_조회_성공() {
        // given
        Long userId = 1L;
        String userIdStr = userId.toString();
        CoffeeLetterRoom.RoomStatus status = CoffeeLetterRoom.RoomStatus.ACTIVE;
        when(roomRepository.findByParticipantsContainingAndStatus(userIdStr, status)).thenReturn(rooms);
        when(modelMapper.map(any(CoffeeLetterRoom.class), eq(CoffeeLetterRoomDTO.class))).thenReturn(roomDTO);
        
        // when
        List<CoffeeLetterRoomDTO> result = chatService.getRoomsByUserIdAndStatus(userId, status);
        
        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(roomRepository, times(1)).findByParticipantsContainingAndStatus(userIdStr, status);
    }
    
    @Test
    void getRoomsByUserIdAndStatus_빈_목록_반환() {
        // given
        Long userId = 999L;
        String userIdStr = userId.toString();
        CoffeeLetterRoom.RoomStatus status = CoffeeLetterRoom.RoomStatus.INACTIVE;
        when(roomRepository.findByParticipantsContainingAndStatus(userIdStr, status)).thenReturn(Collections.emptyList());
        
        // when
        List<CoffeeLetterRoomDTO> result = chatService.getRoomsByUserIdAndStatus(userId, status);
        
        // then
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(roomRepository, times(1)).findByParticipantsContainingAndStatus(userIdStr, status);
    }
} 