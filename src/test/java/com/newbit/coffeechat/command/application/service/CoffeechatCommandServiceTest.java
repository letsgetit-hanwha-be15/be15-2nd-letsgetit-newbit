package com.newbit.coffeechat.command.application.service;

import com.newbit.coffeechat.command.application.domain.repository.CoffeechatRepository;
import com.newbit.coffeechat.command.application.dto.request.CoffeechatCreateRequest;
import com.newbit.coffeechat.command.domain.aggregate.Coffeechat;
import com.newbit.coffeechat.query.dto.request.CoffeechatSearchRequest;
import com.newbit.coffeechat.query.dto.response.CoffeechatDto;
import com.newbit.coffeechat.query.dto.response.CoffeechatListResponse;
import com.newbit.coffeechat.query.service.CoffeechatQueryService;
import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoffeechatCommandServiceTest {

    @InjectMocks
    private CoffeechatCommandService coffeechatCommandService;

    @Mock
    private CoffeechatRepository coffeechatRepository;
    @Mock
    private CoffeechatQueryService coffeechatQueryService;

    @DisplayName("1-1. 커피챗 요청 등록 성공")
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

    @DisplayName("1-2. 이미 진행중인 커피챗이 존재할 시 에러 반환")
    @Test
    void createCoffeechat_진행중인_커피챗이_존재() {
        // given
        Long userId = 8L;
        CoffeechatCreateRequest request = new CoffeechatCreateRequest("취업 관련 꿀팁 얻고 싶어요.", 2, 2L);

        List<CoffeechatDto> coffeechatDtos = new ArrayList<>();
        coffeechatDtos.add(new CoffeechatDto());

        CoffeechatListResponse response = CoffeechatListResponse.builder()
                .coffeechats(coffeechatDtos).build(); // coffeechatQueryservice에서 같은 멘토와 멘티가 현재 진행중인 커피챗이 있을 때, 한 개 이상의 list 반환

        when(coffeechatQueryService.getCoffeechats(any(CoffeechatSearchRequest.class)))
                .thenReturn(response);

        // when & then
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> coffeechatCommandService.createCoffeechat(userId, request)
        );

        // 예외 타입 혹은 ErrorCode 등 원하는 검증 로직
        assertEquals(ErrorCode.COFFEECHAT_ALREADY_EXIST, exception.getErrorCode());

        // 예외가 발생했으므로 save는 절대 호출되지 않아야 함
        verify(coffeechatRepository, never()).save(any(Coffeechat.class));

    }

}