package com.newbit.newbitfeatureservice.coffeeletter.service;

import com.newbit.newbitfeatureservice.client.user.MentorFeignClient;
import com.newbit.newbitfeatureservice.coffeeletter.domain.chat.ChatMessage;
import com.newbit.newbitfeatureservice.coffeeletter.domain.chat.CoffeeLetterRoom;
import com.newbit.newbitfeatureservice.coffeeletter.domain.chat.MessageType;
import com.newbit.newbitfeatureservice.coffeeletter.dto.ChatMessageDTO;
import com.newbit.newbitfeatureservice.coffeeletter.repository.ChatMessageRepository;
import com.newbit.newbitfeatureservice.coffeeletter.repository.CoffeeLetterRoomRepository;
import com.newbit.newbitfeatureservice.coffeeletter.util.RoomUtils;
import com.newbit.newbitfeatureservice.notification.command.application.service.NotificationCommandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("메시지 서비스 단위 테스트")
class MessageServiceTest {

    @Mock
    private ChatMessageRepository messageRepository;
    
    @Mock
    private CoffeeLetterRoomRepository roomRepository;
    
    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private MentorFeignClient mentorFeignClient;

    @Mock
    private NotificationCommandService notificationCommandService;

    @InjectMocks
    private MessageServiceImpl messageService;

    private CoffeeLetterRoom room;
    private ChatMessageDTO messageDTO;
    private ChatMessage message;
    private List<ChatMessage> messages;
    private Page<ChatMessage> messagePage;

    @BeforeEach
    void setUp() {
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
        
        when(modelMapper.map(any(ChatMessageDTO.class), eq(ChatMessage.class))).thenReturn(message);
        when(modelMapper.map(any(ChatMessage.class), eq(ChatMessageDTO.class))).thenReturn(messageDTO);
    }
    
    @Test
    @DisplayName("멘토가 보낸 메시지 전송 성공 테스트")
    void sendMessageByMentorTest() {
        // Given
        String roomId = "123";
        Long mentorId = 1L;
        Long menteeId = 2L;

        messageDTO.setRoomId(roomId);
        messageDTO.setSenderId(mentorId);
        message.setSenderId(menteeId);
        message.setRoomId(roomId);
        message.setReadByMentor(false);
        message.setReadByMentee(true);

        CoffeeLetterRoom room = CoffeeLetterRoom.builder()
                .id(roomId)
                .mentorId(mentorId)
                .menteeId(menteeId)
                .unreadCountMentor(0)
                .unreadCountMentee(0)
                .build();
        
        try (MockedStatic<RoomUtils> mockedRoomUtils = Mockito.mockStatic(RoomUtils.class)) {
            mockedRoomUtils.when(() -> RoomUtils.getRoomById(roomRepository, roomId)).thenReturn(room);
            
            when(messageRepository.save(any(ChatMessage.class))).thenReturn(message);
            when(roomRepository.save(any(CoffeeLetterRoom.class))).thenReturn(room);
            
            // When
            ChatMessageDTO result = messageService.sendMessage(messageDTO);
            
            // Then
            assertThat(result).isNotNull();
            verify(messageRepository, times(1)).save(any(ChatMessage.class));
            verify(roomRepository, times(1)).save(any(CoffeeLetterRoom.class));
            verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/chat/room/" + roomId), any(Object.class));
            
            // RoomUtils.getRoomById가 한 번 호출되었는지 확인
            mockedRoomUtils.verify(() -> RoomUtils.getRoomById(roomRepository, roomId), times(1));
        }
    }
    
