package com.newbit.newbitfeatureservice.coffeechat.query.service;

import com.newbit.newbitfeatureservice.coffeechat.command.domain.aggregate.ProgressStatus;
import com.newbit.newbitfeatureservice.coffeechat.query.dto.request.CoffeechatSearchServiceRequest;
import com.newbit.newbitfeatureservice.coffeechat.query.dto.response.*;
import com.newbit.newbitfeatureservice.coffeechat.query.mapper.CoffeechatMapper;
import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoffeechatQueryServiceTest {
    @Mock
    private CoffeechatMapper coffeechatMapper;

    @InjectMocks
    private CoffeechatQueryService coffeechatQueryService;

    static Stream<CoffeechatTestParams> coffeechatProvider() {
        // 테스트에 사용할 공통 mentor, mentee

        CoffeechatDto coffeechat1 = CoffeechatDto.builder()
                .coffeechatId(1L)
                .progressStatus(com.newbit.newbitfeatureservice.coffeechat.query.dto.response.ProgressStatus.COFFEECHAT_WAITING)
                .requestMessage("첫번째 커피챗 신청드립니다.")
                .confirmedSchedule(LocalDateTime.of(2025, 4, 11, 19, 0))
                .endedAt(LocalDateTime.of(2025, 4, 11, 19, 30))
                .mentorId(1L) // 1L
                .menteeId(1L)
                .build();

        CoffeechatDto coffeechat2 = CoffeechatDto.builder()
                .coffeechatId(2L)
                .progressStatus(com.newbit.newbitfeatureservice.coffeechat.query.dto.response.ProgressStatus.IN_PROGRESS)
                .requestMessage("두번째 커피챗도 부탁드립니다.")
                .confirmedSchedule(LocalDateTime.of(2025, 4, 12, 19, 0))
                .endedAt(LocalDateTime.of(2025, 4, 12, 19, 30))
                .mentorId(1L) // 1L
                .menteeId(2L)
                .build();

        return Stream.of(
                new CoffeechatTestParams(1L, coffeechat1),
                new CoffeechatTestParams(2L, coffeechat2)
        );
    }


    @DisplayName("1-1. 커피챗 상세 조회 - 성공")
    @ParameterizedTest(name = "[{index}] coffeechatId={0}")
    @MethodSource("coffeechatProvider")
    void getCoffeechat_정상케이스(CoffeechatTestParams params) {
        // given
        given(coffeechatMapper.selectCoffeechatById(params.coffeechatId)).willReturn(params.coffeechatDto);

        // when
        CoffeechatDetailResponse result = coffeechatQueryService.getCoffeechat(params.coffeechatId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCoffeechat()).isEqualTo(params.coffeechatDto);
    }

    static Stream<Long> coffeechatNotFoundProvider() {
        return Stream.of(0L, 999L, 10000L);
    }

    @DisplayName("1-2. 커피챗 상세 조회 - 실패(존재하지 않는 커피챗)")
    @ParameterizedTest(name = "[{index}] coffeechatId={0}")
    @MethodSource("coffeechatNotFoundProvider")
    void getCoffeechat_실패케이스(Long invalidCoffeechatId) {
        // given
        given(coffeechatMapper.selectCoffeechatById(invalidCoffeechatId)).willReturn(null);

        // when & then
        assertThatThrownBy(() -> coffeechatQueryService.getCoffeechat(invalidCoffeechatId))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.COFFEECHAT_NOT_FOUND.getMessage());

    }

    @DisplayName("2-1. 커피챗 목록 조회 - 멘토가 조회")
    @Test
    void getCoffeechats_멘토() {
        // given
        CoffeechatSearchServiceRequest coffeechatSearchServiceRequest = new CoffeechatSearchServiceRequest();
        coffeechatSearchServiceRequest.setMentorId(1L);

        CoffeechatDto coffeechat1 = CoffeechatDto.builder()
                .coffeechatId(1L)
                .progressStatus(com.newbit.newbitfeatureservice.coffeechat.query.dto.response.ProgressStatus.COFFEECHAT_WAITING)
                .requestMessage("첫번째 커피챗 신청드립니다.")
                .confirmedSchedule(LocalDateTime.of(2025, 4, 11, 19, 0))
                .endedAt(LocalDateTime.of(2025, 4, 11, 19, 30))
                .mentorId(1L) // 1L
                .menteeId(1L)
                .build();

        CoffeechatDto coffeechat2 = CoffeechatDto.builder()
                .coffeechatId(2L)
                .progressStatus(com.newbit.newbitfeatureservice.coffeechat.query.dto.response.ProgressStatus.IN_PROGRESS)
                .requestMessage("두번째 커피챗도 부탁드립니다.")
                .confirmedSchedule(LocalDateTime.of(2025, 4, 12, 19, 0))
                .endedAt(LocalDateTime.of(2025, 4, 12, 19, 30))
                .mentorId(1L) // 1L
                .menteeId(2L)
                .build();

        List<CoffeechatDto> originalList = Arrays.asList(coffeechat1, coffeechat2);
        when(coffeechatMapper.selectCoffeechats(coffeechatSearchServiceRequest)).thenReturn(originalList);

        // when
        CoffeechatListResponse results = coffeechatQueryService.getCoffeechats(coffeechatSearchServiceRequest);

        // then

        // 주어진 결과 값이 올바른 비즈니스 로직을 통해 가공되었는지 확인
        assertNotNull(results);
        assertEquals(2, results.getCoffeechats().size());
        assertEquals("첫번째 커피챗 신청드립니다.", results.getCoffeechats().get(0).getRequestMessage());
        assertEquals("두번째 커피챗도 부탁드립니다.", results.getCoffeechats().get(1).getRequestMessage());
        // 해당 객체에서 메소드 호출 여부 확인 -> 서비스 내부의 상호 작용이 기대한 대로 이루어졌는지
        verify(coffeechatMapper).selectCoffeechats(coffeechatSearchServiceRequest);
    }

    @DisplayName("2-2. 커피챗 목록 조회 - 멘티가 조회")
    @Test
    void getCoffeechats_멘티() {
        // given
        CoffeechatSearchServiceRequest coffeechatSearchServiceRequest = new CoffeechatSearchServiceRequest();
        coffeechatSearchServiceRequest.setMenteeId(1L);

        CoffeechatDto coffeechat1 = CoffeechatDto.builder()
                .coffeechatId(1L)
                .progressStatus(com.newbit.newbitfeatureservice.coffeechat.query.dto.response.ProgressStatus.COFFEECHAT_WAITING)
                .requestMessage("첫번째 커피챗 신청드립니다.")
                .confirmedSchedule(LocalDateTime.of(2025, 4, 11, 19, 0))
                .endedAt(LocalDateTime.of(2025, 4, 11, 19, 30))
                .mentorId(1L) // 1L
                .menteeId(1L)
                .build();

        List<CoffeechatDto> originalList = Arrays.asList(coffeechat1);
        when(coffeechatMapper.selectCoffeechats(coffeechatSearchServiceRequest)).thenReturn(originalList);

        // when
        CoffeechatListResponse results = coffeechatQueryService.getCoffeechats(coffeechatSearchServiceRequest);

        // then

        // 주어진 결과 값이 올바른 비즈니스 로직을 통해 가공되었는지 확인
        assertNotNull(results);
        assertEquals(1, results.getCoffeechats().size());
        assertEquals("첫번째 커피챗 신청드립니다.", results.getCoffeechats().get(0).getRequestMessage());
        // 해당 객체에서 메소드 호출 여부 확인 -> 서비스 내부의 상호 작용이 기대한 대로 이루어졌는지
        verify(coffeechatMapper).selectCoffeechats(coffeechatSearchServiceRequest);
    }

    @DisplayName("3-1. 커피챗 요청 목록 조회 - 성공")
    @Test
    void getCoffeechatRequestTimes_성공() {
        // given
        Long coffeechatId = 4L;

        RequestTimeDto request1 = RequestTimeDto.builder()
                .requestTimeId(1L)
                .eventDate(LocalDate.of(2025, 4, 11))
                .startTime(LocalDateTime.of(2025, 4, 11, 19, 30))
                .endTime(LocalDateTime.of(2025, 4, 11, 21, 30))
                .coffeechatId(coffeechatId)
                .build();

        RequestTimeDto request2 = RequestTimeDto.builder()
                .requestTimeId(1L)
                .eventDate(LocalDate.of(2025, 4, 12))
                .startTime(LocalDateTime.of(2025, 4, 12, 19, 30))
                .endTime(LocalDateTime.of(2025, 4, 12, 21, 30))
                .coffeechatId(coffeechatId)
                .build();


        List<RequestTimeDto> originalList = Arrays.asList(request1, request2);
        when(coffeechatMapper.selectRequestTimeByCoffeechatId(coffeechatId)).thenReturn(originalList);

        // when
        RequestTimeListResponse results = coffeechatQueryService.getCoffeechatRequestTimes(coffeechatId);

        // then

        // 주어진 결과 값이 올바른 비즈니스 로직을 통해 가공되었는지 확인
        assertNotNull(results);
        assertEquals(2, results.getRequestTimes().size());
        // 해당 객체에서 메소드 호출 여부 확인 -> 서비스 내부의 상호 작용이 기대한 대로 이루어졌는지
        verify(coffeechatMapper).selectRequestTimeByCoffeechatId(coffeechatId);
    }

    @DisplayName("3-2. 커피챗 요청 목록 조회 - 실패")
    @Test
    void getCoffeechatRequestTimes_실패() {
        // given
        Long invalidCoffeechatId = 22L;
        when(coffeechatMapper.selectRequestTimeByCoffeechatId(invalidCoffeechatId)).thenReturn(null);

        // when
        assertThatThrownBy(() -> coffeechatQueryService.getCoffeechatRequestTimes(invalidCoffeechatId))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.REQUEST_TIME_NOT_FOUND.getMessage());

    }

    static class CoffeechatTestParams {
        final Long coffeechatId;
        final CoffeechatDto coffeechatDto;

        CoffeechatTestParams(Long coffeechatId, CoffeechatDto coffeechatDto) {
            this.coffeechatId = coffeechatId;
            this.coffeechatDto = coffeechatDto;
        }
    }

    @DisplayName("멘토에게 요청온 커피챗 목록 조회")
    @Test
    void getCoffeechats_멘토_요청() {
        // given
        CoffeechatSearchServiceRequest coffeechatSearchServiceRequest = new CoffeechatSearchServiceRequest();
        coffeechatSearchServiceRequest.setMentorId(1L);
        coffeechatSearchServiceRequest.setProgressStatus(ProgressStatus.IN_PROGRESS);

        CoffeechatDto coffeechat2 = CoffeechatDto.builder()
                .coffeechatId(2L)
                .progressStatus(com.newbit.newbitfeatureservice.coffeechat.query.dto.response.ProgressStatus.IN_PROGRESS)
                .requestMessage("두번째 커피챗도 부탁드립니다.")
                .confirmedSchedule(LocalDateTime.of(2025, 4, 12, 19, 0))
                .endedAt(LocalDateTime.of(2025, 4, 12, 19, 30))
                .mentorId(1L) // 1L
                .menteeId(2L)
                .build();

        List<CoffeechatDto> originalList = Arrays.asList(coffeechat2);
        when(coffeechatMapper.selectCoffeechats(coffeechatSearchServiceRequest)).thenReturn(originalList);

        // when
        CoffeechatListResponse results = coffeechatQueryService.getCoffeechats(coffeechatSearchServiceRequest);

        // then

        // 주어진 결과 값이 올바른 비즈니스 로직을 통해 가공되었는지 확인
        assertNotNull(results);
        assertEquals(1, results.getCoffeechats().size());
        assertEquals("두번째 커피챗도 부탁드립니다.", results.getCoffeechats().get(0).getRequestMessage());
        // 해당 객체에서 메소드 호출 여부 확인 -> 서비스 내부의 상호 작용이 기대한 대로 이루어졌는지
        verify(coffeechatMapper).selectCoffeechats(coffeechatSearchServiceRequest);
    }
}
