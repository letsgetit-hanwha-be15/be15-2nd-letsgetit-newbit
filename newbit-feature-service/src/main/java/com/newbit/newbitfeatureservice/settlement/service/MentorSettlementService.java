package com.newbit.newbitfeatureservice.settlement.service;

import com.newbit.newbitfeatureservice.client.user.MailFeignClient;
import com.newbit.newbitfeatureservice.client.user.MentorFeignClient;
import com.newbit.newbitfeatureservice.client.user.UserFeignClient;
import com.newbit.newbitfeatureservice.common.dto.Pagination;
import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import com.newbit.newbitfeatureservice.purchase.command.domain.aggregate.SaleHistory;
import com.newbit.newbitfeatureservice.purchase.command.domain.repository.SaleHistoryRepository;
import com.newbit.newbitfeatureservice.settlement.dto.response.MentorSettlementDetailResponseDto;
import com.newbit.newbitfeatureservice.settlement.dto.response.MentorSettlementListResponseDto;
import com.newbit.newbitfeatureservice.settlement.dto.response.MentorSettlementSummaryDto;
import com.newbit.newbitfeatureservice.settlement.entity.MonthlySettlementHistory;
import com.newbit.newbitfeatureservice.settlement.repository.MonthlySettlementHistoryRepository;
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
    private final MentorFeignClient mentorFeignClient;
    private final MailFeignClient mailFeignClient;
    private final UserFeignClient userFeignClient;

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

            MonthlySettlementHistory settlement = MonthlySettlementHistory.of(mentorId, year, month, totalAmount);
            monthlySettlementHistoryRepository.save(settlement);

            // 해당 판매내역 정산처리
            sales.forEach(SaleHistory::markAsSettled);
        }
    }

    @Transactional(readOnly = true)
    public MentorSettlementListResponseDto getMySettlements(Long mentorId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "settledAt"));
        Page<MonthlySettlementHistory> resultPage =
                monthlySettlementHistoryRepository.findAllByMentorId(mentorId, pageable);

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

    @Transactional(readOnly = true)
    public void sendSettlementEmail(Long mentorId, Long settlementId) {
        MonthlySettlementHistory history = monthlySettlementHistoryRepository.findById(settlementId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SETTLEMENT_NOT_FOUND));

        if(!history.getMentorId().equals(mentorId)) {
            throw new BusinessException(ErrorCode.SETTLEMENT_NOT_FOUND);
        }

        Long userId = mentorFeignClient.getUserIdByMentorId(mentorId).getData();
        String email = userFeignClient.getEmailByUserId(userId).getData();
        String nickname = userFeignClient.getNicknameByUserId(userId).getData();

        String subject = "[Newbit] 월별 정산 내역 안내";
        String content = String.format("""
                <h3>안녕하세요, %s님</h3>
                <p>%d년 %d월 정산 내역을 안내드립니다.</p>
                <ul>
                    <li><strong>정산 금액:</strong> %s원</li>
                    <li><strong>정산 완료일:</strong> %s</li>
                </ul>
                <p>감사합니다.</p>
                """,
                nickname,
                history.getSettlementYear(),
                history.getSettlementMonth(),
                history.getSettlementAmount().toPlainString(),
                history.getSettledAt().toString()
        );

        mailFeignClient.sendMail(email, subject, content);

    }


}
