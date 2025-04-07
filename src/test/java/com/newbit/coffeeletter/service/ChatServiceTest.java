package com.newbit.coffeeletter.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.newbit.coffeeletter.domain.chat.CoffeeLetterRoom;
import com.newbit.coffeeletter.dto.CoffeeLetterRoomDTO;
import com.newbit.coffeeletter.repository.CoffeeLetterRoomRepository;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private CoffeeLetterRoomRepository roomRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ChatServiceImpl chatService;

    private CoffeeLetterRoomDTO roomDTO;
    private CoffeeLetterRoom room;
    private List<CoffeeLetterRoom> rooms;

    @BeforeEach
    void setUp() {
        roomDTO = new CoffeeLetterRoomDTO();
        roomDTO.setMentorId(1L);
        roomDTO.setMenteeId(2L);
        roomDTO.setMentorName("멘토");
        roomDTO.setMenteeName("멘티");
        roomDTO.setCoffeeChatId(100L);

        room = new CoffeeLetterRoom();
        room.setId("test-room-id");
        room.setMentorId(1L);
        room.setMenteeId(2L);
        room.setMentorName("멘토");
        room.setMenteeName("멘티");
        room.setCoffeeChatId(100L);
        room.setStatus(CoffeeLetterRoom.RoomStatus.ACTIVE);
        room.getParticipants().add("1");
        room.getParticipants().add("2");

        rooms = Arrays.asList(room);
    }

    @Test
    void createRoom_createRoomTest() {
        // given
        when(roomRepository.findByCoffeeChatId(anyLong()))
                .thenReturn(Optional.empty());
        when(modelMapper.map(roomDTO, CoffeeLetterRoom.class)).thenReturn(room);
        when(roomRepository.save(any(CoffeeLetterRoom.class))).thenReturn(room);
        when(modelMapper.map(room, CoffeeLetterRoomDTO.class)).thenReturn(roomDTO);

        // when
        CoffeeLetterRoomDTO result = chatService.createRoom(roomDTO);

        // then
        assertNotNull(result);
        assertEquals(roomDTO.getMentorId(), result.getMentorId());
        assertEquals(roomDTO.getMenteeId(), result.getMenteeId());
        verify(roomRepository, times(1)).save(any(CoffeeLetterRoom.class));
    }

    @Test
    void createRoom_duplicatedTest() {
        // given
        when(roomRepository.findByCoffeeChatId(anyLong()))
                .thenReturn(Optional.of(room));

        // when & then
        assertThrows(IllegalStateException.class, () -> {
            chatService.createRoom(roomDTO);
        });
        verify(roomRepository, never()).save(any(CoffeeLetterRoom.class));
    }

} 