    @Test
    @DisplayName("멘티가 보낸 메시지 전송 성공 테스트")
    void sendMessageByMenteeTest() {
        // Given
        String roomId = "123";
        Long mentorId = 1L;
        Long menteeId = 2L;

        messageDTO.setRoomId(roomId);
        messageDTO.setSenderId(menteeId);

        message.setSenderId(menteeId);
        message.setRoomId(roomId);
        message.setReadByMentor(false);
        message.setReadByMentee(true);

        CoffeeLetterRoom room = CoffeeLetterRoom.builder()
                .id(roomId)
                .mentorId(mentorId)
                .menteeId(menteeId)
                .unreadCountMentor(0)
                .unreadCountMentee(0)
                .build();
        
        try (MockedStatic<RoomUtils> mockedRoomUtils = Mockito.mockStatic(RoomUtils.class)) {
            mockedRoomUtils.when(() -> RoomUtils.getRoomById(roomRepository, roomId)).thenReturn(room);
            
            when(messageRepository.save(any(ChatMessage.class))).thenReturn(message);
            when(roomRepository.save(any(CoffeeLetterRoom.class))).thenReturn(room);
            
            // When
            ChatMessageDTO result = messageService.sendMessage(messageDTO);
            
            // Then
            assertThat(result).isNotNull();
            verify(messageRepository, times(1)).save(any(ChatMessage.class));
            verify(roomRepository, times(1)).save(any(CoffeeLetterRoom.class));
            verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/chat/room/" + roomId), any(Object.class));
            
            // RoomUtils.getRoomById가 한 번 호출되었는지 확인
            mockedRoomUtils.verify(() -> RoomUtils.getRoomById(roomRepository, roomId), times(1));
        }
    }
    
    @Test
    @DisplayName("시스템 메시지 전송 성공 테스트")
    void sendSystemMessageTest() {
        // Given
        String roomId = "test-room-id";
        String content = "시스템 메시지 테스트";

        try (MockedStatic<RoomUtils> mockedRoomUtils = Mockito.mockStatic(RoomUtils.class)) {
            mockedRoomUtils.when(() -> RoomUtils.getRoomById(roomRepository, roomId)).thenReturn(room);
            
            when(messageRepository.save(any(ChatMessage.class))).thenReturn(message);
            
            // When
            ChatMessageDTO result = messageService.sendSystemMessage(roomId, content);
            
            // Then
            assertThat(result).isNotNull();
            verify(messageRepository, times(1)).save(any(ChatMessage.class));
            verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/chat/room/" + roomId), any(Object.class));
            
            // RoomUtils.getRoomById가 한 번 호출되었는지 확인
            mockedRoomUtils.verify(() -> RoomUtils.getRoomById(roomRepository, roomId), times(1));
        }
    }
    
    @Test
    @DisplayName("존재하지 않는 채팅방에 시스템 메시지 전송 시 예외 발생 테스트")
    void sendSystemMessageToNonExistingRoomTest() {
        // Given
        String nonExistingRoomId = "non-existing-room-id";
        String content = "시스템 메시지 테스트";

        try (MockedStatic<RoomUtils> mockedRoomUtils = Mockito.mockStatic(RoomUtils.class)) {
            mockedRoomUtils.when(() -> RoomUtils.getRoomById(roomRepository, nonExistingRoomId))
                    .thenThrow(new IllegalArgumentException("채팅방을 찾을 수 없습니다."));
            
            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                messageService.sendSystemMessage(nonExistingRoomId, content);
            });
            
