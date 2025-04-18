package com.newbit.purchase.command.infrastructure;

import com.newbit.purchase.command.domain.aggregate.ColumnPurchaseHistory;
import com.newbit.purchase.command.domain.repository.ColumnPurchaseHistoryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaColumnPurchaseHistoryRepository extends ColumnPurchaseHistoryRepository, JpaRepository<ColumnPurchaseHistory, Long> {
}
