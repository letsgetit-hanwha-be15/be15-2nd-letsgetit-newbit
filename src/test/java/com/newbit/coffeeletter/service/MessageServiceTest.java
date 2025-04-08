package com.newbit.coffeeletter.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
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
import com.newbit.coffeeletter.repository.ChatMessageRepository;
import com.newbit.coffeeletter.repository.CoffeeLetterRoomRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MessageServiceTest {

    @Mock
    private ChatMessageRepository messageRepository;
    
    @Mock
    private CoffeeLetterRoomRepository roomRepository;
    
    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private ModelMapper modelMapper;

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
        ChatMessageDTO result = messageService.sendMessage(messageDTO);
        
        // then
        assertNotNull(result);
        verify(messageRepository, times(1)).save(any(ChatMessage.class));
        verify(roomRepository, times(2)).findById(roomId);
        verify(roomRepository, times(1)).save(any(CoffeeLetterRoom.class));
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/chat/room/" + roomId), any(Object.class));
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
        ChatMessageDTO result = messageService.sendMessage(messageDTO);
        
        // then
        assertNotNull(result);
        verify(messageRepository, times(1)).save(any(ChatMessage.class));
        verify(roomRepository, times(2)).findById(roomId);
        verify(roomRepository, times(1)).save(any(CoffeeLetterRoom.class));
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/chat/room/" + roomId), any(Object.class));
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
        ChatMessageDTO result = messageService.sendSystemMessage(roomId, content);
        
        // then
        assertNotNull(result);
        verify(roomRepository, times(2)).findById(roomId);
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
            messageService.sendSystemMessage(roomId, content);
        });
        
        verify(roomRepository, atLeastOnce()).findById(roomId);
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
        List<ChatMessageDTO> result = messageService.getMessagesByRoomId(roomId);
        
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
        List<ChatMessageDTO> result = messageService.getMessagesByRoomId(roomId);
        
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
            messageService.getMessagesByRoomId(roomId);
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
        Page<ChatMessageDTO> result = messageService.getMessagesByRoomId(roomId, pageable);
        
        // then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalElements());
        verify(roomRepository, times(1)).findById(roomId);
        verify(messageRepository, times(1)).findByRoomId(eq(roomId), any(Pageable.class));
    }
    
    @Test
    void getUnreadMessages_멘토의_읽지않은_메시지_조회_성공() {
        // given
        String roomId = "test-room-id";
        Long mentorId = 1L;
        
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(messageRepository.findByRoomIdAndReadByMentorFalse(roomId)).thenReturn(messages);
        
        // when
        List<ChatMessageDTO> results = messageService.getUnreadMessages(roomId, mentorId);
        
        // then
        assertNotNull(results);
        assertEquals(1, results.size());
        verify(roomRepository, times(1)).findById(roomId);
        verify(messageRepository, times(1)).findByRoomIdAndReadByMentorFalse(roomId);
        verify(messageRepository, never()).findByRoomIdAndReadByMenteeFalse(anyString());
    }
    
    @Test
    void getUnreadMessages_멘티의_읽지않은_메시지_조회_성공() {
        // given
        String roomId = "test-room-id";
        Long menteeId = 2L;
        
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(messageRepository.findByRoomIdAndReadByMenteeFalse(roomId)).thenReturn(messages);
        
        // when
        List<ChatMessageDTO> results = messageService.getUnreadMessages(roomId, menteeId);
        
        // then
        assertNotNull(results);
        assertEquals(1, results.size());
        verify(roomRepository, times(1)).findById(roomId);
        verify(messageRepository, never()).findByRoomIdAndReadByMentorFalse(anyString());
        verify(messageRepository, times(1)).findByRoomIdAndReadByMenteeFalse(roomId);
    }
    
    @Test
    void markAsRead_멘토_메시지_읽음_처리_성공() {
        // given
        String roomId = "test-room-id";
        Long mentorId = 1L;
        
        // 채팅방 설정
        CoffeeLetterRoom testRoom = new CoffeeLetterRoom();
        testRoom.setId(roomId);
        testRoom.setMentorId(mentorId);
        testRoom.setMenteeId(2L);
        testRoom.setUnreadCountMentor(3);
        
        // 읽지 않은 메시지 설정
        List<ChatMessage> unreadMessages = Arrays.asList(
            new ChatMessage(), new ChatMessage(), new ChatMessage()
        );
        
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(testRoom));
        when(messageRepository.findByRoomIdAndReadByMentorFalse(roomId)).thenReturn(unreadMessages);
        when(messageRepository.saveAll(unreadMessages)).thenReturn(unreadMessages);
        when(roomRepository.save(testRoom)).thenReturn(testRoom);
        
        // when
        messageService.markAsRead(roomId, mentorId);
        
        // then
        verify(roomRepository, times(1)).findById(roomId);
        verify(messageRepository, times(1)).findByRoomIdAndReadByMentorFalse(roomId);
        verify(messageRepository, times(1)).saveAll(unreadMessages);
        verify(roomRepository, times(1)).save(testRoom);
        
        // 읽지 않은 메시지 카운트가 0으로 초기화되었는지 확인
        assertEquals(0, testRoom.getUnreadCountMentor());
    }
    
    @Test
    void getUnreadMessageCount_멘토_카운트_조회_성공() {
        // given
        String roomId = "test-room-id";
        Long mentorId = 1L;
        int expectedCount = 3;
        
        CoffeeLetterRoom testRoom = new CoffeeLetterRoom();
        testRoom.setId(roomId);
        testRoom.setMentorId(mentorId);
        testRoom.setMenteeId(2L);
        testRoom.setUnreadCountMentor(expectedCount);
        
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(testRoom));
        
        // when
        int actualCount = messageService.getUnreadMessageCount(roomId, mentorId);
        
        // then
        assertEquals(expectedCount, actualCount);
        verify(roomRepository, times(1)).findById(roomId);
    }
    
    @Test
    void getLastMessage_성공적인_조회() {
        // given
        String roomId = "test-room-id";
        
        when(messageRepository.findFirstByRoomIdOrderByTimestampDesc(roomId)).thenReturn(message);
        
        // when
        ChatMessageDTO result = messageService.getLastMessage(roomId);
        
        // then
        assertNotNull(result);
        verify(messageRepository, times(1)).findFirstByRoomIdOrderByTimestampDesc(roomId);
    }
    
    @Test
    void getLastMessage_메시지가_없는_경우() {
        // given
        String roomId = "empty-room-id";
        when(messageRepository.findFirstByRoomIdOrderByTimestampDesc(roomId)).thenReturn(null);
        
        // when
        ChatMessageDTO result = messageService.getLastMessage(roomId);
        
        // then
        assertNull(result);
        verify(messageRepository, times(1)).findFirstByRoomIdOrderByTimestampDesc(roomId);
    }
} 