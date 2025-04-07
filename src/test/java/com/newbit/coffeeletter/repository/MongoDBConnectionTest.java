package com.newbit.coffeeletter.repository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.newbit.coffeeletter.domain.chat.CoffeeLetterRoom;

@SpringBootTest
public class MongoDBConnectionTest {

    @Autowired
    private CoffeeLetterRoomRepository coffeeLetterRoomRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    void 몽고DB_연결_테스트() {
        // given
        // 기존 데이터 모두 삭제
        coffeeLetterRoomRepository.deleteAll();

        // 테스트용 데이터 생성
        CoffeeLetterRoom room = CoffeeLetterRoom.builder()
                .coffeeChatId(1L)
                .mentorId(100L)
                .mentorName("테스트멘토")
                .menteeId(200L)
                .menteeName("테스트멘티")
                .createdAt(LocalDateTime.now())
                .build();

        // when
        // 데이터 저장
        CoffeeLetterRoom savedRoom = coffeeLetterRoomRepository.save(room);

        // then
        // 저장된 데이터 확인
        assertThat(savedRoom).isNotNull();
        assertThat(savedRoom.getId()).isNotNull();
        
        // 저장된 데이터 조회
        List<CoffeeLetterRoom> rooms = coffeeLetterRoomRepository.findAll();
        assertThat(rooms).hasSize(1);
        assertThat(rooms.get(0).getMentorName()).isEqualTo("테스트멘토");
        assertThat(rooms.get(0).getMenteeName()).isEqualTo("테스트멘티");
        
        // 데이터 삭제
        coffeeLetterRoomRepository.deleteAll();
        List<CoffeeLetterRoom> emptyRooms = coffeeLetterRoomRepository.findAll();
        assertThat(emptyRooms).isEmpty();
    }
} 