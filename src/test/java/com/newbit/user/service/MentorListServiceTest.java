package com.newbit.user.service;

import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.user.dto.request.MentorListRequestDTO;
import com.newbit.user.dto.response.MentorListResponseDTO;
import com.newbit.user.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MentorListServiceTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserQueryService userQueryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getMentors_정상_조회() {
        // given
        MentorListRequestDTO request = new MentorListRequestDTO();
        MentorListResponseDTO mentor = new MentorListResponseDTO();
        mentor.setMentorId(1L);
        mentor.setNickname("테스트 멘토");

        when(userMapper.findMentors(request)).thenReturn(List.of(mentor));

        // when
        List<MentorListResponseDTO> result = userQueryService.getMentors(request);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("테스트 멘토", result.get(0).getNickname());
        verify(userMapper, times(1)).findMentors(request);
    }

    @Test
    void getMentors_멘토_없음_예외() {
        // given
        MentorListRequestDTO request = new MentorListRequestDTO();
        when(userMapper.findMentors(request)).thenReturn(Collections.emptyList());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () ->
                userQueryService.getMentors(request));

        assertEquals(ErrorCode.MENTOR_NOT_FOUND, exception.getErrorCode());
        verify(userMapper, times(1)).findMentors(request);
    }
}
