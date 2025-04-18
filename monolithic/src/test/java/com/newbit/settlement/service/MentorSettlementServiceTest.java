package com.newbit.settlement.service;

import com.newbit.purchase.command.domain.aggregate.SaleHistory;
import com.newbit.purchase.command.domain.repository.SaleHistoryRepository;
import com.newbit.settlement.dto.response.MentorSettlementDetailResponseDto;
import com.newbit.settlement.dto.response.MentorSettlementListResponseDto;
import com.newbit.settlement.entity.MonthlySettlementHistory;
import com.newbit.settlement.repository.MonthlySettlementHistoryRepository;
import com.newbit.user.entity.Mentor;
import com.newbit.user.entity.User;
import com.newbit.user.service.MentorService;
import com.newbit.user.service.UserQueryService;
import com.newbit.user.support.MailServiceSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.domain.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MentorSettlementServiceTest {

    @Mock
    private SaleHistoryRepository saleHistoryRepository;

    @Mock
    private MonthlySettlementHistoryRepository monthlySettlementHistoryRepository;

    @Mock
    private MentorService mentorService;

    @Mock
    private UserQueryService userQueryService;

    @Mock
    private MailServiceSupport mailServiceSupport;

    @InjectMocks
    private MentorSettlementService mentorSettlementService;

    @Test
    @DisplayName("멘토 정산 생성 - 성공")
    void generateMonthlySettlements_success() {
        // given
        int year = 2025, month = 4;
        Long mentorId = 1L;

        SaleHistory sale1 = mock(SaleHistory.class);
        SaleHistory sale2 = mock(SaleHistory.class);
        when(sale1.getMentorId()).thenReturn(mentorId);
        when(sale2.getMentorId()).thenReturn(mentorId);
        when(sale1.getSaleAmount()).thenReturn(BigDecimal.valueOf(5000));
        when(sale2.getSaleAmount()).thenReturn(BigDecimal.valueOf(3000));

        when(saleHistoryRepository.findAllByIsSettledFalseAndCreatedAtBetween(any(), any()))
                .thenReturn(List.of(sale1, sale2));

        // when
        mentorSettlementService.generateMonthlySettlements(year, month);

        // then
        verify(monthlySettlementHistoryRepository).save(any(MonthlySettlementHistory.class));
        verify(sale1).markAsSettled();
        verify(sale2).markAsSettled();
    }

    @Test
    @DisplayName("멘토 정산 목록 조회 - 성공")
    void getMySettlements_success() {
        Long mentorId = 1L;
        int page = 1, size = 10;
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "settledAt"));

        MonthlySettlementHistory history1 = MonthlySettlementHistory.builder()
                .monthlySettlementHistoryId(1L)
                .settlementYear(2025)
                .settlementMonth(4)
                .settlementAmount(BigDecimal.valueOf(100000))
                .settledAt(LocalDateTime.now())
                .mentorId(mentorId)
                .build();

        Page<MonthlySettlementHistory> pageResult = new PageImpl<>(List.of(history1), pageable, 1);
        when(monthlySettlementHistoryRepository.findAllByMentorId(eq(mentorId), eq(pageable)))
                .thenReturn(pageResult);

        // when
        MentorSettlementListResponseDto result = mentorSettlementService.getMySettlements(mentorId, page, size);

        // then
        assertThat(result.getSettlements()).hasSize(1);
        assertThat(result.getSettlements().get(0).getSettlementAmount()).isEqualByComparingTo("100000");
    }

    @Test
    @DisplayName("정산 상세 내역 조회 - 성공")
    void getSettlementDetail_success() {
        Long settlementId = 1L;
        MonthlySettlementHistory history = MonthlySettlementHistory.builder()
                .monthlySettlementHistoryId(settlementId)
                .settlementYear(2025)
                .settlementMonth(4)
                .settlementAmount(BigDecimal.valueOf(123456))
                .settledAt(LocalDateTime.of(2025, 4, 30, 23, 59))
                .mentorId(1L)
                .build();

        when(monthlySettlementHistoryRepository.findById(settlementId)).thenReturn(Optional.of(history));

        MentorSettlementDetailResponseDto result = mentorSettlementService.getSettlementDetail(settlementId);

        assertThat(result.getSettlementId()).isEqualTo(settlementId);
        assertThat(result.getAmount()).isEqualByComparingTo("123456");
    }

    @Test
    @DisplayName("정산 이메일 전송 - 성공")
    void sendSettlementEmail_success() {
        Long mentorId = 1L;
        Long settlementId = 100L;

        MonthlySettlementHistory history = MonthlySettlementHistory.builder()
                .monthlySettlementHistoryId(settlementId)
                .settlementYear(2025)
                .settlementMonth(4)
                .settlementAmount(BigDecimal.valueOf(150000))
                .settledAt(LocalDateTime.now())
                .mentorId(mentorId)
                .build();

        when(monthlySettlementHistoryRepository.findById(settlementId)).thenReturn(Optional.of(history));
        when(mentorService.getUserIdByMentorId(mentorId)).thenReturn(10L);
        when(userQueryService.getEmailByUserId(10L)).thenReturn("test@newbit.com");
        when(userQueryService.getNicknameByUserId(10L)).thenReturn("홍길동");

        // when
        mentorSettlementService.sendSettlementEmail(mentorId, settlementId);

        // then
        verify(mailServiceSupport).sendMailSupport(
                eq("test@newbit.com"),
                contains("정산 내역"),
                contains("150000")
        );
    }
}
