package com.newbit.newbitfeatureservice.purchase.command.infrastructure;

import com.newbit.newbitfeatureservice.purchase.command.domain.aggregate.ColumnPurchaseHistory;
import com.newbit.newbitfeatureservice.purchase.command.domain.repository.ColumnPurchaseHistoryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaColumnPurchaseHistoryRepository extends ColumnPurchaseHistoryRepository, JpaRepository<ColumnPurchaseHistory, Long> {
}
