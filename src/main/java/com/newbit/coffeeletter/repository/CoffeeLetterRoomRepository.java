package com.newbit.coffeeletter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.newbit.coffeeletter.domain.chat.CoffeeLetterRoom;

@Repository
public interface CoffeeLetterRoomRepository extends MongoRepository<CoffeeLetterRoom, String> {
} 