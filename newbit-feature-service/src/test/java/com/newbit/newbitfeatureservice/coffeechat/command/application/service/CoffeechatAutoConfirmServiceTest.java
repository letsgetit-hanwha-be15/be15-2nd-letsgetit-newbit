package com.newbit.newbitfeatureservice.coffeechat.command.application.service;

import com.newbit.newbitfeatureservice.coffeechat.query.mapper.CoffeechatMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CoffeechatAutoConfirmServiceTest {

    @Mock
    private CoffeechatMapper coffeechatMapper;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CoffeechatAutoConfirmService coffeechatAutoConfirmService;

    @BeforeEach
    public void setUp() {
        // 테스트 환경에서 confirmBaseUrl 값을 강제로 설정 (기본값: http://localhost:8080/api/v1/coffeechats/confirm-purchase)
        ReflectionTestUtils.setField(
                coffeechatAutoConfirmService,
                "confirmBaseUrl",
                "http://localhost:8080/api/v1/coffeechats/confirm-purchase"
        );
    }

    @Test
    @DisplayName("정상 케이스: 단일 coffeechatId에 대해 구매확정 API 호출 테스트")
    public void testAutoConfirmCoffeechats() {
        // given
        Long coffeechatId = 1L;
        LocalDate targetDate = LocalDate.now().minusDays(7);

        List<Long> coffeechatIdList = Collections.singletonList(coffeechatId);

        when(coffeechatMapper.selectCoffeechatIdByEndDate(targetDate))
                .thenReturn(coffeechatIdList);

        // then
        coffeechatAutoConfirmService.autoConfirmCoffeechats();

        String expectedUrl = "http://localhost:8080/api/v1/coffeechats/confirm-purchase/" + coffeechatId;
        verify(restTemplate).put(expectedUrl, null);
    }

    @Test
    @DisplayName("예외 처리 케이스: 첫번째 API 호출 예외 후 두번째 API 정상 호출 테스트")
    public void testAutoConfirmCoffeechatsWithException() {
        // 예외가 상위로 전파되지 않고, 이후 호출이 정상적으로 실행되는지 확인
        // given
        Long coffeechatId1 = 1L;
        Long coffeechatId2 = 2L;
        LocalDate targetDate = LocalDate.now().minusDays(7);
        List<Long> coffeechatIdList = List.of(coffeechatId1, coffeechatId2);

        when(coffeechatMapper.selectCoffeechatIdByEndDate(targetDate))
                .thenReturn(coffeechatIdList);

        // 첫 번째 커피챗 ID 호출 시, 예외 발생하도록 설정
        String expectedUrl1 = "http://localhost:8080/api/v1/coffeechats/confirm-purchase/" + coffeechatId1;
        org.mockito.Mockito.doThrow(new RuntimeException("API 호출 예외"))
                .when(restTemplate).put(expectedUrl1, null);
        String expectedUrl2 = "http://localhost:8080/api/v1/coffeechats/confirm-purchase/" + coffeechatId2;

        // when
        coffeechatAutoConfirmService.autoConfirmCoffeechats();

        // then: 첫 번째 호출과 두 번째 호출 모두 verify
        verify(restTemplate).put(expectedUrl1, null);
        verify(restTemplate).put(expectedUrl2, null);
    }
}