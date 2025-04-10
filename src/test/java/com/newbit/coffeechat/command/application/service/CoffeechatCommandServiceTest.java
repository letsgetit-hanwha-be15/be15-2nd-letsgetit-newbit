package com.newbit.coffeechat.command.application.service;

import com.newbit.coffeechat.command.application.domain.repository.CoffeechatRepository;
import com.newbit.coffeechat.command.application.dto.request.CoffeechatCreateRequest;
import com.newbit.coffeechat.command.domain.aggregate.Coffeechat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoffeechatCommandServiceTest {

    @InjectMocks
    private CoffeechatCommandService coffeechatCommandService;

    @Mock
    private CoffeechatRepository coffeechatRepository;

    @DisplayName("1-1. 커피챗 요청 등록 : 성공")
    @Test
    void createCoffeechat_성공() {
        // given
        Long userId = 8L;
        CoffeechatCreateRequest request = new CoffeechatCreateRequest("취업 관련 꿀팁 얻고 싶어요.", 2, 2L);

        Coffeechat mockCoffeechat = Coffeechat.of(userId,
                request.getMentorId(),
                request.getRequestMessage(),
                request.getPurchaseQuantity());

        // private 필드인 coffeechatId에 직접 주입
        ReflectionTestUtils.setField(mockCoffeechat, "coffeechatId", 10L);

        when(coffeechatRepository.save(any(Coffeechat.class))).thenReturn(mockCoffeechat);

        // when
        Long result = coffeechatCommandService.createCoffeechat(userId, request);

        // then
        assertNotNull(result);
        assertEquals(10L, result);
        verify(coffeechatRepository).save(any(Coffeechat.class));

    }



}