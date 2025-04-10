package com.newbit.coffeechat.command.application.service;

import com.newbit.coffeechat.command.application.dto.request.RequestTimeDto;
import com.newbit.coffeechat.command.domain.aggregate.RequestTime;
import com.newbit.coffeechat.command.domain.repository.CoffeechatRepository;
import com.newbit.coffeechat.command.application.dto.request.CoffeechatCreateRequest;
import com.newbit.coffeechat.command.domain.aggregate.Coffeechat;
import com.newbit.coffeechat.command.domain.repository.RequestTimeRepository;
import com.newbit.coffeechat.query.dto.request.CoffeechatSearchRequest;
import com.newbit.coffeechat.query.dto.response.CoffeechatDto;
import com.newbit.coffeechat.query.dto.response.CoffeechatListResponse;
import com.newbit.coffeechat.query.service.CoffeechatQueryService;
import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
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
    @Mock
    private RequestTimeRepository requestTimeRepository;

    @DisplayName("커피챗 요청 등록 성공")
    @Test
    void createCoffeechat_성공() {
        // given
        Long userId = 8L;
        LocalDateTime pastStartDateTime = LocalDateTime.now().plusDays(1);
        LocalDateTime pastEndDateTime = pastStartDateTime.plusHours(1);
        RequestTimeDto requestTimeDto = new RequestTimeDto();
        ReflectionTestUtils.setField(requestTimeDto, "startDateTime", pastStartDateTime);
        ReflectionTestUtils.setField(requestTimeDto, "endDateTime", pastEndDateTime);

        CoffeechatCreateRequest request = new CoffeechatCreateRequest(
                "취업 관련 꿀팁 얻고 싶어요.",
                2,
                2L,
                List.of(requestTimeDto));

        Coffeechat mockCoffeechat = Coffeechat.of(userId,
                request.getMentorId(),
                request.getRequestMessage(),
                request.getPurchaseQuantity());

        // private 필드인 coffeechatId에 직접 주입
        ReflectionTestUtils.setField(mockCoffeechat, "coffeechatId", 999L);
        when(coffeechatRepository.save(any(Coffeechat.class))).thenReturn(mockCoffeechat);

        // coffeechatQueryService.getCoffeechats 메서드 요청 시, 빈 리스트 반환
        CoffeechatListResponse coffeechatListResponse = CoffeechatListResponse.builder()
                .coffeechats(new LinkedList<>()).build();
        when(coffeechatQueryService.getCoffeechats(any(CoffeechatSearchRequest.class))).thenReturn(coffeechatListResponse);

        // requestTimeRepository::save 시 requestTime 반환
        RequestTime requestTime = RequestTime.of(
                requestTimeDto.getStartDateTime().toLocalDate(),
                requestTimeDto.getStartDateTime(),
                requestTimeDto.getEndDateTime(),
                999L);
        when(requestTimeRepository.save(any(RequestTime.class))).thenReturn(requestTime);

        // when
        Long result = coffeechatCommandService.createCoffeechat(userId, request);

        // then
        assertNotNull(result);
        assertEquals(999L, result);
        verify(coffeechatRepository).save(any(Coffeechat.class));

    }

    @DisplayName("이미 진행중인 커피챗이 존재할 시 에러 반환")
    @Test
    void createCoffeechat_진행중인_커피챗이_존재() {
        // given
        Long userId = 8L;
        LocalDateTime pastStartDateTime = LocalDateTime.now().plusDays(1);
        LocalDateTime pastEndDateTime = pastStartDateTime.plusHours(1);
        RequestTimeDto invalidTimeDto = new RequestTimeDto();
        ReflectionTestUtils.setField(invalidTimeDto, "startDateTime", pastStartDateTime);
        ReflectionTestUtils.setField(invalidTimeDto, "endDateTime", pastEndDateTime);

        CoffeechatCreateRequest request = new CoffeechatCreateRequest(
                "취업 관련 꿀팁 얻고 싶어요.",
                2,
                2L,
                List.of(invalidTimeDto));

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

    @DisplayName("현재보다 이전 날짜 & 시간이 start time으로 들어오면 에러 반환")
    @Test
    void createCoffeechat_현재보다_이전이면_에러() {
        // given
        Long userId = 8L;
        LocalDateTime pastStartDateTime = LocalDateTime.now().minusDays(1);
        LocalDateTime pastEndDateTime = pastStartDateTime.plusHours(1);
        RequestTimeDto invalidTimeDto = new RequestTimeDto();
        ReflectionTestUtils.setField(invalidTimeDto, "startDateTime", pastStartDateTime);
        ReflectionTestUtils.setField(invalidTimeDto, "endDateTime", pastEndDateTime);

        CoffeechatCreateRequest request = new CoffeechatCreateRequest(
                "취업 관련 꿀팁 얻고 싶어요.",
                2,
                2L,
                List.of(invalidTimeDto));

        Coffeechat mockCoffeechat = Coffeechat.of(userId,
                request.getMentorId(),
                request.getRequestMessage(),
                request.getPurchaseQuantity());

        // private 필드인 coffeechatId에 직접 주입
        ReflectionTestUtils.setField(mockCoffeechat, "coffeechatId", 999L);
        when(coffeechatRepository.save(any(Coffeechat.class))).thenReturn(mockCoffeechat);

        // coffeechatQueryService.getCoffeechats 메서드 요청 시, 빈 리스트 반환
        CoffeechatListResponse coffeechatListResponse = CoffeechatListResponse.builder()
                .coffeechats(new LinkedList<>()).build();
        when(coffeechatQueryService.getCoffeechats(any(CoffeechatSearchRequest.class))).thenReturn(coffeechatListResponse);

        // when & then
        assertThrows(
                BusinessException.class,
                () -> coffeechatCommandService.createCoffeechat(userId, request)
        );
    }

    @DisplayName("시작 날짜와 끝 날짜가 다르면 INVALID_REQUEST_DATE 예외 발생")
    @Test
    void createCoffeechat_startdate와_enddate가_다른경우() {
        // given
        Long userId = 8L;
        LocalDateTime pastStartDateTime = LocalDateTime.now().plusDays(1);
        LocalDateTime pastEndDateTime = pastStartDateTime.plusDays(1);
        RequestTimeDto invalidTimeDto = new RequestTimeDto();
        ReflectionTestUtils.setField(invalidTimeDto, "startDateTime", pastStartDateTime);
        ReflectionTestUtils.setField(invalidTimeDto, "endDateTime", pastEndDateTime);

        CoffeechatCreateRequest request = new CoffeechatCreateRequest(
                "취업 관련 꿀팁 얻고 싶어요.",
                2,
                2L,
                List.of(invalidTimeDto));

        Coffeechat mockCoffeechat = Coffeechat.of(userId,
                request.getMentorId(),
                request.getRequestMessage(),
                request.getPurchaseQuantity());

        // private 필드인 coffeechatId에 직접 주입
        ReflectionTestUtils.setField(mockCoffeechat, "coffeechatId", 999L);
        when(coffeechatRepository.save(any(Coffeechat.class))).thenReturn(mockCoffeechat);

        // coffeechatQueryService.getCoffeechats 메서드 요청 시, 빈 리스트 반환
        CoffeechatListResponse coffeechatListResponse = CoffeechatListResponse.builder()
                .coffeechats(new LinkedList<>()).build();
        when(coffeechatQueryService.getCoffeechats(any(CoffeechatSearchRequest.class))).thenReturn(coffeechatListResponse);


        // when
        Throwable thrown = catchThrowable(() -> coffeechatCommandService.createCoffeechat(userId, request));

        // then
        assertThat(thrown).isInstanceOf(BusinessException.class);
        BusinessException businessException = (BusinessException) thrown;

        // 커스텀 예외의 ErrorCode가 INVALID_REQUEST_DATE 인지 확인
        assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.INVALID_REQUEST_DATE);
        // 메시지를 함께 검증하고 싶다면
        assertThat(businessException.getMessage()).contains("시작 날짜와 끝 날짜가 다릅니다.");

    }

    @DisplayName("시작 시간과 끝 시간이 구매 수량 × 30분보다 작으면 INVALID_REQUEST_TIME 예외 발생")
    @Test
    void createCoffeechat_시간부족_에러() {
        // given
        Long userId = 8L;
        LocalDateTime pastStartDateTime = LocalDateTime.of(2025, 5, 1, 10, 0);
        LocalDateTime pastEndDateTime = LocalDateTime.of(2025, 5, 1, 10, 30);
        RequestTimeDto invalidTimeDto = new RequestTimeDto();
        ReflectionTestUtils.setField(invalidTimeDto, "startDateTime", pastStartDateTime);
        ReflectionTestUtils.setField(invalidTimeDto, "endDateTime", pastEndDateTime);

        CoffeechatCreateRequest request = new CoffeechatCreateRequest(
                "취업 관련 꿀팁 얻고 싶어요.",
                2,
                2L,
                List.of(invalidTimeDto));


        Coffeechat mockCoffeechat = Coffeechat.of(userId,
                request.getMentorId(),
                request.getRequestMessage(),
                request.getPurchaseQuantity());

        // private 필드인 coffeechatId에 직접 주입
        ReflectionTestUtils.setField(mockCoffeechat, "coffeechatId", 999L);
        when(coffeechatRepository.save(any(Coffeechat.class))).thenReturn(mockCoffeechat);

        // coffeechatQueryService.getCoffeechats 메서드 요청 시, 빈 리스트 반환
        CoffeechatListResponse coffeechatListResponse = CoffeechatListResponse.builder()
                .coffeechats(new LinkedList<>()).build();
        when(coffeechatQueryService.getCoffeechats(any(CoffeechatSearchRequest.class))).thenReturn(coffeechatListResponse);


        // when
        Throwable thrown = catchThrowable(() -> coffeechatCommandService.createCoffeechat(userId, request));

        // then
        assertThat(thrown).isInstanceOf(BusinessException.class);
        BusinessException businessException = (BusinessException) thrown;

        // 커스텀 예외의 ErrorCode가 INVALID_REQUEST_DATE 인지 확인
        assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.INVALID_REQUEST_TIME);
        // 메시지를 함께 검증하고 싶다면
        assertThat(businessException.getMessage()).contains("시작 시간과 끝 시간 구매 수량 x 30분 보다 작습니다.");

    }
}