package com.newbit.newbitfeatureservice.coffeeletter.service;

import com.newbit.newbitfeatureservice.coffeeletter.domain.chat.ChatMessage;
import com.newbit.newbitfeatureservice.coffeeletter.domain.chat.CoffeeLetterRoom;
import com.newbit.newbitfeatureservice.coffeeletter.domain.chat.MessageType;
import com.newbit.newbitfeatureservice.coffeeletter.dto.ChatMessageDTO;
import com.newbit.newbitfeatureservice.coffeeletter.dto.CoffeeLetterRoomDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("채팅 서비스 단위 테스트")
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

    @Test
    @DisplayName("새로운 채팅방 생성 성공 테스트")
    void createRoomTest() {
        // Given
        when(roomService.createRoom(any(CoffeeLetterRoomDTO.class))).thenReturn(roomDTO);

        // When
        CoffeeLetterRoomDTO result = chatService.createRoom(roomDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getMentorId()).isEqualTo(roomDTO.getMentorId());
        assertThat(result.getMenteeId()).isEqualTo(roomDTO.getMenteeId());

        verify(roomService, times(1)).createRoom(roomDTO);
    }
    
    @Test
    @DisplayName("채팅방 비활성화 성공 테스트")
    void endRoomTest() {
        // Given
        String roomId = "test-room-id";
        when(roomService.endRoom(roomId)).thenReturn(roomDTO);
        
        // When
        CoffeeLetterRoomDTO result = chatService.endRoom(roomId);
        
        // Then
        assertThat(result).isNotNull();
        verify(roomService, times(1)).endRoom(roomId);
    }
    
    @Test
    @DisplayName("채팅방 취소 성공 테스트")
    void cancelRoomTest() {
        // Given
        String roomId = "test-room-id";
        when(roomService.cancelRoom(roomId)).thenReturn(roomDTO);
        
        // When
        CoffeeLetterRoomDTO result = chatService.cancelRoom(roomId);
        
        // Then
        assertThat(result).isNotNull();
        verify(roomService, times(1)).cancelRoom(roomId);
    }
    
    @Test
    @DisplayName("모든 채팅방 조회 성공 테스트")
    void getAllRoomsTest() {
        // Given
        when(roomService.getAllRooms()).thenReturn(roomDTOs);
        
        // When
        List<CoffeeLetterRoomDTO> result = chatService.getAllRooms();
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        verify(roomService, times(1)).getAllRooms();
    }
    
    @Test
    @DisplayName("채팅방 ID로 채팅방 조회 성공 테스트")
    void getRoomByIdTest() {
        // Given
        String roomId = "test-room-id";
        when(roomService.getRoomById(roomId)).thenReturn(roomDTO);
        
        // When
        CoffeeLetterRoomDTO result = chatService.getRoomById(roomId);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getMentorId()).isEqualTo(roomDTO.getMentorId());
        assertThat(result.getMenteeId()).isEqualTo(roomDTO.getMenteeId());
        verify(roomService, times(1)).getRoomById(roomId);
    }
    
    @Test
    @DisplayName("사용자 ID로 채팅방 목록 조회 성공 테스트")
    void getRoomsByUserIdTest() {
        // Given
        Long userId = 1L;
        when(roomService.getRoomsByUserId(userId)).thenReturn(roomDTOs);
        
        // When
        List<CoffeeLetterRoomDTO> result = chatService.getRoomsByUserId(userId);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        verify(roomService, times(1)).getRoomsByUserId(userId);
    }
    
    @Test
    @DisplayName("사용자 ID와 상태로 채팅방 목록 조회 성공 테스트")
    void getRoomsByUserIdAndStatusTest() {
        // Given
        Long userId = 1L;
        CoffeeLetterRoom.RoomStatus status = CoffeeLetterRoom.RoomStatus.ACTIVE;
        when(roomService.getRoomsByUserIdAndStatus(userId, status)).thenReturn(roomDTOs);
        
        // When
        List<CoffeeLetterRoomDTO> result = chatService.getRoomsByUserIdAndStatus(userId, status);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        verify(roomService, times(1)).getRoomsByUserIdAndStatus(userId, status);
    }
    
    @Test
    @DisplayName("커피챗 ID로 채팅방 ID 조회 성공 테스트")
    void findRoomIdByCoffeeChatIdTest() {
        // Given
        Long coffeeChatId = 100L;
        String expectedRoomId = "test-room-id";
        when(roomService.findRoomIdByCoffeeChatId(coffeeChatId)).thenReturn(expectedRoomId);
        
        // When
        String result = chatService.findRoomIdByCoffeeChatId(coffeeChatId);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedRoomId);
        verify(roomService, times(1)).findRoomIdByCoffeeChatId(coffeeChatId);
    }
    
    @Test
    @DisplayName("커피챗 ID로 채팅방 조회 성공 테스트")
    void getRoomByCoffeeChatIdTest() {
        // Given
        Long coffeeChatId = 100L;
        when(roomService.getRoomByCoffeeChatId(coffeeChatId)).thenReturn(roomDTO);
        
        // When
        CoffeeLetterRoomDTO result = chatService.getRoomByCoffeeChatId(coffeeChatId);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(roomDTO.getId());
        assertThat(result.getCoffeeChatId()).isEqualTo(roomDTO.getCoffeeChatId());
        verify(roomService, times(1)).getRoomByCoffeeChatId(coffeeChatId);
    }
    
    // === MessageService 메서드 테스트 ===
    
    @Test
    @DisplayName("메시지 전송 성공 테스트")
    void sendMessageTest() {
        // Given
        when(messageService.sendMessage(any(ChatMessageDTO.class))).thenReturn(messageDTO);
        
        // When
        ChatMessageDTO result = chatService.sendMessage(messageDTO);
        
        // Then
        assertThat(result).isNotNull();
        verify(messageService, times(1)).sendMessage(messageDTO);
    }
    
    @Test
    @DisplayName("시스템 메시지 전송 성공 테스트")
    void sendSystemMessageTest() {
        // Given
        String roomId = "test-room-id";
        String content = "시스템 메시지 테스트";
        
        when(messageService.sendSystemMessage(roomId, content)).thenReturn(messageDTO);
        
        // When
        ChatMessageDTO result = chatService.sendSystemMessage(roomId, content);
        
        // Then
        assertThat(result).isNotNull();
        verify(messageService, times(1)).sendSystemMessage(roomId, content);
    }
    
    @Test
    @DisplayName("채팅방 메시지 목록 조회 성공 테스트")
    void getMessagesByRoomIdTest() {
        // Given
        String roomId = "test-room-id";
        when(messageService.getMessagesByRoomId(roomId)).thenReturn(messageDTOs);
        
        // When
        List<ChatMessageDTO> result = chatService.getMessagesByRoomId(roomId);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        verify(messageService, times(1)).getMessagesByRoomId(roomId);
    }
    
    @Test
    @DisplayName("채팅방 메시지 페이징 조회 성공 테스트")
    void getPagedMessagesByRoomIdTest() {
        // Given
        String roomId = "test-room-id";
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        
        when(messageService.getMessagesByRoomId(roomId, pageable)).thenReturn(messagePage);
        
        // When
        Page<ChatMessageDTO> result = chatService.getMessagesByRoomId(roomId, pageable);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(messageService, times(1)).getMessagesByRoomId(roomId, pageable);
    }
    
    @Test
    @DisplayName("읽지 않은 메시지 조회 성공 테스트")
    void getUnreadMessagesTest() {
        // Given
        String roomId = "test-room-id";
        Long userId = 1L;
        
        when(messageService.getUnreadMessages(roomId, userId)).thenReturn(messageDTOs);
        
        // When
        List<ChatMessageDTO> results = chatService.getUnreadMessages(roomId, userId);
        
        // Then
        assertThat(results).isNotNull();
        assertThat(results).hasSize(1);
        verify(messageService, times(1)).getUnreadMessages(roomId, userId);
    }
    
    @Test
    @DisplayName("메시지 읽음 처리 성공 테스트")
    void markAsReadTest() {
        // Given
        String roomId = "test-room-id";
        Long userId = 1L;
        
        // When
        chatService.markAsRead(roomId, userId);
        
        // Then
        verify(messageService, times(1)).markAsRead(roomId, userId);
    }
    
    @Test
    @DisplayName("읽지 않은 메시지 수 조회 성공 테스트")
    void getUnreadMessageCountTest() {
        // Given
        String roomId = "test-room-id";
        Long userId = 1L;
        int expectedCount = 3;
        
        when(messageService.getUnreadMessageCount(roomId, userId)).thenReturn(expectedCount);
        
        // When
        int actualCount = chatService.getUnreadMessageCount(roomId, userId);
        
        // Then
        assertThat(actualCount).isEqualTo(expectedCount);
        verify(messageService, times(1)).getUnreadMessageCount(roomId, userId);
    }
    
    @Test
    @DisplayName("마지막 메시지 조회 성공 테스트")
    void getLastMessageTest() {
        // Given
        String roomId = "test-room-id";
        
        when(messageService.getLastMessage(roomId)).thenReturn(messageDTO);
        
        // When
        ChatMessageDTO result = chatService.getLastMessage(roomId);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(messageDTO.getId());
        assertThat(result.getRoomId()).isEqualTo(messageDTO.getRoomId());
        verify(messageService, times(1)).getLastMessage(roomId);
    }
    
    @Test
    @DisplayName("메시지가 없는 경우 마지막 메시지 조회 테스트")
    void getLastMessageEmptyTest() {
        // Given
        String roomId = "empty-room-id";
        when(messageService.getLastMessage(roomId)).thenReturn(null);
        
        // When
        ChatMessageDTO result = chatService.getLastMessage(roomId);
        
        // Then
        assertThat(result).isNull();
        verify(messageService, times(1)).getLastMessage(roomId);
    }
} 