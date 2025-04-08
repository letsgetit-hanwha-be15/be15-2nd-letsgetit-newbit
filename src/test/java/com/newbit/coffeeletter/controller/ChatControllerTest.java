package com.newbit.coffeeletter.controller;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newbit.coffeeletter.domain.chat.MessageType;
import com.newbit.coffeeletter.dto.ChatMessageDTO;
import com.newbit.coffeeletter.service.ChatService;

@WebMvcTest(ChatController.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ChatService chatService;

    @MockBean
    private SimpMessagingTemplate messagingTemplate;

    private ChatMessageDTO messageDTO;

    @BeforeEach
    void setUp() {
        messageDTO = new ChatMessageDTO();
        messageDTO.setId("test-message-id");
        messageDTO.setRoomId("test-room-id");
        messageDTO.setSenderId(1L);
        messageDTO.setSenderName("멘토");
        messageDTO.setContent("안녕하세요");
        messageDTO.setType(MessageType.CHAT);
        messageDTO.setTimestamp(LocalDateTime.now());
        messageDTO.setReadByMentor(true);
        messageDTO.setReadByMentee(false);
    }

    @Test
    void getLastMessage_성공() throws Exception {
        // given
        String roomId = "test-room-id";
        when(chatService.getLastMessage(roomId)).thenReturn(messageDTO);

        // when & then
        mockMvc.perform(get("/api/v1/coffeeletter/messages/{roomId}/last", roomId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(messageDTO.getId()))
                .andExpect(jsonPath("$.roomId").value(messageDTO.getRoomId()))
                .andExpect(jsonPath("$.senderId").value(messageDTO.getSenderId()))
                .andExpect(jsonPath("$.senderName").value(messageDTO.getSenderName()))
                .andExpect(jsonPath("$.content").value(messageDTO.getContent()))
                .andExpect(jsonPath("$.type").value(messageDTO.getType().toString()));

        verify(chatService, times(1)).getLastMessage(roomId);
    }

    @Test
    void getLastMessage_메시지가_없는_경우_null_반환() throws Exception {
        // given
        String roomId = "empty-room-id";
        when(chatService.getLastMessage(roomId)).thenReturn(null);

        // when & then
        mockMvc.perform(get("/api/v1/coffeeletter/messages/{roomId}/last", roomId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(chatService, times(1)).getLastMessage(roomId);
    }

    @Test
    void markAsRead_성공() throws Exception {
        // given
        String roomId = "test-room-id";
        Long userId = 1L;

        // when & then
        mockMvc.perform(post("/api/v1/coffeeletter/messages/{roomId}/mark-as-read/{userId}", roomId, userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(chatService, times(1)).markAsRead(roomId, userId);
    }
} 