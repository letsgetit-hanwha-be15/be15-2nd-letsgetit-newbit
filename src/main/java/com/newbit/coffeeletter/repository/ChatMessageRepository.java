package com.newbit.coffeeletter.repository;

import com.newbit.coffeeletter.domain.chat.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    // 특정 채팅방의 모든 메시지 조회
    List<ChatMessage> findByRoomId(String roomId);

    // 특정 채팅방의 메시지 페이징 조회 todo 페이징 필요한 지 확인 필요
    Page<ChatMessage> findByRoomId(String roomId, Pageable pageable);

} 