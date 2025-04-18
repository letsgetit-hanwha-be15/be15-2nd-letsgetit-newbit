package com.newbit.newbitfeatureservice.coffeeletter.service;

import com.newbit.newbitfeatureservice.coffeeletter.domain.chat.CoffeeLetterRoom;
import com.newbit.newbitfeatureservice.coffeeletter.dto.CoffeeLetterRoomDTO;
import com.newbit.newbitfeatureservice.coffeeletter.repository.CoffeeLetterRoomRepository;
import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("채팅방 서비스 단위 테스트")
class RoomServiceTest {

    @Mock
    private CoffeeLetterRoomRepository roomRepository;
    
    @Mock
    private MessageService messageService;
    
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RoomServiceImpl roomService;

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
        
        when(modelMapper.map(any(Object.class), eq(CoffeeLetterRoomDTO.class))).thenReturn(roomDTO);
        when(modelMapper.map(any(CoffeeLetterRoomDTO.class), eq(CoffeeLetterRoom.class))).thenReturn(room);
    }

    @Test
    @DisplayName("새로운 채팅방 생성 성공 테스트")
    void createRoomTest() {
        // Given
        when(roomRepository.findByCoffeeChatId(anyLong()))
                .thenReturn(Optional.empty());
        when(roomRepository.save(any(CoffeeLetterRoom.class))).thenReturn(room);

        // When
        CoffeeLetterRoomDTO result = roomService.createRoom(roomDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getMentorId()).isEqualTo(roomDTO.getMentorId());
        assertThat(result.getMenteeId()).isEqualTo(roomDTO.getMenteeId());

        verify(roomRepository, times(1)).save(any(CoffeeLetterRoom.class));
        verify(messageService, times(1)).sendSystemMessage(anyString(), anyString());
    }

    @Test
    @DisplayName("이미 존재하는 채팅방 생성 시 예외 발생 테스트")
    void createRoomWithExistingRoomTest() {
        // Given
        when(roomRepository.findByCoffeeChatId(anyLong()))
                .thenReturn(Optional.of(room));

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            roomService.createRoom(roomDTO);
        });
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.COFFEELETTER_ALREADY_EXIST);
        verify(roomRepository, never()).save(any(CoffeeLetterRoom.class));
    }
    
    @Test
    @DisplayName("채팅방 비활성화 성공 테스트")
    void endRoomTest() {
        // Given
        String roomId = "test-room-id";
        CoffeeLetterRoom activeRoom = new CoffeeLetterRoom();
        activeRoom.setId(roomId);
        activeRoom.setStatus(CoffeeLetterRoom.RoomStatus.ACTIVE);
        
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(activeRoom));
        when(roomRepository.save(any(CoffeeLetterRoom.class))).thenReturn(activeRoom);
        
        // When
        CoffeeLetterRoomDTO result = roomService.endRoom(roomId);
        
        // Then
        assertThat(result).isNotNull();
        verify(roomRepository, atLeastOnce()).findById(roomId);
        verify(roomRepository, times(1)).save(any(CoffeeLetterRoom.class));
        assertThat(activeRoom.getStatus()).isEqualTo(CoffeeLetterRoom.RoomStatus.INACTIVE);
        verify(messageService, times(1)).sendSystemMessage(anyString(), anyString());
    }
    
    @Test
    @DisplayName("존재하지 않는 채팅방 비활성화 시 예외 발생 테스트")
    void endRoomWithNonExistingRoomTest() {
        // Given
        String roomId = "non-existent-room-id";
        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());
        
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            roomService.endRoom(roomId);
        });
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.COFFEELETTER_ROOM_NOT_FOUND);
        
        verify(roomRepository, times(1)).findById(roomId);
        verify(roomRepository, never()).save(any(CoffeeLetterRoom.class));
    }
    
    @Test
    @DisplayName("채팅방 취소 성공 테스트")
    void cancelRoomTest() {
        // Given
        String roomId = "test-room-id";
        CoffeeLetterRoom activeRoom = new CoffeeLetterRoom();
        activeRoom.setId(roomId);
        activeRoom.setStatus(CoffeeLetterRoom.RoomStatus.ACTIVE);
        
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(activeRoom));
        when(roomRepository.save(any(CoffeeLetterRoom.class))).thenReturn(activeRoom);
        
        // When
        CoffeeLetterRoomDTO result = roomService.cancelRoom(roomId);
        
        // Then
        assertThat(result).isNotNull();
        verify(roomRepository, atLeastOnce()).findById(roomId);
        verify(roomRepository, times(1)).save(any(CoffeeLetterRoom.class));
        assertThat(activeRoom.getStatus()).isEqualTo(CoffeeLetterRoom.RoomStatus.CANCELED);
        verify(messageService, times(1)).sendSystemMessage(anyString(), anyString());
    }
    
    @Test
    @DisplayName("존재하지 않는 채팅방 취소 시 예외 발생 테스트")
    void cancelRoomWithNonExistingRoomTest() {
        // Given
        String roomId = "non-existent-room-id";
        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());
        
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            roomService.cancelRoom(roomId);
        });
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.COFFEELETTER_ROOM_NOT_FOUND);
        
        verify(roomRepository, times(1)).findById(roomId);
        verify(roomRepository, never()).save(any(CoffeeLetterRoom.class));
    }
    
    @Test
    @DisplayName("모든 채팅방 조회 성공 테스트")
    void getAllRoomsTest() {
        // Given
        when(roomRepository.findAll()).thenReturn(rooms);
        
        // When
        List<CoffeeLetterRoomDTO> result = roomService.getAllRooms();
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        verify(roomRepository, times(1)).findAll();
    }
    
    @Test
    @DisplayName("채팅방이 없을 때 빈 목록 반환 테스트")
    void getAllRoomsEmptyTest() {
        // Given
        when(roomRepository.findAll()).thenReturn(Collections.emptyList());
        
        // When
        List<CoffeeLetterRoomDTO> result = roomService.getAllRooms();
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(roomRepository, times(1)).findAll();
    }
    
    @Test
    @DisplayName("채팅방 ID로 채팅방 조회 성공 테스트")
    void getRoomByIdTest() {
        // Given
        String roomId = "test-room-id";
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        
        // When
        CoffeeLetterRoomDTO result = roomService.getRoomById(roomId);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getMentorId()).isEqualTo(roomDTO.getMentorId());
        assertThat(result.getMenteeId()).isEqualTo(roomDTO.getMenteeId());
        verify(roomRepository, times(1)).findById(roomId);
    }
    
    @Test
    @DisplayName("존재하지 않는 채팅방 ID로 조회 시 예외 발생 테스트")
    void getRoomByIdWithNonExistingRoomTest() {
        // Given
        String roomId = "non-existent-room-id";
        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());
        
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            roomService.getRoomById(roomId);
        });
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.COFFEELETTER_ROOM_NOT_FOUND);
        
        verify(roomRepository, times(1)).findById(roomId);
    }
    
    @Test
    @DisplayName("사용자 ID로 채팅방 목록 조회 성공 테스트")
    void getRoomsByUserIdTest() {
        // Given
        Long userId = 1L;
        String userIdStr = userId.toString();
        when(roomRepository.findByParticipantsContaining(userIdStr)).thenReturn(rooms);
        
        // When
        List<CoffeeLetterRoomDTO> result = roomService.getRoomsByUserId(userId);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        verify(roomRepository, times(1)).findByParticipantsContaining(userIdStr);
    }
    
    @Test
    @DisplayName("사용자 ID로 채팅방 목록 조회 시 빈 목록 반환 테스트")
    void getRoomsByUserIdEmptyTest() {
        // Given
        Long userId = 1L;
        String userIdStr = userId.toString();
        when(roomRepository.findByParticipantsContaining(userIdStr)).thenReturn(Collections.emptyList());
        
        // When
        List<CoffeeLetterRoomDTO> result = roomService.getRoomsByUserId(userId);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(roomRepository, times(1)).findByParticipantsContaining(userIdStr);
    }
    
    @Test
    @DisplayName("사용자 ID와 상태로 채팅방 목록 조회 성공 테스트")
    void getRoomsByUserIdAndStatusTest() {
        // Given
        Long userId = 1L;
        String userIdStr = userId.toString();
        CoffeeLetterRoom.RoomStatus status = CoffeeLetterRoom.RoomStatus.ACTIVE;
        when(roomRepository.findByParticipantsContainingAndStatus(userIdStr, status)).thenReturn(rooms);
        
        // When
        List<CoffeeLetterRoomDTO> result = roomService.getRoomsByUserIdAndStatus(userId, status);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        verify(roomRepository, times(1)).findByParticipantsContainingAndStatus(userIdStr, status);
    }
    
    @Test
    @DisplayName("사용자 ID와 상태로 채팅방 목록 조회 시 빈 목록 반환 테스트")
    void getRoomsByUserIdAndStatusEmptyTest() {
        // Given
        Long userId = 1L;
        String userIdStr = userId.toString();
        CoffeeLetterRoom.RoomStatus status = CoffeeLetterRoom.RoomStatus.ACTIVE;
        when(roomRepository.findByParticipantsContainingAndStatus(userIdStr, status)).thenReturn(Collections.emptyList());
        
        // When
        List<CoffeeLetterRoomDTO> result = roomService.getRoomsByUserIdAndStatus(userId, status);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(roomRepository, times(1)).findByParticipantsContainingAndStatus(userIdStr, status);
    }
    
    @Test
    @DisplayName("커피챗 ID로 채팅방 ID 조회 성공 테스트")
    void findRoomIdByCoffeeChatIdTest() {
        // Given
        Long coffeeChatId = 100L;
        when(roomRepository.findByCoffeeChatId(coffeeChatId)).thenReturn(Optional.of(room));
        
        // When
        String result = roomService.findRoomIdByCoffeeChatId(coffeeChatId);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(room.getId());
        verify(roomRepository, times(1)).findByCoffeeChatId(coffeeChatId);
    }
    
    @Test
    @DisplayName("존재하지 않는 커피챗 ID로 채팅방 ID 조회 시 예외 발생 테스트")
    void findRoomIdByCoffeeChatIdWithNonExistingCoffeeChatTest() {
        // Given
        Long coffeeChatId = 999L;
        when(roomRepository.findByCoffeeChatId(coffeeChatId)).thenReturn(Optional.empty());
        
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            roomService.findRoomIdByCoffeeChatId(coffeeChatId);
        });
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.COFFEELETTER_NOT_FOUND);
        
        verify(roomRepository, times(1)).findByCoffeeChatId(coffeeChatId);
    }
    
    @Test
    @DisplayName("커피챗 ID로 채팅방 조회 성공 테스트")
    void getRoomByCoffeeChatIdTest() {
        // Given
        Long coffeeChatId = 100L;
        when(roomRepository.findByCoffeeChatId(coffeeChatId)).thenReturn(Optional.of(room));
        
        // When
        CoffeeLetterRoomDTO result = roomService.getRoomByCoffeeChatId(coffeeChatId);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getMentorId()).isEqualTo(roomDTO.getMentorId());
        assertThat(result.getMenteeId()).isEqualTo(roomDTO.getMenteeId());
        verify(roomRepository, times(1)).findByCoffeeChatId(coffeeChatId);
    }
    
    @Test
    @DisplayName("존재하지 않는 커피챗 ID로 채팅방 조회 시 예외 발생 테스트")
    void getRoomByCoffeeChatIdWithNonExistingCoffeeChatTest() {
        // Given
        Long coffeeChatId = 999L;
        when(roomRepository.findByCoffeeChatId(coffeeChatId)).thenReturn(Optional.empty());
        
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            roomService.getRoomByCoffeeChatId(coffeeChatId);
        });
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.COFFEELETTER_NOT_FOUND);
        
        verify(roomRepository, times(1)).findByCoffeeChatId(coffeeChatId);
    }
} 