package com.newbit.purchase.command.infrastructure;

import com.newbit.purchase.command.domain.aggregate.SaleHistory;
import com.newbit.purchase.command.domain.repository.SaleHistoryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaSaleHistoryRepository extends SaleHistoryRepository, JpaRepository<SaleHistory, Long> {
}
