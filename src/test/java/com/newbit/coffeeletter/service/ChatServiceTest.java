package com.newbit.coffeeletter.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.newbit.coffeeletter.domain.chat.ChatMessage;
import com.newbit.coffeeletter.domain.chat.CoffeeLetterRoom;
import com.newbit.coffeeletter.domain.chat.MessageType;
import com.newbit.coffeeletter.dto.ChatMessageDTO;
import com.newbit.coffeeletter.dto.CoffeeLetterRoomDTO;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ChatServiceTest {

    @Mock
    private RoomService roomService;
    
    @Mock
    private MessageService messageService;

    @InjectMocks
    private ChatServiceImpl chatService;

    private CoffeeLetterRoomDTO roomDTO;
    private CoffeeLetterRoom room;
    private List<CoffeeLetterRoomDTO> roomDTOs;
    private ChatMessageDTO messageDTO;
    private ChatMessage message;
    private List<ChatMessageDTO> messageDTOs;
    private Page<ChatMessageDTO> messagePage;

    @BeforeEach
    void setUp() {
        roomDTO = new CoffeeLetterRoomDTO();
        roomDTO.setId("test-room-id");
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

        roomDTOs = Arrays.asList(roomDTO);
        
        messageDTO = new ChatMessageDTO();
        messageDTO.setId("test-message-id");
        messageDTO.setRoomId("test-room-id");
        messageDTO.setSenderId(1L);
        messageDTO.setSenderName("멘토");
        messageDTO.setContent("안녕하세요");
        messageDTO.setType(MessageType.CHAT);
        messageDTO.setTimestamp(LocalDateTime.now());
        
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
        messageDTOs = Arrays.asList(messageDTO);
        messagePage = new PageImpl<>(messageDTOs);
    }

    // === RoomService 메서드 테스트 ===

    @Test
    void createRoom_새로운_채팅방_생성_성공() {
        // given
        when(roomService.createRoom(any(CoffeeLetterRoomDTO.class))).thenReturn(roomDTO);

        // when
        CoffeeLetterRoomDTO result = chatService.createRoom(roomDTO);

        // then
        assertNotNull(result);
        assertEquals(roomDTO.getMentorId(), result.getMentorId());
        assertEquals(roomDTO.getMenteeId(), result.getMenteeId());

        verify(roomService, times(1)).createRoom(roomDTO);
    }
    
    @Test
    void endRoom_채팅방_비활성화_성공() {
        // given
        String roomId = "test-room-id";
        when(roomService.endRoom(roomId)).thenReturn(roomDTO);
        
        // when
        CoffeeLetterRoomDTO result = chatService.endRoom(roomId);
        
        // then
        assertNotNull(result);
        verify(roomService, times(1)).endRoom(roomId);
    }
    
    @Test
    void cancelRoom_채팅방_취소_성공() {
        // given
        String roomId = "test-room-id";
        when(roomService.cancelRoom(roomId)).thenReturn(roomDTO);
        
        // when
        CoffeeLetterRoomDTO result = chatService.cancelRoom(roomId);
        
        // then
        assertNotNull(result);
        verify(roomService, times(1)).cancelRoom(roomId);
    }
    
    @Test
    void getAllRooms_모든_채팅방_조회_성공() {
        // given
        when(roomService.getAllRooms()).thenReturn(roomDTOs);
        
        // when
        List<CoffeeLetterRoomDTO> result = chatService.getAllRooms();
        
        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(roomService, times(1)).getAllRooms();
    }
    
    @Test
    void getRoomById_채팅방_조회_성공() {
        // given
        String roomId = "test-room-id";
        when(roomService.getRoomById(roomId)).thenReturn(roomDTO);
        
        // when
        CoffeeLetterRoomDTO result = chatService.getRoomById(roomId);
        
        // then
        assertNotNull(result);
        assertEquals(roomDTO.getMentorId(), result.getMentorId());
        assertEquals(roomDTO.getMenteeId(), result.getMenteeId());
        verify(roomService, times(1)).getRoomById(roomId);
    }
    
    @Test
    void getRoomsByUserId_사용자_채팅방_조회_성공() {
        // given
        Long userId = 1L;
        when(roomService.getRoomsByUserId(userId)).thenReturn(roomDTOs);
        
        // when
        List<CoffeeLetterRoomDTO> result = chatService.getRoomsByUserId(userId);
        
        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(roomService, times(1)).getRoomsByUserId(userId);
    }
    
    @Test
    void getRoomsByUserIdAndStatus_사용자와_상태별_채팅방_조회_성공() {
        // given
        Long userId = 1L;
        CoffeeLetterRoom.RoomStatus status = CoffeeLetterRoom.RoomStatus.ACTIVE;
        when(roomService.getRoomsByUserIdAndStatus(userId, status)).thenReturn(roomDTOs);
        
        // when
        List<CoffeeLetterRoomDTO> result = chatService.getRoomsByUserIdAndStatus(userId, status);
        
        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(roomService, times(1)).getRoomsByUserIdAndStatus(userId, status);
    }
    
    // === MessageService 메서드 테스트 ===
    
    @Test
    void sendMessage_메시지_전송_성공() {
        // given
        when(messageService.sendMessage(any(ChatMessageDTO.class))).thenReturn(messageDTO);
        
        // when
        ChatMessageDTO result = chatService.sendMessage(messageDTO);
        
        // then
        assertNotNull(result);
        verify(messageService, times(1)).sendMessage(messageDTO);
    }
    
    @Test
    void sendSystemMessage_시스템_메시지_전송_성공() {
        // given
        String roomId = "test-room-id";
        String content = "시스템 메시지 테스트";
        
        when(messageService.sendSystemMessage(roomId, content)).thenReturn(messageDTO);
        
        // when
        ChatMessageDTO result = chatService.sendSystemMessage(roomId, content);
        
        // then
        assertNotNull(result);
        verify(messageService, times(1)).sendSystemMessage(roomId, content);
    }
    
    @Test
    void getMessagesByRoomId_채팅방_메시지_조회_성공() {
        // given
        String roomId = "test-room-id";
        when(messageService.getMessagesByRoomId(roomId)).thenReturn(messageDTOs);
        
        // when
        List<ChatMessageDTO> result = chatService.getMessagesByRoomId(roomId);
        
        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(messageService, times(1)).getMessagesByRoomId(roomId);
    }
    
    @Test
    void getMessagesByRoomId_페이징_메시지_조회_성공() {
        // given
        String roomId = "test-room-id";
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        
        when(messageService.getMessagesByRoomId(roomId, pageable)).thenReturn(messagePage);
        
        // when
        Page<ChatMessageDTO> result = chatService.getMessagesByRoomId(roomId, pageable);
        
        // then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(messageService, times(1)).getMessagesByRoomId(roomId, pageable);
    }
    
    @Test
    void getUnreadMessages_읽지않은_메시지_조회_성공() {
        // given
        String roomId = "test-room-id";
        Long userId = 1L;
        
        when(messageService.getUnreadMessages(roomId, userId)).thenReturn(messageDTOs);
        
        // when
        List<ChatMessageDTO> results = chatService.getUnreadMessages(roomId, userId);
        
        // then
        assertNotNull(results);
        assertEquals(1, results.size());
        verify(messageService, times(1)).getUnreadMessages(roomId, userId);
    }
    
    @Test
    void markAsRead_메시지_읽음_처리_성공() {
        // given
        String roomId = "test-room-id";
        Long userId = 1L;
        
        // when
        chatService.markAsRead(roomId, userId);
        
        // then
        verify(messageService, times(1)).markAsRead(roomId, userId);
    }
    
    @Test
    void getUnreadMessageCount_읽지않은_메시지_카운트_조회_성공() {
        // given
        String roomId = "test-room-id";
        Long userId = 1L;
        int expectedCount = 3;
        
        when(messageService.getUnreadMessageCount(roomId, userId)).thenReturn(expectedCount);
        
        // when
        int actualCount = chatService.getUnreadMessageCount(roomId, userId);
        
        // then
        assertEquals(expectedCount, actualCount);
        verify(messageService, times(1)).getUnreadMessageCount(roomId, userId);
    }
    
    @Test
    void getLastMessage_성공적인_조회() {
        // given
        String roomId = "test-room-id";
        
        when(messageService.getLastMessage(roomId)).thenReturn(messageDTO);
        
        // when
        ChatMessageDTO result = chatService.getLastMessage(roomId);
        
        // then
        assertNotNull(result);
        assertEquals(messageDTO.getId(), result.getId());
        assertEquals(messageDTO.getRoomId(), result.getRoomId());
        verify(messageService, times(1)).getLastMessage(roomId);
    }
    
    @Test
    void getLastMessage_메시지가_없는_경우() {
        // given
        String roomId = "empty-room-id";
        when(messageService.getLastMessage(roomId)).thenReturn(null);
        
        // when
        ChatMessageDTO result = chatService.getLastMessage(roomId);
        
        // then
        assertNull(result);
        verify(messageService, times(1)).getLastMessage(roomId);
    }
} 