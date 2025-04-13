package com.newbit.settlement.service;

import com.newbit.purchase.command.domain.aggregate.SaleHistory;
import com.newbit.purchase.command.domain.repository.SaleHistoryRepository;
import com.newbit.settlement.dto.response.MentorSettlementListResponseDto;
import com.newbit.settlement.entity.MonthlySettlementHistory;
import com.newbit.settlement.repository.MonthlySettlementHistoryRepository;
import com.newbit.user.entity.Mentor;
import com.newbit.user.service.MentorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

    @InjectMocks
    private MentorSettlementService mentorSettlementService;

    @Test
    @DisplayName("멘토 정산 생성 - 성공")
    void generateMonthlySettlements_success() {
        // given
        int year = 2025;
        int month = 4;
        Long mentorId = 1L;

        SaleHistory sale1 = mock(SaleHistory.class);
        SaleHistory sale2 = mock(SaleHistory.class);
        when(sale1.getMentorId()).thenReturn(mentorId);
        when(sale2.getMentorId()).thenReturn(mentorId);
        when(sale1.getSaleAmount()).thenReturn(BigDecimal.valueOf(5000));
        when(sale2.getSaleAmount()).thenReturn(BigDecimal.valueOf(3000));

        SaleHistory sale = mock(SaleHistory.class);
        List<SaleHistory> sales = List.of(sale1, sale2);
        when(saleHistoryRepository.findAllByIsSettledFalseAndCreatedAtBetween(any(), any()))
                .thenReturn(sales);

        Mentor mentor = Mentor.builder().mentorId(mentorId).build();
        when(mentorService.getMentorEntityByUserId(mentorId)).thenReturn(mentor);

        // when
        mentorSettlementService.generateMonthlySettlements(year, month);

        // then
        verify(monthlySettlementHistoryRepository, times(1))
                .save(any(MonthlySettlementHistory.class));


        verify(sale1).markAsSettled();
        verify(sale2).markAsSettled();

    }

    @Test
    @DisplayName("멘토 정산 목록 조회 - 성공")
    void getMySettlements_success() {
        // given
        Long mentorId = 1L;
        int page = 1;
        int size = 10;

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "settledAt"));

        MonthlySettlementHistory history1 = MonthlySettlementHistory.builder()
                .monthlySettlementHistoryId(1L)
                .settlementYear(2024)
                .settlementMonth(12)
                .settlementAmount(new BigDecimal("100000.00"))
                .settledAt(LocalDateTime.of(2024, 12, 31, 23, 59))
                .mentor(Mentor.builder().mentorId(mentorId).build())
                .build();

        MonthlySettlementHistory history2 = MonthlySettlementHistory.builder()
                .monthlySettlementHistoryId(2L)
                .settlementYear(2025)
                .settlementMonth(1)
                .settlementAmount(new BigDecimal("85000.00"))
                .settledAt(LocalDateTime.of(2025, 1, 31, 22, 30))
                .mentor(Mentor.builder().mentorId(mentorId).build())
                .build();

        Page<MonthlySettlementHistory> pageResult =
                new PageImpl<>(List.of(history1, history2), pageable, 2);

        when(monthlySettlementHistoryRepository.findAllByMentor_MentorId(eq(mentorId), eq(pageable)))
                .thenReturn(pageResult);

        // when
        MentorSettlementListResponseDto result = mentorSettlementService.getMySettlements(mentorId, page, size);

        // then
        assertThat(result.getSettlements()).hasSize(2);
        assertThat(result.getSettlements().get(0).getSettlementAmount()).isEqualTo(new BigDecimal("100000.00"));
        assertThat(result.getPagination().getCurrentPage()).isEqualTo(page);
        assertThat(result.getPagination().getTotalItems()).isEqualTo(2);
    }
}
