package com.newbit.coffeeletter.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.newbit.coffeeletter.domain.chat.CoffeeLetterRoom;

public interface CoffeeLetterRoomRepository extends MongoRepository<CoffeeLetterRoom, String> {
    Optional<CoffeeLetterRoom> findByCoffeeChatId(Long coffeeChatId);
} 