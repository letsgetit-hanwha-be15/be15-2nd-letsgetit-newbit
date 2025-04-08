package com.newbit.coffeechat.query.service;

import com.newbit.coffeechat.query.dto.response.*;
import com.newbit.coffeechat.query.mapper.CoffeechatMapper;
import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CoffeechatQueryServiceTest {

    @Mock
    private CoffeechatMapper coffeechatMapper;

    @InjectMocks
    private CoffeechatQueryService coffeechatQueryService;

    static Stream<CoffeechatTestParams> coffeechatProvider() {
        // 테스트에 사용할 공통 mentor, mentee
        UserDto mentor = UserDto.builder()
                .userId(1L)
                .nickname("최강멘토")
                .authority(Authority.MENTOR)
                .build();

        UserDto mentee = UserDto.builder()
                .userId(2L)
                .nickname("신입삐약")
                .authority(Authority.USER)
                .build();

        CoffeechatDto coffeechat1 = CoffeechatDto.builder()
                .coffeechatId(1L)
                .progressStatus(ProgressStatus.COFFEECHAT_WAITING)
                .requestMessage("첫번째 커피챗 신청드립니다.")
                .confirmedSchedule(LocalDateTime.of(2025, 4, 11, 19, 0))
                .endedAt(LocalDateTime.of(2025, 4, 11, 19, 30))
                .mentorId(mentor.getUserId()) // 1L
                .temperature(40.0)
                .mentor(mentor)
                .mentee(mentee)
                .build();

        CoffeechatDto coffeechat2 = CoffeechatDto.builder()
                .coffeechatId(2L)
                .progressStatus(ProgressStatus.IN_PROGRESS)
                .requestMessage("두번째 커피챗도 부탁드립니다.")
                .confirmedSchedule(LocalDateTime.of(2025, 4, 12, 19, 0))
                .endedAt(LocalDateTime.of(2025, 4, 12, 19, 30))
                .mentorId(mentor.getUserId()) // 1L
                .temperature(40.0)
                .mentor(mentor)
                .mentee(mentee)
                .build();

        return Stream.of(
                new CoffeechatTestParams(1L, coffeechat1),
                new CoffeechatTestParams(2L, coffeechat2)
        );
    }


    @DisplayName("1. 커피챗 상세 조회 - 성공")
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

    @DisplayName("2. 커피챗 상세 조회 - 실패(존재하지 않는 커피챗)")
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

    static class CoffeechatTestParams {
        final Long coffeechatId;
        final CoffeechatDto coffeechatDto;

        CoffeechatTestParams(Long coffeechatId, CoffeechatDto coffeechatDto) {
            this.coffeechatId = coffeechatId;
            this.coffeechatDto = coffeechatDto;
        }
    }
}
