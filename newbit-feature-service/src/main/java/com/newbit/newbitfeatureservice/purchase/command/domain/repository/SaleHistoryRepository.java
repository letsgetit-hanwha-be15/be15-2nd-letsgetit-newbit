package com.newbit.newbitfeatureservice.purchase.command.domain.repository;

import com.newbit.newbitfeatureservice.purchase.command.domain.aggregate.SaleHistory;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleHistoryRepository{
    SaleHistory save(SaleHistory saleHistory);

    List<SaleHistory> findAllByIsSettledFalseAndCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}