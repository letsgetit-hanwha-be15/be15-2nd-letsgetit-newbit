package com.newbit.newbitfeatureservice.coffeeletter.domain.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "coffeeletterrooms")
public class CoffeeLetterRoom {
    
    @Id
    private String id;
    
    private Long coffeeChatId;
    
    private Long mentorId;
    private String mentorName;
    
    private Long menteeId;
    private String menteeName;
    
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime endTime;
    
    @Builder.Default
    private RoomStatus status = RoomStatus.ACTIVE;
    
    @Builder.Default
    private List<String> participants = new ArrayList<>();
    
    @Builder.Default
    private int unreadCountMentor = 0;
    
    @Builder.Default
    private int unreadCountMentee = 0;
    
    private String lastMessageContent;
    private LocalDateTime lastMessageTime;
    private MessageType lastMessageType;
    private Long lastMessageSenderId;

    public enum RoomStatus {
        ACTIVE,
        INACTIVE,
        CANCELED
    }
}