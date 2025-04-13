package com.newbit.coffeeletter.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.newbit.notification.command.application.dto.request.NotificationSendRequest;
import com.newbit.notification.command.application.service.NotificationCommandService;
import com.newbit.user.service.MentorService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newbit.coffeeletter.domain.chat.ChatMessage;
import com.newbit.coffeeletter.domain.chat.CoffeeLetterRoom;
import com.newbit.coffeeletter.domain.chat.MessageType;
import com.newbit.coffeeletter.dto.ChatMessageDTO;
import com.newbit.coffeeletter.repository.ChatMessageRepository;
import com.newbit.coffeeletter.repository.CoffeeLetterRoomRepository;
import com.newbit.coffeeletter.util.RoomUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    private final ChatMessageRepository messageRepository;
    private final CoffeeLetterRoomRepository roomRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ModelMapper modelMapper;
    private final NotificationCommandService notificationCommandService;
    private final MentorService mentorService;

    public MessageServiceImpl(
            ChatMessageRepository messageRepository,
            CoffeeLetterRoomRepository roomRepository,
            SimpMessagingTemplate messagingTemplate,
            ModelMapper modelMapper,
            NotificationCommandService notificationCommandService, MentorService mentorService) {
        this.messageRepository = messageRepository;
        this.roomRepository = roomRepository;
        this.messagingTemplate = messagingTemplate;
        this.modelMapper = modelMapper;
        this.notificationCommandService = notificationCommandService;
        this.mentorService = mentorService;
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
                : mentorService.getUserIdByMentorId(room.getMentorId());

        notificationCommandService.sendNotification(
                new NotificationSendRequest(
                        receiverId,
                        8L,
                        Long.parseLong(room.getId()),
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
                
        return messageRepository.findByRoomId(roomId).stream()
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
        RoomUtils.validateParticipant(room, userId);

        List<ChatMessage> unreadMessages;

        if (userId.equals(room.getMentorId())) {
            unreadMessages = messageRepository.findByRoomIdAndReadByMentorFalse(roomId);
        } else {
            unreadMessages = messageRepository.findByRoomIdAndReadByMenteeFalse(roomId);
        }

        return unreadMessages.stream()
                .map(message -> modelMapper.map(message, ChatMessageDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(String roomId, Long userId) {
        CoffeeLetterRoom room = RoomUtils.getRoomById(roomRepository, roomId);
        RoomUtils.validateParticipant(room, userId);

        List<ChatMessage> unreadMessages;
        
        if (userId.equals(room.getMentorId())) {
            unreadMessages = messageRepository.findByRoomIdAndReadByMentorFalse(roomId);
            unreadMessages.forEach(message -> message.setReadByMentor(true));
            messageRepository.saveAll(unreadMessages);
            room.setUnreadCountMentor(0);
        } else {
            unreadMessages = messageRepository.findByRoomIdAndReadByMenteeFalse(roomId);
            unreadMessages.forEach(message -> message.setReadByMentee(true));
            messageRepository.saveAll(unreadMessages);
            room.setUnreadCountMentee(0);
        }

        roomRepository.save(room);
    }

    @Override
    public int getUnreadMessageCount(String roomId, Long userId) {
        CoffeeLetterRoom room = RoomUtils.getRoomById(roomRepository, roomId);
        RoomUtils.validateParticipant(room, userId);

        if (userId.equals(room.getMentorId())) {
            return room.getUnreadCountMentor();
        } else {
            return room.getUnreadCountMentee();
        }
    }

    @Override
    public ChatMessageDTO getLastMessage(String roomId) {
        ChatMessage lastMessage = messageRepository.findFirstByRoomIdOrderByTimestampDesc(roomId);
        if (lastMessage == null) {
            return null;
        }
        return modelMapper.map(lastMessage, ChatMessageDTO.class);
    }
} 