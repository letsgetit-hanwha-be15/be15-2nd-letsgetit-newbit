package com.newbit.newbitfeatureservice.purchase.command.domain.repository;

import com.newbit.newbitfeatureservice.purchase.command.domain.aggregate.ColumnPurchaseHistory;

public interface ColumnPurchaseHistoryRepository {
    boolean existsByUserIdAndColumnId(Long userId, Long columnId);
    ColumnPurchaseHistory save(ColumnPurchaseHistory history);
}

