package com.newbit.coffeeletter.scheduler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.newbit.coffeeletter.domain.chat.CoffeeLetterRoom;
import com.newbit.coffeeletter.domain.chat.CoffeeLetterRoom.RoomStatus;
import com.newbit.coffeeletter.repository.CoffeeLetterRoomRepository;
import com.newbit.coffeeletter.service.ChatService;

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
    
    /**
     * 5분마다 실행되는 스케줄러
     * 종료 시간이 지난 활성 상태의 채팅방을 비활성화 처리
     */
    @Scheduled(fixedRate = 300000) // 5분 =300,000ms
    public void closeExpiredRooms() {
        LocalDateTime now = LocalDateTime.now();
        
        List<CoffeeLetterRoom> expiredRooms = roomRepository.findByStatus(RoomStatus.ACTIVE)
                .stream()
                .filter(room -> room.getEndTime() != null && room.getEndTime().isBefore(now))
                .collect(Collectors.toList());
        
        log.info("Closing {} expired chat rooms", expiredRooms.size());
        
        for (CoffeeLetterRoom room : expiredRooms) {
            log.info("Closing room: {}", room.getId());
            chatService.endRoom(room.getId());
        }
    }
} 