package com.newbit.purchase.command.domain.repository;

import com.newbit.purchase.command.domain.aggregate.ColumnPurchaseHistory;

public interface ColumnPurchaseHistoryRepository {
    boolean existsByUserUserIdAndColumnColumnId(Long userId, Long columnId);
    ColumnPurchaseHistory save(ColumnPurchaseHistory history);
}

