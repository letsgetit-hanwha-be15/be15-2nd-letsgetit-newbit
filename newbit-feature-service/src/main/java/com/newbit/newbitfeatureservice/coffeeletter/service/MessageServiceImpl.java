package com.newbit.newbitfeatureservice.coffeeletter.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.newbit.newbitfeatureservice.coffeeletter.dto.CoffeeLetterRoomDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newbit.newbitfeatureservice.client.user.MentorFeignClient;
import com.newbit.newbitfeatureservice.coffeeletter.domain.chat.ChatMessage;
import com.newbit.newbitfeatureservice.coffeeletter.domain.chat.CoffeeLetterRoom;
import com.newbit.newbitfeatureservice.coffeeletter.domain.chat.MessageType;
import com.newbit.newbitfeatureservice.coffeeletter.dto.ChatMessageDTO;
import com.newbit.newbitfeatureservice.coffeeletter.repository.ChatMessageRepository;
import com.newbit.newbitfeatureservice.coffeeletter.repository.CoffeeLetterRoomRepository;
import com.newbit.newbitfeatureservice.coffeeletter.util.RoomUtils;
import com.newbit.newbitfeatureservice.common.dto.ApiResponse;
import com.newbit.newbitfeatureservice.notification.command.application.dto.request.NotificationSendRequest;
import com.newbit.newbitfeatureservice.notification.command.application.service.NotificationCommandService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    private final ChatMessageRepository messageRepository;
    private final CoffeeLetterRoomRepository roomRepository;
    private final ModelMapper modelMapper;
    private final SimpMessageSendingOperations messagingTemplate;
    private final NotificationCommandService notificationCommandService;
    private final MentorFeignClient mentorFeignClient;

    @Autowired
    public MessageServiceImpl(
            ChatMessageRepository messageRepository,
            CoffeeLetterRoomRepository roomRepository,
            ModelMapper modelMapper,
            SimpMessageSendingOperations messagingTemplate,
            NotificationCommandService notificationCommandService,
            MentorFeignClient mentorFeignClient) {
        this.messageRepository = messageRepository;
        this.roomRepository = roomRepository;
        this.modelMapper = modelMapper;
        this.messagingTemplate = messagingTemplate;
        this.notificationCommandService = notificationCommandService;
        this.mentorFeignClient = mentorFeignClient;
    }

    @Override
    @Transactional
    public ChatMessageDTO sendMessage(ChatMessageDTO messageDto) {
        ChatMessage message = modelMapper.map(messageDto, ChatMessage.class);
        message.setTimestamp(LocalDateTime.now());

        CoffeeLetterRoom room = RoomUtils.getRoomById(roomRepository, message.getRoomId());

        if (message.getSenderId().equals(room.getMentorId())) {
            message.setReadByMentor(true);
        }
        else if (message.getSenderId().equals(room.getMenteeId())) {
            message.setReadByMentee(true);
        }

        ChatMessage savedMessage = messageRepository.save(message);
        ChatMessageDTO savedMessageDto = modelMapper.map(savedMessage, ChatMessageDTO.class);

        room.setLastMessageContent(message.getContent());
        room.setLastMessageTime(message.getTimestamp());
        room.setLastMessageType(message.getType());
        room.setLastMessageSenderId(message.getSenderId());

        if (!message.isReadByMentor()) {
            room.setUnreadCountMentor(room.getUnreadCountMentor() + 1);
        }
        if (!message.isReadByMentee()) {
            room.setUnreadCountMentee(room.getUnreadCountMentee() + 1);
        }

        roomRepository.save(room);

        messagingTemplate.convertAndSend("/topic/chat/room/" + message.getRoomId(), savedMessageDto);

        try {
            Long receiverId;
            if (message.getSenderId().equals(room.getMentorId())) {
                receiverId = room.getMenteeId();
            } else if (message.getSenderId().equals(room.getMenteeId())) {
                receiverId = room.getMentorId();
                
                if (mentorFeignClient != null) {
                    try {
                        ApiResponse<Long> response = mentorFeignClient.getUserIdByMentorId(room.getMentorId());
                        if (response != null && response.getData() != null) {
                            receiverId = response.getData();
                        }
                    } catch (Exception e) {
                        log.error("멘토 ID로 사용자 ID 조회 중 오류 발생: {}", e.getMessage());
                    }
                }
            } else {
                log.warn("알 수 없는 발신자 ID: {}, 알림 발송을 건너뜁니다.", message.getSenderId());
                return savedMessageDto;
            }
            
            notificationCommandService.sendNotification(
                    new NotificationSendRequest(
                            receiverId,
                            8L,
                            room.getCoffeeChatId(),
                            message.getContent()
                    )
            );
        } catch (Exception e) {
            log.error("알림 발송 중 오류 발생: {}", e.getMessage());
        }

        return savedMessageDto;
    }

    @Override
    @Transactional
    public ChatMessageDTO sendSystemMessage(String roomId, String content) {
        CoffeeLetterRoom room = RoomUtils.getRoomById(roomRepository, roomId);

        ChatMessage systemMessage = ChatMessage.builder()
                .roomId(roomId)
                .type(MessageType.SYSTEM)
                .content(content)
                .senderId(0L)
                .senderName("System")
                .timestamp(LocalDateTime.now())
                .readByMentor(false)
                .readByMentee(false)
                .build();

        ChatMessage savedMessage = messageRepository.save(systemMessage);
        ChatMessageDTO savedMessageDto = modelMapper.map(savedMessage, ChatMessageDTO.class);

        room.setLastMessageContent(systemMessage.getContent());
        room.setLastMessageTime(systemMessage.getTimestamp());
        room.setLastMessageType(systemMessage.getType());
        room.setLastMessageSenderId(systemMessage.getSenderId());

        if (!systemMessage.isReadByMentor()) {
            room.setUnreadCountMentor(room.getUnreadCountMentor() + 1);
        }
        if (!systemMessage.isReadByMentee()) {
            room.setUnreadCountMentee(room.getUnreadCountMentee() + 1);
        }

        roomRepository.save(room);

        messagingTemplate.convertAndSend("/topic/chat/room/" + roomId, savedMessageDto);

        return savedMessageDto;
    }

    @Override
    public List<ChatMessageDTO> getMessagesByRoomId(String roomId) {
        RoomUtils.getRoomById(roomRepository, roomId);
                
        return messageRepository.findByRoomIdOrderByTimestampAsc(roomId).stream()
                .map(message -> modelMapper.map(message, ChatMessageDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public Page<ChatMessageDTO> getMessagesByRoomId(String roomId, Pageable pageable) {
        RoomUtils.getRoomById(roomRepository, roomId);
                
        return messageRepository.findByRoomId(roomId, pageable)
                .map(message -> modelMapper.map(message, ChatMessageDTO.class));
    }

    @Override
    public List<ChatMessageDTO> getUnreadMessages(String roomId, Long userId) {
        CoffeeLetterRoom room = RoomUtils.getRoomById(roomRepository, roomId);
        RoomUtils.isParticipant(room, userId);

        List<ChatMessage> unreadMessages;

        if (userId.equals(room.getMentorId())) {
            unreadMessages = messageRepository.findByRoomIdAndReadByMentorFalseOrderByTimestampAsc(roomId);
        } else {
            unreadMessages = messageRepository.findByRoomIdAndReadByMenteeFalseOrderByTimestampAsc(roomId);
        }

        return unreadMessages.stream()
                .map(message -> modelMapper.map(message, ChatMessageDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(String roomId, Long userId) {
        CoffeeLetterRoom room = RoomUtils.getRoomById(roomRepository, roomId);
        List<ChatMessage> unreadMessages = getUnreadMessageEntities(roomId, userId);

        boolean updated = false;
        for (ChatMessage message : unreadMessages) {
            if (room.getMentorId().equals(userId)) {
                if (!message.isReadByMentor()) { // 이미 true이면 업데이트 안 함
                    message.setReadByMentor(true);
                    updated = true;
                }
            } else if (room.getMenteeId().equals(userId)) {
                if (!message.isReadByMentee()) {
                    message.setReadByMentee(true);
                    updated = true;
                }
            }
        }

        if (updated) {
            if (room.getMentorId().equals(userId)) {
                room.setUnreadCountMentor(0);
            } else {
                room.setUnreadCountMentee(0);
            }
            CoffeeLetterRoom updatedRoom = roomRepository.save(room); // 변경된 room 저장

            messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/events",
                createReadStatusEvent(roomId, userId)
            );

            messagingTemplate.convertAndSend(
                "/topic/chat/" + roomId,
                createReadStatusBroadcast(roomId, userId)
            );

            updateRoomListForUsers(updatedRoom);
        }
    }
    
    private Object createReadStatusEvent(String roomId, Long userId) {
        return new ReadStatusDTO(roomId, userId, true);
    }
    
    private Object createReadStatusBroadcast(String roomId, Long userId) {
        ChatMessageDTO message = new ChatMessageDTO();
        message.setType(MessageType.READ_RECEIPT);
        message.setRoomId(roomId);
        message.setSenderId(userId);
        message.setTimestamp(LocalDateTime.now());
        message.setContent("메시지를 읽었습니다");
        return message;
    }
    
    private void updateRoomListForUsers(CoffeeLetterRoom room) {
        CoffeeLetterRoomDTO roomDto = modelMapper.map(room, CoffeeLetterRoomDTO.class);
        // DTO 객체를 전송
        messagingTemplate.convertAndSend("/topic/rooms", roomDto);
    }

    @Override
    public int getUnreadMessageCount(String roomId, Long userId) {
        CoffeeLetterRoom room = RoomUtils.getRoomById(roomRepository, roomId);
        RoomUtils.isParticipant(room, userId);

        if (userId.equals(room.getMentorId())) {
            return messageRepository.countByRoomIdAndReadByMentorFalse(roomId);
        } else {
            return messageRepository.countByRoomIdAndReadByMenteeFalse(roomId);
        }
    }

    @Override
    public ChatMessageDTO getLastMessage(String roomId) {
        ChatMessage lastMessage = messageRepository.findTopByRoomIdOrderByTimestampDesc(roomId);
        if (lastMessage == null) {
            return null;
        }
        return modelMapper.map(lastMessage, ChatMessageDTO.class);
    }

    private List<ChatMessage> getUnreadMessageEntities(String roomId, Long userId) {
        CoffeeLetterRoom room = RoomUtils.getRoomById(roomRepository, roomId);
        RoomUtils.isParticipant(room, userId);

        List<ChatMessage> unreadMessages;

        if (userId.equals(room.getMentorId())) {
            unreadMessages = messageRepository.findByRoomIdAndReadByMentorFalseOrderByTimestampAsc(roomId);
        } else {
            unreadMessages = messageRepository.findByRoomIdAndReadByMenteeFalseOrderByTimestampAsc(roomId);
        }

        return unreadMessages;
    }

    private static class ReadStatusDTO {
        private String roomId;
        private Long userId;
        private boolean read;
        
        public ReadStatusDTO(String roomId, Long userId, boolean read) {
            this.roomId = roomId;
            this.userId = userId;
            this.read = read;
        }

        public String getRoomId() {
            return roomId;
        }

        public Long getUserId() {
            return userId;
        }

        public boolean isRead() {
            return read;
        }
    }
} 