package com.newbit.settlement.service;

import com.newbit.purchase.command.domain.aggregate.SaleHistory;
import com.newbit.purchase.command.domain.repository.SaleHistoryRepository;
import com.newbit.settlement.entity.MonthlySettlementHistory;
import com.newbit.settlement.repository.MonthlySettlementHistoryRepository;
import com.newbit.user.entity.Mentor;
import com.newbit.user.service.MentorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MentorSettlementService {

    private final SaleHistoryRepository saleHistoryRepository;
    private final MonthlySettlementHistoryRepository monthlySettlementHistoryRepository;
    private final MentorService mentorService;

    @Transactional
    public void generateMonthlySettlements(int year, int month) {
        // 해당 월의 시작과 끝
        LocalDateTime start = LocalDate.of(year, month, 1).atStartOfDay();
        LocalDateTime end = start.plusMonths(1).minusNanos(1);

        // 아직 정산되지 않은 판매 내역 가져오기
        List<SaleHistory> unsettledSales = saleHistoryRepository
                .findAllByIsSettledFalseAndCreatedAtBetween(start, end);

        // 멘토별로 그룹화하여 정산금액 계산
        Map<Long, List<SaleHistory>> groupedByMentor = new HashMap<>();
        for (SaleHistory sale : unsettledSales) {
            groupedByMentor.computeIfAbsent(sale.getMentorId(), k -> new ArrayList<>()).add(sale);
        }

        for (Map.Entry<Long, List<SaleHistory>> entry : groupedByMentor.entrySet()) {
            Long mentorId = entry.getKey();
            List<SaleHistory> sales = entry.getValue();

            BigDecimal totalAmount = sales.stream()
                    .map(SaleHistory::getSaleAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Mentor mentor = mentorService.getMentorEntityByUserId(mentorId);

            MonthlySettlementHistory settlement = MonthlySettlementHistory.of(mentor, year, month, totalAmount);
            monthlySettlementHistoryRepository.save(settlement);

            // 해당 판매내역 정산처리
            sales.forEach(SaleHistory::markAsSettled);
        }
    }
}
