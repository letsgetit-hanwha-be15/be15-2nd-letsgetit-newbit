package com.newbit.newbitfeatureservice.coffeeletter.dto;

import com.newbit.newbitfeatureservice.coffeeletter.domain.chat.MessageType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "채팅 메시지 정보")
public class ChatMessageDTO {
    @Schema(description = "메시지 ID")
    private String id;
    
    @Schema(description = "채팅방 ID")
    private String roomId;
    
    @Schema(description = "발신자 ID")
    private Long senderId;
    
    @Schema(description = "발신자 이름")
    private String senderName;
    
    @Schema(description = "메시지 내용")
    private String content;
    
    @Schema(description = "메시지 타입(CHAT, SYSTEM, ENTER, LEAVE)")
    private MessageType type;
    
    @Schema(description = "메시지 전송 시간")
    private LocalDateTime timestamp;
    
    @Schema(description = "멘토 읽음 여부")
    private boolean readByMentor;
    
    @Schema(description = "멘티 읽음 여부")
    private boolean readByMentee;
} 