package com.newbit.newbitfeatureservice.coffeeletter.dto;

import com.newbit.newbitfeatureservice.coffeeletter.domain.chat.CoffeeLetterRoom;
import com.newbit.newbitfeatureservice.coffeeletter.domain.chat.MessageType;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "채팅방 정보")
public class CoffeeLetterRoomDTO {
    @Schema(description = "채팅방 ID")
    private String id;
    
    @Schema(description = "커피챗 ID")
    private Long coffeeChatId;
    
    @Schema(description = "멘토 ID")
    private Long mentorId;
    
    @Schema(description = "멘토 이름")
    private String mentorName;
    
    @Schema(description = "멘티 ID")
    private Long menteeId;
    
    @Schema(description = "멘티 이름")
    private String menteeName;
    
    @Schema(description = "채팅방 생성 시간")
    private LocalDateTime createdAt;
    
    @Schema(description = "채팅방 종료 시간")
    private LocalDateTime endTime;
    
    @Schema(description = "채팅방 상태 (ACTIVE, INACTIVE, CANCELED)")
    private CoffeeLetterRoom.RoomStatus status;
    
    @Schema(description = "참여자 ID 목록")
    @Builder.Default
    private List<String> participants = new ArrayList<>();
    
    @Schema(description = "멘토 읽지 않은 메시지 수")
    private int unreadCountMentor;
    
    @Schema(description = "멘티 읽지 않은 메시지 수")
    private int unreadCountMentee;
    
    @Schema(description = "마지막 메시지 내용")
    private String lastMessageContent;
    
    @Schema(description = "마지막 메시지 시간")
    private LocalDateTime lastMessageTime;
    
    @Schema(description = "마지막 메시지 타입")
    private MessageType lastMessageType;
    
    @Schema(description = "마지막 메시지 발신자 ID")
    private Long lastMessageSenderId;
} 