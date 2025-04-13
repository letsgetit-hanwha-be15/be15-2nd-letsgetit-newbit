package com.newbit.settlement.service;

import com.newbit.common.dto.Pagination;
import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.purchase.command.domain.aggregate.SaleHistory;
import com.newbit.purchase.command.domain.repository.SaleHistoryRepository;
import com.newbit.settlement.dto.response.MentorSettlementDetailResponseDto;
import com.newbit.settlement.dto.response.MentorSettlementListResponseDto;
import com.newbit.settlement.dto.response.MentorSettlementSummaryDto;
import com.newbit.settlement.entity.MonthlySettlementHistory;
import com.newbit.settlement.repository.MonthlySettlementHistoryRepository;
import com.newbit.user.entity.Mentor;
import com.newbit.user.service.MentorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Transactional(readOnly = true)
    public MentorSettlementListResponseDto getMySettlements(Long mentorId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "settledAt"));
        Page<MonthlySettlementHistory> resultPage =
                monthlySettlementHistoryRepository.findAllByMentor_MentorId(mentorId, pageable);

        List<MentorSettlementSummaryDto> content = resultPage.getContent().stream()
                .map(MentorSettlementSummaryDto::from)
                .collect(Collectors.toList());

        return MentorSettlementListResponseDto.builder()
                .settlements(content)
                .pagination(Pagination.builder()
                        .currentPage(page)
                        .totalPage(resultPage.getTotalPages())
                        .totalItems(resultPage.getTotalElements())
                        .build())
                .build();
    }

    @Transactional(readOnly = true)
    public MentorSettlementDetailResponseDto getSettlementDetail(Long settlementId) {
        MonthlySettlementHistory history = monthlySettlementHistoryRepository.findById(settlementId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SETTLEMENT_NOT_FOUND));

        return MentorSettlementDetailResponseDto.from(history);
    }
}
