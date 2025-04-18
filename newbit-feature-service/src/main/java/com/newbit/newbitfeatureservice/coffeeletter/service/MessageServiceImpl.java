package com.newbit.newbitfeatureservice.coffeeletter.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
import com.newbit.newbitfeatureservice.notification.command.application.dto.request.NotificationSendRequest;
import com.newbit.newbitfeatureservice.notification.command.application.service.NotificationCommandService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    private final ChatMessageRepository messageRepository;
    private final CoffeeLetterRoomRepository roomRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ModelMapper modelMapper;
    private final NotificationCommandService notificationCommandService;
    private final MentorFeignClient mentorFeignClient;

    public MessageServiceImpl(
            ChatMessageRepository messageRepository,
            CoffeeLetterRoomRepository roomRepository,
            SimpMessagingTemplate messagingTemplate,
            ModelMapper modelMapper,
            NotificationCommandService notificationCommandService, 
            MentorFeignClient mentorFeignClient) {
        this.messageRepository = messageRepository;
        this.roomRepository = roomRepository;
        this.messagingTemplate = messagingTemplate;
        this.modelMapper = modelMapper;
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

        Long receiverId = message.getSenderId().equals(room.getMentorId())
                ? room.getMenteeId()
                : mentorFeignClient.getUserIdByMentorId(room.getMentorId()).getData();

        notificationCommandService.sendNotification(
                new NotificationSendRequest(
                        receiverId,
                        8L,
                        room.getCoffeeChatId(),
                        message.getContent()
                )
        );

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
        RoomUtils.isParticipant(room, userId);
        
        int updatedCount = 0;
        
        if (userId.equals(room.getMentorId())) {
            updatedCount = messageRepository.updateReadByMentorByRoomId(roomId);
            room.setUnreadCountMentor(0);
        } else {
            updatedCount = messageRepository.updateReadByMenteeByRoomId(roomId);
            room.setUnreadCountMentee(0);
        }

        roomRepository.save(room);
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
} 