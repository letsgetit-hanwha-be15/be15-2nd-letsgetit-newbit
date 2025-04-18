package com.newbit.newbitfeatureservice.coffeeletter.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.newbit.newbitfeatureservice.coffeeletter.domain.chat.CoffeeLetterRoom;

public interface CoffeeLetterRoomRepository extends MongoRepository<CoffeeLetterRoom, String> {

    // 특정 사용자가 참여한 모든 채팅방 조회
    List<CoffeeLetterRoom> findByParticipantsContaining(String userId);

    // 특정 사용자가 참여한 특정 상태의 모든 채팅방 조회
    List<CoffeeLetterRoom> findByParticipantsContainingAndStatus(String userId, CoffeeLetterRoom.RoomStatus status);

    // 특정 상태의 모든 채팅방 조회
    List<CoffeeLetterRoom> findByStatus(CoffeeLetterRoom.RoomStatus status);

    // 커피챗 주문 ID로 채팅방 조회
    Optional<CoffeeLetterRoom> findByCoffeeChatId(Long coffeeChatId);

    // 멘토 ID로 채팅방 조회 todo 프론트 연결 후 삭제 여부 결정
    List<CoffeeLetterRoom> findByMentorId(Long mentorId);

    // 멘티 ID로 채팅방 조회 todo 프론트 연결 후 삭제 여부 결정
    List<CoffeeLetterRoom> findByMenteeId(Long menteeId);

    // 특정 멘토의 특정 상태 채팅방 조회 todo 프론트 연결 후 삭제 여부 결정
    List<CoffeeLetterRoom> findByMentorIdAndStatus(Long mentorId, CoffeeLetterRoom.RoomStatus status);

    // 특정 멘티의 특정 상태 채팅방 조회 todo 프론트 연결 후 삭제 여부 결정
    List<CoffeeLetterRoom> findByMenteeIdAndStatus(Long menteeId, CoffeeLetterRoom.RoomStatus status);

    // 페이징 처리된 채팅방 조회 todo 테스트 코드임 개발 완료 후 삭제 여부 결정
    Page<CoffeeLetterRoom> findAll(Pageable pageable);

    List<CoffeeLetterRoom> findByMentorIdOrMenteeId(Long mentorId, Long menteeId);
    List<CoffeeLetterRoom> findByMentorIdOrMenteeIdAndStatus(Long mentorId, Long menteeId, CoffeeLetterRoom.RoomStatus status);

}