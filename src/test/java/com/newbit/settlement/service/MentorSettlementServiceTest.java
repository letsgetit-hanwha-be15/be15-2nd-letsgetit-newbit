package com.newbit.settlement.service;

import com.newbit.purchase.command.domain.aggregate.SaleHistory;
import com.newbit.purchase.command.domain.repository.SaleHistoryRepository;
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

@ExtendWith(MockitoExtension.class)
class MentorSettlementServiceTest {

    @Mock
    private SaleHistoryRepository saleHistoryRepository;

    @Mock
    private MonthlySettlementHistoryRepository monthlySettlementHistoryRepository;

    @Mock
    private MentorService mentorService;

    @InjectMocks
    private MentorSettlementService settlementService;

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
        settlementService.generateMonthlySettlements(year, month);

        // then
        verify(monthlySettlementHistoryRepository, times(1))
                .save(any(MonthlySettlementHistory.class));


        verify(sale1).markAsSettled();
        verify(sale2).markAsSettled();

    }
}
