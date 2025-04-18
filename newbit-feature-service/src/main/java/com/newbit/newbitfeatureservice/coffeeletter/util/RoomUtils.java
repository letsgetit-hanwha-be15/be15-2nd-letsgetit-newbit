package com.newbit.newbitfeatureservice.coffeeletter.util;

import com.newbit.newbitfeatureservice.coffeeletter.domain.chat.CoffeeLetterRoom;
import com.newbit.newbitfeatureservice.coffeeletter.repository.CoffeeLetterRoomRepository;
import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;

public class RoomUtils {
    

    public static CoffeeLetterRoom getRoomById(CoffeeLetterRoomRepository repository, String roomId) {
        return repository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COFFEELETTER_ROOM_NOT_FOUND));
    }

    public static boolean isParticipant(CoffeeLetterRoom room, Long userId) {
        return userId.equals(room.getMentorId()) || userId.equals(room.getMenteeId());
    }

    public static void validateParticipant(CoffeeLetterRoom room, Long userId) {
        if (!isParticipant(room, userId)) {
            throw new BusinessException(ErrorCode.COFFEELETTER_INVALID_ACCESS);
        }
    }

    public static CoffeeLetterRoom findRoomByCoffeeChatId(CoffeeLetterRoomRepository repository, Long coffeeChatId) {
        return repository.findByCoffeeChatId(coffeeChatId).orElse(null);
    }

    public static CoffeeLetterRoom getRoomByCoffeeChatId(CoffeeLetterRoomRepository repository, Long coffeeChatId) {
        return repository.findByCoffeeChatId(coffeeChatId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COFFEELETTER_NOT_FOUND));
    }
} 