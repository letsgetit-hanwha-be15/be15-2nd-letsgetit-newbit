package com.newbit.purchase.command.domain.repository;

import com.newbit.purchase.command.domain.aggregate.SaleHistory;

public interface SaleHistoryRepository{
    SaleHistory save(SaleHistory saleHistory);
}