            verify(messageRepository, never()).save(any(ChatMessage.class));
            verify(messagingTemplate, never()).convertAndSend(anyString(), any(Object.class));
        }
    }
    
    @Test
    @DisplayName("채팅방 메시지 목록 조회 성공 테스트")
    void getMessagesByRoomIdTest() {
        // Given
        String roomId = "test-room-id";

        try (MockedStatic<RoomUtils> mockedRoomUtils = Mockito.mockStatic(RoomUtils.class)) {
            mockedRoomUtils.when(() -> RoomUtils.getRoomById(roomRepository, roomId)).thenReturn(room);
            
            when(messageRepository.findByRoomIdOrderByTimestampAsc(roomId)).thenReturn(messages);
            
            // When
            List<ChatMessageDTO> result = messageService.getMessagesByRoomId(roomId);
            
            // Then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            verify(messageRepository, times(1)).findByRoomIdOrderByTimestampAsc(roomId);
            
            // RoomUtils.getRoomById가 한 번 호출되었는지 확인
            mockedRoomUtils.verify(() -> RoomUtils.getRoomById(roomRepository, roomId), times(1));
        }
    }
    
    @Test
    @DisplayName("메시지가 없는 채팅방 메시지 목록 조회 시 빈 목록 반환 테스트")
    void getMessagesByRoomIdEmptyTest() {
        // Given
        String roomId = "empty-room-id";

        try (MockedStatic<RoomUtils> mockedRoomUtils = Mockito.mockStatic(RoomUtils.class)) {
            mockedRoomUtils.when(() -> RoomUtils.getRoomById(roomRepository, roomId)).thenReturn(room);
            
            when(messageRepository.findByRoomIdOrderByTimestampAsc(roomId)).thenReturn(Collections.emptyList());
            
            // When
            List<ChatMessageDTO> result = messageService.getMessagesByRoomId(roomId);
            
            // Then
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
            verify(messageRepository, times(1)).findByRoomIdOrderByTimestampAsc(roomId);
            
            // RoomUtils.getRoomById가 한 번 호출되었는지 확인
            mockedRoomUtils.verify(() -> RoomUtils.getRoomById(roomRepository, roomId), times(1));
        }
    }
    
    @Test
    @DisplayName("존재하지 않는 채팅방 메시지 목록 조회 시 예외 발생 테스트")
    void getMessagesByRoomIdWithNonExistingRoomTest() {
        // Given
        String nonExistingRoomId = "non-existing-room-id";

        try (MockedStatic<RoomUtils> mockedRoomUtils = Mockito.mockStatic(RoomUtils.class)) {
            mockedRoomUtils.when(() -> RoomUtils.getRoomById(roomRepository, nonExistingRoomId))
                    .thenThrow(new IllegalArgumentException("채팅방을 찾을 수 없습니다."));
            
            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                messageService.getMessagesByRoomId(nonExistingRoomId);
            });
            
            verify(messageRepository, never()).findByRoomIdOrderByTimestampAsc(anyString());
        }
    }
    
    @Test
    @DisplayName("채팅방 메시지 페이징 조회 성공 테스트")
    void getPagedMessagesByRoomIdTest() {
        // Given
        String roomId = "test-room-id";
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));

        try (MockedStatic<RoomUtils> mockedRoomUtils = Mockito.mockStatic(RoomUtils.class)) {
            mockedRoomUtils.when(() -> RoomUtils.getRoomById(roomRepository, roomId)).thenReturn(room);
            
            when(messageRepository.findByRoomId(roomId, pageable)).thenReturn(messagePage);
            
            // When
            Page<ChatMessageDTO> result = messageService.getMessagesByRoomId(roomId, pageable);
            
            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            verify(messageRepository, times(1)).findByRoomId(roomId, pageable);
            
            // RoomUtils.getRoomById가 한 번 호출되었는지 확인
            mockedRoomUtils.verify(() -> RoomUtils.getRoomById(roomRepository, roomId), times(1));
        }
    }
    
    @Test
    @DisplayName("멘토가 읽지 않은 메시지 조회 성공 테스트")
    void getUnreadMessagesByMentorTest() {
        // Given
        String roomId = "test-room-id";
        Long mentorId = 1L; // 멘토 ID

        try (MockedStatic<RoomUtils> mockedRoomUtils = Mockito.mockStatic(RoomUtils.class)) {
            mockedRoomUtils.when(() -> RoomUtils.getRoomById(roomRepository, roomId)).thenReturn(room);
            mockedRoomUtils.when(() -> RoomUtils.isParticipant(room, mentorId)).thenReturn(true);
            
            when(messageRepository.findByRoomIdAndReadByMentorFalseOrderByTimestampAsc(roomId)).thenReturn(messages);
            
            // When
            List<ChatMessageDTO> result = messageService.getUnreadMessages(roomId, mentorId);
            
            // Then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            verify(messageRepository, times(1)).findByRoomIdAndReadByMentorFalseOrderByTimestampAsc(roomId);
            
            // RoomUtils 메소드 호출 확인
            mockedRoomUtils.verify(() -> RoomUtils.getRoomById(roomRepository, roomId), times(1));
            mockedRoomUtils.verify(() -> RoomUtils.isParticipant(room, mentorId), times(1));
        }
    }
    
    @Test
    @DisplayName("멘티가 읽지 않은 메시지 조회 성공 테스트")
    void getUnreadMessagesByMenteeTest() {
        // Given
        String roomId = "test-room-id";
        Long menteeId = 2L; // 멘티 ID

        try (MockedStatic<RoomUtils> mockedRoomUtils = Mockito.mockStatic(RoomUtils.class)) {
            mockedRoomUtils.when(() -> RoomUtils.getRoomById(roomRepository, roomId)).thenReturn(room);
            mockedRoomUtils.when(() -> RoomUtils.isParticipant(room, menteeId)).thenReturn(true);
            
            when(messageRepository.findByRoomIdAndReadByMenteeFalseOrderByTimestampAsc(roomId)).thenReturn(messages);
            
            // When
            List<ChatMessageDTO> result = messageService.getUnreadMessages(roomId, menteeId);
            
            // Then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            verify(messageRepository, times(1)).findByRoomIdAndReadByMenteeFalseOrderByTimestampAsc(roomId);
            
            // RoomUtils 메소드 호출 확인
            mockedRoomUtils.verify(() -> RoomUtils.getRoomById(roomRepository, roomId), times(1));
            mockedRoomUtils.verify(() -> RoomUtils.isParticipant(room, menteeId), times(1));
        }
    }
    
    @Test
    @DisplayName("멘토의 메시지 읽음 처리 성공 테스트")
    void markAsReadByMentorTest() {
        // Given
        String roomId = "test-room-id";
        Long mentorId = 1L; // 멘토 ID

        try (MockedStatic<RoomUtils> mockedRoomUtils = Mockito.mockStatic(RoomUtils.class)) {
            mockedRoomUtils.when(() -> RoomUtils.getRoomById(roomRepository, roomId)).thenReturn(room);
            mockedRoomUtils.when(() -> RoomUtils.isParticipant(room, mentorId)).thenReturn(true);
            
            when(messageRepository.updateReadByMentorByRoomId(roomId)).thenReturn(5);
            when(roomRepository.save(any(CoffeeLetterRoom.class))).thenReturn(room);
            
            // When
            messageService.markAsRead(roomId, mentorId);
            
            // Then
            verify(messageRepository, times(1)).updateReadByMentorByRoomId(roomId);
            verify(roomRepository, times(1)).save(any(CoffeeLetterRoom.class));
            
            // RoomUtils 메소드 호출 확인
            mockedRoomUtils.verify(() -> RoomUtils.getRoomById(roomRepository, roomId), times(1));
            mockedRoomUtils.verify(() -> RoomUtils.isParticipant(room, mentorId), times(1));
        }
    }
    
    @Test
    @DisplayName("멘티의 메시지 읽음 처리 성공 테스트")
    void markAsReadByMenteeTest() {
        // Given
        String roomId = "test-room-id";
        Long menteeId = 2L; // 멘티 ID

        try (MockedStatic<RoomUtils> mockedRoomUtils = Mockito.mockStatic(RoomUtils.class)) {
            mockedRoomUtils.when(() -> RoomUtils.getRoomById(roomRepository, roomId)).thenReturn(room);
            mockedRoomUtils.when(() -> RoomUtils.isParticipant(room, menteeId)).thenReturn(true);
            
            when(messageRepository.updateReadByMenteeByRoomId(roomId)).thenReturn(5);
            when(roomRepository.save(any(CoffeeLetterRoom.class))).thenReturn(room);
            
            // When
            messageService.markAsRead(roomId, menteeId);
            
            // Then
            verify(messageRepository, times(1)).updateReadByMenteeByRoomId(roomId);
            verify(roomRepository, times(1)).save(any(CoffeeLetterRoom.class));
            
            // RoomUtils 메소드 호출 확인
            mockedRoomUtils.verify(() -> RoomUtils.getRoomById(roomRepository, roomId), times(1));
            mockedRoomUtils.verify(() -> RoomUtils.isParticipant(room, menteeId), times(1));
        }
    }
    
    @Test
    @DisplayName("멘토의 읽지 않은 메시지 수 조회 성공 테스트")
    void getUnreadMessageCountByMentorTest() {
        // Given
        String roomId = "test-room-id";
        Long mentorId = 1L; // 멘토 ID
        int expectedCount = 5;

        try (MockedStatic<RoomUtils> mockedRoomUtils = Mockito.mockStatic(RoomUtils.class)) {
            mockedRoomUtils.when(() -> RoomUtils.getRoomById(roomRepository, roomId)).thenReturn(room);
            mockedRoomUtils.when(() -> RoomUtils.isParticipant(room, mentorId)).thenReturn(true);
            
            when(messageRepository.countByRoomIdAndReadByMentorFalse(roomId)).thenReturn(expectedCount);
            
            // When
            int result = messageService.getUnreadMessageCount(roomId, mentorId);
            
            // Then
            assertThat(result).isEqualTo(expectedCount);
            verify(messageRepository, times(1)).countByRoomIdAndReadByMentorFalse(roomId);
            
            // RoomUtils 메소드 호출 확인
            mockedRoomUtils.verify(() -> RoomUtils.getRoomById(roomRepository, roomId), times(1));
            mockedRoomUtils.verify(() -> RoomUtils.isParticipant(room, mentorId), times(1));
        }
    }
    
    @Test
    @DisplayName("멘티의 읽지 않은 메시지 수 조회 성공 테스트")
    void getUnreadMessageCountByMenteeTest() {
        // Given
        String roomId = "test-room-id";
        Long menteeId = 2L; // 멘티 ID
        int expectedCount = 3;

        try (MockedStatic<RoomUtils> mockedRoomUtils = Mockito.mockStatic(RoomUtils.class)) {
            mockedRoomUtils.when(() -> RoomUtils.getRoomById(roomRepository, roomId)).thenReturn(room);
            mockedRoomUtils.when(() -> RoomUtils.isParticipant(room, menteeId)).thenReturn(true);
            
            when(messageRepository.countByRoomIdAndReadByMenteeFalse(roomId)).thenReturn(expectedCount);
            
            // When
            int result = messageService.getUnreadMessageCount(roomId, menteeId);
            
            // Then
            assertThat(result).isEqualTo(expectedCount);
            verify(messageRepository, times(1)).countByRoomIdAndReadByMenteeFalse(roomId);
            
            // RoomUtils 메소드 호출 확인
            mockedRoomUtils.verify(() -> RoomUtils.getRoomById(roomRepository, roomId), times(1));
            mockedRoomUtils.verify(() -> RoomUtils.isParticipant(room, menteeId), times(1));
        }
    }
    
    @Test
    @DisplayName("마지막 메시지 조회 성공 테스트")
    void getLastMessageTest() {
        // Given
        String roomId = "test-room-id";
        
        when(messageRepository.findTopByRoomIdOrderByTimestampDesc(roomId)).thenReturn(message);
        
        // When
        ChatMessageDTO result = messageService.getLastMessage(roomId);
        
        // Then
        assertThat(result).isNotNull();
        verify(messageRepository, times(1)).findTopByRoomIdOrderByTimestampDesc(roomId);
    }
    
    @Test
    @DisplayName("메시지가 없는 채팅방의 마지막 메시지 조회 테스트")
    void getLastMessageEmptyTest() {
        // Given
        String roomId = "empty-room-id";
        
        when(messageRepository.findTopByRoomIdOrderByTimestampDesc(roomId)).thenReturn(null);
        
        // When
        ChatMessageDTO result = messageService.getLastMessage(roomId);
        
        // Then
        assertThat(result).isNull();
        verify(messageRepository, times(1)).findTopByRoomIdOrderByTimestampDesc(roomId);
    }
} 