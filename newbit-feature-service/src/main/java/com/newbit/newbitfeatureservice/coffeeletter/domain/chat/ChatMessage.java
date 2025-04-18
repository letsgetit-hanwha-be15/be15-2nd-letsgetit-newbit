package com.newbit.newbitfeatureservice.coffeeletter.domain.chat;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "coffeelettermessages")
public class ChatMessage {
    
    @Id
    private String id;
    
    private String roomId;
    
    private Long senderId;
    private String senderName;
    
    private String content;
    
    @Builder.Default
    private MessageType type = MessageType.CHAT;
    
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    private boolean readByMentor;
    private boolean readByMentee;
} 