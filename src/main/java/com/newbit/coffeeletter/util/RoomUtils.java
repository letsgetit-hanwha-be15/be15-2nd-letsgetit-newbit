package com.newbit.coffeeletter.util;

import com.newbit.coffeeletter.domain.chat.CoffeeLetterRoom;
import com.newbit.coffeeletter.repository.CoffeeLetterRoomRepository;

/**
 * 채팅방 관련 유틸리티 클래스
 */
public class RoomUtils {
    
    /**
     * 채팅방 ID로 채팅방을 조회합니다.
     * 존재하지 않는 경우 예외를 발생시킵니다.
     * 
     * @param repository 채팅방 저장소
     * @param roomId 채팅방 ID
     * @return 조회된 채팅방
     * @throws IllegalArgumentException 채팅방이 존재하지 않는 경우
     */
    public static CoffeeLetterRoom getRoomById(CoffeeLetterRoomRepository repository, String roomId) {
        return repository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다: " + roomId));
    }
    
    /**
     * 사용자가 채팅방의 참여자인지 확인합니다.
     * 
     * @param room 채팅방
     * @param userId 사용자 ID
     * @return 참여자 여부
     */
    public static boolean isParticipant(CoffeeLetterRoom room, Long userId) {
        return userId.equals(room.getMentorId()) || userId.equals(room.getMenteeId());
    }
    
    /**
     * 사용자가 채팅방의 참여자인지 확인하고, 참여자가 아닌 경우 예외를 발생시킵니다.
     * 
     * @param room 채팅방
     * @param userId 사용자 ID
     * @throws IllegalArgumentException 참여자가 아닌 경우
     */
    public static void validateParticipant(CoffeeLetterRoom room, Long userId) {
        if (!isParticipant(room, userId)) {
            throw new IllegalArgumentException("해당 사용자는 이 채팅방에 참여하지 않았습니다: " + userId);
        }
    }
    
    /**
     * 커피챗 ID로 채팅방을 조회합니다.
     * 
     * @param repository 채팅방 저장소
     * @param coffeeChatId 커피챗 ID
     * @return 조회된 채팅방 (존재하지 않는 경우 null)
     */
    public static CoffeeLetterRoom findRoomByCoffeeChatId(CoffeeLetterRoomRepository repository, Long coffeeChatId) {
        return repository.findByCoffeeChatId(coffeeChatId).orElse(null);
    }
} 