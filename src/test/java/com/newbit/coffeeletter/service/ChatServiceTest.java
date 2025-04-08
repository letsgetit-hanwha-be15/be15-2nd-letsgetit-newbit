package com.newbit.coffeeletter.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.newbit.coffeeletter.domain.chat.ChatMessage;
import com.newbit.coffeeletter.domain.chat.CoffeeLetterRoom;
import com.newbit.coffeeletter.domain.chat.MessageType;
import com.newbit.coffeeletter.dto.ChatMessageDTO;
import com.newbit.coffeeletter.dto.CoffeeLetterRoomDTO;
import com.newbit.coffeeletter.repository.ChatMessageRepository;
import com.newbit.coffeeletter.repository.CoffeeLetterRoomRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ChatServiceTest {

    @Mock
    private CoffeeLetterRoomRepository roomRepository;
    
    @Mock
    private ChatMessageRepository messageRepository;
    
    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ChatServiceImpl chatService;

    private CoffeeLetterRoomDTO roomDTO;
    private CoffeeLetterRoom room;
    private List<CoffeeLetterRoom> rooms;
    private ChatMessageDTO messageDTO;
    private ChatMessage message;
    private List<ChatMessage> messages;
    private Page<ChatMessage> messagePage;

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
        
        messageDTO = new ChatMessageDTO();
        messageDTO.setRoomId("test-room-id");
        messageDTO.setSenderId(1L);
        messageDTO.setSenderName("멘토");
        messageDTO.setContent("안녕하세요");
        messageDTO.setType(MessageType.CHAT);
        
        message = new ChatMessage();
        message.setId("test-message-id");
        message.setRoomId("test-room-id");
        message.setSenderId(1L);
        message.setSenderName("멘토");
        message.setContent("안녕하세요");
        message.setType(MessageType.CHAT);
        message.setTimestamp(LocalDateTime.now());
        message.setReadByMentor(true);
        message.setReadByMentee(false);
        
        // 메시지 목록 및 페이지 설정
        messages = Arrays.asList(message);
        messagePage = new PageImpl<>(messages);
        
        when(modelMapper.map(any(Object.class), eq(CoffeeLetterRoomDTO.class))).thenReturn(roomDTO);
        when(modelMapper.map(any(Object.class), eq(ChatMessageDTO.class))).thenReturn(messageDTO);
        when(modelMapper.map(any(CoffeeLetterRoomDTO.class), eq(CoffeeLetterRoom.class))).thenReturn(room);
        when(modelMapper.map(any(ChatMessageDTO.class), eq(ChatMessage.class))).thenReturn(message);
    }

    @Test
    void createRoom_새로운_채팅방_생성_성공() {
        // given
        when(roomRepository.findByCoffeeChatId(anyLong()))
                .thenReturn(Optional.empty());
        when(roomRepository.save(any(CoffeeLetterRoom.class))).thenReturn(room);
        when(roomRepository.findById(anyString())).thenReturn(Optional.of(room));
        when(messageRepository.save(any(ChatMessage.class))).thenReturn(message);

        // when
        CoffeeLetterRoomDTO result = chatService.createRoom(roomDTO);

        // then
        assertNotNull(result);
        assertEquals(roomDTO.getMentorId(), result.getMentorId());
        assertEquals(roomDTO.getMenteeId(), result.getMenteeId());

        verify(roomRepository, times(2)).save(any(CoffeeLetterRoom.class));

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
        when(messageRepository.save(any(ChatMessage.class))).thenReturn(message);
        
        // when
        CoffeeLetterRoomDTO result = chatService.endRoom(roomId);
        
        // then
        assertNotNull(result);
        verify(roomRepository, atLeastOnce()).findById(roomId);
        verify(roomRepository, times(2)).save(any(CoffeeLetterRoom.class));
        assertEquals(CoffeeLetterRoom.RoomStatus.INACTIVE, activeRoom.getStatus());
        verify(messageRepository, times(1)).save(any(ChatMessage.class));
        verify(messagingTemplate, times(1)).convertAndSend(anyString(), any(Object.class));
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
        when(messageRepository.save(any(ChatMessage.class))).thenReturn(message);
        
        // when
        CoffeeLetterRoomDTO result = chatService.cancelRoom(roomId);
        
        // then
        assertNotNull(result);
        verify(roomRepository, atLeastOnce()).findById(roomId);
        verify(roomRepository, times(2)).save(any(CoffeeLetterRoom.class));
        assertEquals(CoffeeLetterRoom.RoomStatus.CANCELED, activeRoom.getStatus());
        verify(messageRepository, times(1)).save(any(ChatMessage.class));
        verify(messagingTemplate, times(1)).convertAndSend(anyString(), any(Object.class));
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
    
    @Test
    void sendMessage_멘토가_보낸_메시지_성공() {
        // given
        String roomId = "test-room-id";
        Long mentorId = 1L;
        
        messageDTO.setSenderId(mentorId);
        message.setSenderId(mentorId);
        message.setReadByMentor(true);
        message.setReadByMentee(false);
        
        when(messageRepository.save(any(ChatMessage.class))).thenReturn(message);
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(roomRepository.save(any(CoffeeLetterRoom.class))).thenReturn(room);
        
        // when
        ChatMessageDTO result = chatService.sendMessage(messageDTO);
        
        // then
        assertNotNull(result);
        verify(messageRepository, times(1)).save(any(ChatMessage.class));
        verify(roomRepository, atLeastOnce()).findById(roomId);
        verify(roomRepository, times(1)).save(any(CoffeeLetterRoom.class));
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/chat/room/" + roomId), any(Object.class));
        assertTrue(message.isReadByMentor());
        assertEquals(false, message.isReadByMentee());
    }
    
    @Test
    void sendMessage_멘티가_보낸_메시지_성공() {
        // given
        String roomId = "test-room-id";
        Long menteeId = 2L;
        
        messageDTO.setSenderId(menteeId);
        message.setSenderId(menteeId);
        message.setReadByMentor(false);
        message.setReadByMentee(true);
        
        when(messageRepository.save(any(ChatMessage.class))).thenReturn(message);
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(roomRepository.save(any(CoffeeLetterRoom.class))).thenReturn(room);
        
        // when
        ChatMessageDTO result = chatService.sendMessage(messageDTO);
        
        // then
        assertNotNull(result);
        verify(messageRepository, times(1)).save(any(ChatMessage.class));
        verify(roomRepository, atLeastOnce()).findById(roomId);
        verify(roomRepository, times(1)).save(any(CoffeeLetterRoom.class));
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/chat/room/" + roomId), any(Object.class));
        assertTrue(message.isReadByMentee());
        assertEquals(false, message.isReadByMentor());
    }
    
    @Test
    void sendSystemMessage_시스템_메시지_전송_성공() {
        // given
        String roomId = "test-room-id";
        String content = "시스템 메시지 테스트";
        
        ChatMessage systemMessage = new ChatMessage();
        systemMessage.setRoomId(roomId);
        systemMessage.setType(MessageType.SYSTEM);
        systemMessage.setContent(content);
        systemMessage.setSenderId(0L);
        systemMessage.setSenderName("System");
        
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(messageRepository.save(any(ChatMessage.class))).thenReturn(systemMessage);
        when(roomRepository.save(any(CoffeeLetterRoom.class))).thenReturn(room);
        
        // when
        ChatMessageDTO result = chatService.sendSystemMessage(roomId, content);
        
        // then
        assertNotNull(result);
        verify(roomRepository, atLeastOnce()).findById(roomId);
        verify(messageRepository, times(1)).save(any(ChatMessage.class));
        verify(roomRepository, times(1)).save(any(CoffeeLetterRoom.class));
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/chat/room/" + roomId), any(Object.class));
    }
    
    @Test
    void sendSystemMessage_존재하지_않는_채팅방_예외발생() {
        // given
        String roomId = "non-existent-room-id";
        String content = "시스템 메시지 테스트";
        
        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());
        
        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            chatService.sendSystemMessage(roomId, content);
        });
        
        verify(roomRepository, times(1)).findById(roomId);
        verify(messageRepository, never()).save(any(ChatMessage.class));
        verify(messagingTemplate, never()).convertAndSend(anyString(), any(Object.class));
    }
    
    @Test
    void getMessagesByRoomId_채팅방_메시지_조회_성공() {
        // given
        String roomId = "test-room-id";
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(messageRepository.findByRoomId(roomId)).thenReturn(messages);
        
        // when
        List<ChatMessageDTO> result = chatService.getMessagesByRoomId(roomId);
        
        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(roomRepository, times(1)).findById(roomId);
        verify(messageRepository, times(1)).findByRoomId(roomId);
    }
    
    @Test
    void getMessagesByRoomId_빈_목록_반환() {
        // given
        String roomId = "test-room-id";
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(messageRepository.findByRoomId(roomId)).thenReturn(Collections.emptyList());
        
        // when
        List<ChatMessageDTO> result = chatService.getMessagesByRoomId(roomId);
        
        // then
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(roomRepository, times(1)).findById(roomId);
        verify(messageRepository, times(1)).findByRoomId(roomId);
    }
    
    @Test
    void getMessagesByRoomId_존재하지_않는_채팅방_예외발생() {
        // given
        String roomId = "non-existent-room-id";
        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());
        
        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            chatService.getMessagesByRoomId(roomId);
        });
        
        verify(roomRepository, times(1)).findById(roomId);
        verify(messageRepository, never()).findByRoomId(anyString());
    }
    
    @Test
    void getMessagesByRoomId_페이징_메시지_조회_성공() {
        // given
        String roomId = "test-room-id";
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(messageRepository.findByRoomId(eq(roomId), any(Pageable.class))).thenReturn(messagePage);
        
        // when
        Page<ChatMessageDTO> result = chatService.getMessagesByRoomId(roomId, pageable);
        
        // then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalElements());
        verify(roomRepository, times(1)).findById(roomId);
        verify(messageRepository, times(1)).findByRoomId(eq(roomId), any(Pageable.class));
    }
    
    @Test
    void getMessagesByRoomId_빈_페이지_반환() {
        // given
        String roomId = "test-room-id";
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<ChatMessage> emptyPage = new PageImpl<>(Collections.emptyList());
        
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(messageRepository.findByRoomId(eq(roomId), any(Pageable.class))).thenReturn(emptyPage);
        
        // when
        Page<ChatMessageDTO> result = chatService.getMessagesByRoomId(roomId, pageable);
        
        // then
        assertNotNull(result);
        assertEquals(0, result.getContent().size());
        assertEquals(0, result.getTotalElements());
        verify(roomRepository, times(1)).findById(roomId);
        verify(messageRepository, times(1)).findByRoomId(eq(roomId), any(Pageable.class));
    }
    
    @Test
    void getMessagesByRoomId_페이징_존재하지_않는_채팅방_예외발생() {
        // given
        String roomId = "non-existent-room-id";
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        
        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());
        
        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            chatService.getMessagesByRoomId(roomId, pageable);
        });
        
        verify(roomRepository, times(1)).findById(roomId);
        verify(messageRepository, never()).findByRoomId(anyString(), any(Pageable.class));
    }
} 