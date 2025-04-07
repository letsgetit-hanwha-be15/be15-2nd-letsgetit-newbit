package com.newbit.coffeeletter.dto;

import com.newbit.coffeeletter.domain.chat.CoffeeLetterRoom;
import com.newbit.coffeeletter.domain.chat.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoffeeLetterRoomDTO {
    private String id;
    private Long coffeeChatId;
    
    private Long mentorId;
    private String mentorName;
    
    private Long menteeId;
    private String menteeName;
    
    private LocalDateTime createdAt;
    private LocalDateTime endTime;
    
    private CoffeeLetterRoom.RoomStatus status;
    
    @Builder.Default
    private List<String> participants = new ArrayList<>();
    
    private int unreadCountMentor;
    private int unreadCountMentee;
    
    private String lastMessageContent;
    private LocalDateTime lastMessageTime;
    private MessageType lastMessageType;
    private Long lastMessageSenderId;
} 