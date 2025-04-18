package com.newbit.newbitfeatureservice.coffeeletter.scheduler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.newbit.newbitfeatureservice.coffeeletter.domain.chat.CoffeeLetterRoom;
import com.newbit.newbitfeatureservice.coffeeletter.domain.chat.CoffeeLetterRoom.RoomStatus;
import com.newbit.newbitfeatureservice.coffeeletter.dto.CoffeeLetterRoomDTO;
import com.newbit.newbitfeatureservice.coffeeletter.repository.CoffeeLetterRoomRepository;
import com.newbit.newbitfeatureservice.coffeeletter.service.ChatService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@EnableScheduling
public class ChatRoomScheduler {

    private final CoffeeLetterRoomRepository roomRepository;
    private final ChatService chatService;
    
    public ChatRoomScheduler(CoffeeLetterRoomRepository roomRepository, ChatService chatService) {
        this.roomRepository = roomRepository;
        this.chatService = chatService;
    }

    @Scheduled(fixedRate = 300000)
    public void closeExpiredRooms() {
        LocalDateTime now = LocalDateTime.now();
        
        List<CoffeeLetterRoom> expiredRooms = roomRepository.findByStatus(RoomStatus.ACTIVE)
                .stream()
                .filter(room -> room.getEndTime() != null && room.getEndTime().isBefore(now))
                .collect(Collectors.toList());
        
        log.info("Closing {} expired chat rooms", expiredRooms.size());
        
        for (CoffeeLetterRoom room : expiredRooms) {
            log.info("Closing room: {}", room.getId());
            try {
                CoffeeLetterRoomDTO inactiveRoom = chatService.endRoom(room.getId());
                log.info("Room closed successfully: {}", inactiveRoom.getId());
            } catch (Exception e) {
                log.error("Error closing room {}: {}", room.getId(), e.getMessage());
            }
        }
    }
} 