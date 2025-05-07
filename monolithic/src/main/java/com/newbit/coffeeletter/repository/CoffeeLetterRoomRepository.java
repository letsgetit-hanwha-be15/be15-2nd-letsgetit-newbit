package com.newbit.coffeeletter.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.newbit.coffeeletter.domain.chat.CoffeeLetterRoom;

public interface CoffeeLetterRoomRepository extends MongoRepository<CoffeeLetterRoom, String> {

    // 특정 사용자가 참여한 모든 채팅방 조회
    List<CoffeeLetterRoom> findByParticipantsContaining(String userId);

    // 특정 사용자가 참여한 특정 상태의 모든 채팅방 조회
    List<CoffeeLetterRoom> findByParticipantsContainingAndStatus(String userId, CoffeeLetterRoom.RoomStatus status);

    // 특정 상태의 모든 채팅방 조회
    List<CoffeeLetterRoom> findByStatus(CoffeeLetterRoom.RoomStatus status);

    // 커피챗 주문 ID로 채팅방 조회
    Optional<CoffeeLetterRoom> findByCoffeeChatId(Long coffeeChatId);

    List<CoffeeLetterRoom> findByMentorId(Long mentorId);

    List<CoffeeLetterRoom> findByMenteeId(Long menteeId);

    List<CoffeeLetterRoom> findByMentorIdAndStatus(Long mentorId, CoffeeLetterRoom.RoomStatus status);

    List<CoffeeLetterRoom> findByMenteeIdAndStatus(Long menteeId, CoffeeLetterRoom.RoomStatus status);

    Page<CoffeeLetterRoom> findAll(Pageable pageable);

    List<CoffeeLetterRoom> findByMentorIdOrMenteeId(Long mentorId, Long menteeId);
    List<CoffeeLetterRoom> findByMentorIdOrMenteeIdAndStatus(Long mentorId, Long menteeId, CoffeeLetterRoom.RoomStatus status);

}