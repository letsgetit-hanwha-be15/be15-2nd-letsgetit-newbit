package com.newbit.newbitfeatureservice.coffeeletter.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import com.newbit.newbitfeatureservice.coffeeletter.domain.chat.ChatMessage;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    // 특정 채팅방의 모든 메시지 조회
    List<ChatMessage> findByRoomId(String roomId);
    
    // 특정 채팅방의 모든 메시지 타임스탬프 오름차순 조회
    List<ChatMessage> findByRoomIdOrderByTimestampAsc(String roomId);

    // 특정 채팅방의 메시지 페이징 조회 todo 페이징 필요한 지 확인 필요
    Page<ChatMessage> findByRoomId(String roomId, Pageable pageable);

    // 특정 채팅방의 멘토가 읽지 않은 메시지 조회
    List<ChatMessage> findByRoomIdAndReadByMentorFalse(String roomId);
    
    // 특정 채팅방의 멘토가 읽지 않은 메시지 타임스탬프 오름차순 조회
    List<ChatMessage> findByRoomIdAndReadByMentorFalseOrderByTimestampAsc(String roomId);

    // 특정 채팅방의 멘티가 읽지 않은 메시지 조회
    List<ChatMessage> findByRoomIdAndReadByMenteeFalse(String roomId);
    
    // 특정 채팅방의 멘티가 읽지 않은 메시지 타임스탬프 오름차순 조회
    List<ChatMessage> findByRoomIdAndReadByMenteeFalseOrderByTimestampAsc(String roomId);

    // 특정 채팅방의 마지막 메시지 조회
    ChatMessage findFirstByRoomIdOrderByTimestampDesc(String roomId);
    
    // 특정 채팅방의 최신 메시지 조회 (위와 같은 기능)
    ChatMessage findTopByRoomIdOrderByTimestampDesc(String roomId);
    
    // 특정 채팅방의 멘토 읽음 상태 업데이트
    @Query(value = "{'roomId': ?0, 'readByMentor': false}")
    @Update("{'$set': {'readByMentor': true}}")
    int updateReadByMentorByRoomId(String roomId);
    
    // 특정 채팅방의 멘티 읽음 상태 업데이트
    @Query(value = "{'roomId': ?0, 'readByMentee': false}")
    @Update("{'$set': {'readByMentee': true}}")
    int updateReadByMenteeByRoomId(String roomId);
    
    // 특정 채팅방의 멘토가 읽지 않은 메시지 수 조회
    int countByRoomIdAndReadByMentorFalse(String roomId);
    
    // 특정 채팅방의 멘티가 읽지 않은 메시지 수 조회
    int countByRoomIdAndReadByMenteeFalse(String roomId);
} 