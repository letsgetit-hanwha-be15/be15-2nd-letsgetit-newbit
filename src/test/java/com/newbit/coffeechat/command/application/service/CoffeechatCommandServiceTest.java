package com.newbit.coffeechat.command.application.service;

import com.newbit.coffeechat.command.domain.aggregate.RequestTime;
import com.newbit.coffeechat.command.domain.repository.CoffeechatRepository;
import com.newbit.coffeechat.command.application.dto.request.CoffeechatCreateRequest;
import com.newbit.coffeechat.command.domain.aggregate.Coffeechat;
import com.newbit.coffeechat.command.domain.repository.RequestTimeRepository;
import com.newbit.coffeechat.query.dto.request.CoffeechatSearchServiceRequest;
import com.newbit.coffeechat.query.dto.response.CoffeechatDto;
import com.newbit.coffeechat.query.dto.response.CoffeechatListResponse;
import com.newbit.coffeechat.query.service.CoffeechatQueryService;
import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.purchase.command.application.service.SaleCommandService;
import com.newbit.user.dto.response.MentorDTO;
import com.newbit.user.service.MentorService;
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
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
    @Mock
    private MentorService mentorService;
    @Mock
    private SaleCommandService saleCommandService;

    @DisplayName("커피챗 요청 등록 성공")
    @Test
    void createCoffeechat_성공() {
        // given
        Long userId = 8L;
        LocalDateTime pastStartDateTime = LocalDateTime.now().plusDays(1);

        CoffeechatCreateRequest request = new CoffeechatCreateRequest(
                "취업 관련 꿀팁 얻고 싶어요.",
                2,
                2L,
                List.of(pastStartDateTime));

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
        when(coffeechatQueryService.getCoffeechats(any(CoffeechatSearchServiceRequest.class))).thenReturn(coffeechatListResponse);

        // requestTimeRepository::save 시 requestTime 반환
        RequestTime requestTime = RequestTime.of(
                pastStartDateTime.toLocalDate(),
                pastStartDateTime,
                pastStartDateTime.plusMinutes(60L),
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

        CoffeechatCreateRequest request = new CoffeechatCreateRequest(
                "취업 관련 꿀팁 얻고 싶어요.",
                2,
                2L,
                List.of(pastStartDateTime));

        List<CoffeechatDto> coffeechatDtos = new ArrayList<>();
        coffeechatDtos.add(new CoffeechatDto());

        CoffeechatListResponse response = CoffeechatListResponse.builder()
                .coffeechats(coffeechatDtos).build(); // coffeechatQueryservice에서 같은 멘토와 멘티가 현재 진행중인 커피챗이 있을 때, 한 개 이상의 list 반환

        when(coffeechatQueryService.getCoffeechats(any(CoffeechatSearchServiceRequest.class)))
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

        CoffeechatCreateRequest request = new CoffeechatCreateRequest(
                "취업 관련 꿀팁 얻고 싶어요.",
                2,
                2L,
                List.of(pastStartDateTime));

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
        when(coffeechatQueryService.getCoffeechats(any(CoffeechatSearchServiceRequest.class))).thenReturn(coffeechatListResponse);

        // when & then
        assertThrows(
                BusinessException.class,
                () -> coffeechatCommandService.createCoffeechat(userId, request)
        );
    }

    @DisplayName("커피챗 승인 성공")
    @Test
    void acceptCoffeechatTime_성공() {
        // given
        Long requestTimeId = 1L;
        Long coffeechatId = 999L;

        // requestTime 객체 만들어주기
        LocalDateTime start = LocalDateTime.of(2025, 5, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2025, 5, 1, 11, 30);
        RequestTime requestTime = RequestTime.of(start.toLocalDate(), start, end, coffeechatId);

        // 커피챗 객체 만들어주기
        Coffeechat mockCoffeechat = Coffeechat.of(12L,
                2L,
                "취업 관련 꿀팁 얻고 싶어요.",
                2);

        // repo setting
        when(requestTimeRepository.findById(requestTimeId)).thenReturn(Optional.of(requestTime));
        when(coffeechatRepository.findById(coffeechatId)).thenReturn(Optional.of(mockCoffeechat));

        // when & then: 예외가 발생하지 않으면 테스트 통과
        assertDoesNotThrow(() -> coffeechatCommandService.acceptCoffeechatTime(requestTimeId));
    }

    @DisplayName("requsetTime 객체를 찾을 수 없습니다")
    @Test
    void acceptCoffeechatTime_requsetTime_없음() {
        // given
        Long requestTimeId = 1L;
        Long coffeechatId = 999L;

        // repo setting
        when(requestTimeRepository.findById(requestTimeId)).thenReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> coffeechatCommandService.acceptCoffeechatTime(requestTimeId));
        // 에러 코드가 REQUEST_TIME_NOT_FOUND 인지 검증
        assertEquals(ErrorCode.REQUEST_TIME_NOT_FOUND, exception.getErrorCode());
    }

    @DisplayName("coffeechat 객체를 찾을 수 없습니다")
    @Test
    void acceptCoffeechatTime_coffeechat_없음() {
        // given
        Long requestTimeId = 1L;
        Long coffeechatId = 999L;

        // requestTime 객체 만들어주기
        LocalDateTime start = LocalDateTime.of(2025, 5, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2025, 5, 1, 11, 30);
        RequestTime requestTime = RequestTime.of(start.toLocalDate(), start, end, coffeechatId);

        // repo setting
        when(requestTimeRepository.findById(requestTimeId)).thenReturn(Optional.of(requestTime));
        when(coffeechatRepository.findById(coffeechatId)).thenReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> coffeechatCommandService.acceptCoffeechatTime(requestTimeId));
        // 에러 코드가 REQUEST_TIME_NOT_FOUND 인지 검증
        assertEquals(ErrorCode.COFFEECHAT_NOT_FOUND, exception.getErrorCode());

    }

    @DisplayName("커피챗 거절 성공")
    @Test
    void rejectCoffeechatTime_성공() {
        // given
        Long coffeechatId = 999L;
        Long requestTimeId = 1L;

        // requestTime 객체 만들어주기
        LocalDateTime start = LocalDateTime.of(2025, 5, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2025, 5, 1, 11, 30);
        RequestTime requestTime = RequestTime.of(start.toLocalDate(), start, end, coffeechatId);
        ReflectionTestUtils.setField(requestTime, "requestTimeId", requestTimeId);


        // 커피챗 객체 만들어주기
        Coffeechat mockCoffeechat = Coffeechat.of(12L,
                2L,
                "취업 관련 꿀팁 얻고 싶어요.",
                2);

        // repo setting
        when(coffeechatRepository.findById(coffeechatId)).thenReturn(Optional.of(mockCoffeechat));
        when(requestTimeRepository.findAllByCoffeechatId(coffeechatId)).thenReturn(List.of(requestTime));
        doNothing().when(requestTimeRepository).deleteById(requestTimeId);

        // when & then: 예외가 발생하지 않으면 테스트 통과
        assertDoesNotThrow(() -> coffeechatCommandService.rejectCoffeechatTime(coffeechatId));
    }

    @DisplayName("커피챗 거절 시 객체 찾지 못함")
    @Test
    void rejectCoffeechatTime_실패() {
        // given
        Long coffeechatId = 999L;

        // repo setting
        when(coffeechatRepository.findById(coffeechatId)).thenReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> coffeechatCommandService.rejectCoffeechatTime(coffeechatId));
        assertEquals(ErrorCode.COFFEECHAT_NOT_FOUND, exception.getErrorCode());
    }

    @DisplayName("커피챗 종료 성공")
    @Test
    void closeCoffeechat_성공() {
        // given
        Long coffeechatId = 999L;

        // 커피챗 객체 만들어주기
        Coffeechat mockCoffeechat = Coffeechat.of(12L,
                2L,
                "취업 관련 꿀팁 얻고 싶어요.",
                2);

        // repo setting
        when(coffeechatRepository.findById(coffeechatId)).thenReturn(Optional.of(mockCoffeechat));

        // when & then: 예외가 발생하지 않으면 테스트 통과
        assertDoesNotThrow(() -> coffeechatCommandService.closeCoffeechat(coffeechatId));
    }

    @DisplayName("커피챗 종료 시 객체 찾지 못함")
    @Test
    void closeCoffeechat_실패() {
        // given
        Long coffeechatId = 999L;

        // repo setting
        when(coffeechatRepository.findById(coffeechatId)).thenReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> coffeechatCommandService.closeCoffeechat(coffeechatId));
        assertEquals(ErrorCode.COFFEECHAT_NOT_FOUND, exception.getErrorCode());
    }

    @DisplayName("멘티 구매 확정 성공")
    @Test
    void confirmPurchaseCoffeechat_성공() {
        // given
        Long coffeechatId = 999L;
        Long mentorId = 2L;

        // coffeechat repo setting
        Coffeechat mockCoffeechat = Coffeechat.of(12L,
                mentorId,
                "취업 관련 꿀팁 얻고 싶어요.",
                2);
        when(coffeechatRepository.findById(coffeechatId)).thenReturn(Optional.of(mockCoffeechat));

        // mentorService setting
        MentorDTO mentorDTO = MentorDTO.builder()
                .price(10)
                .isActive(true)
                .build();
        when(mentorService.getMentorInfo(mentorId)).thenReturn(mentorDTO);
        doNothing().when(saleCommandService).addSaleHistory(mentorId, 10, coffeechatId);

        // when & then: 예외가 발생하지 않으면 테스트 통과
        assertDoesNotThrow(() -> coffeechatCommandService.confirmPurchaseCoffeechat(coffeechatId));
    }

    @DisplayName("구매확정 시 객체 찾지 못함")
    @Test
    void confirmPurchaseCoffeechat_실패() {
        // given
        Long coffeechatId = 999L;

        // repo setting
        when(coffeechatRepository.findById(coffeechatId)).thenReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> coffeechatCommandService.confirmPurchaseCoffeechat(coffeechatId));
        assertEquals(ErrorCode.COFFEECHAT_NOT_FOUND, exception.getErrorCode());
    }
}