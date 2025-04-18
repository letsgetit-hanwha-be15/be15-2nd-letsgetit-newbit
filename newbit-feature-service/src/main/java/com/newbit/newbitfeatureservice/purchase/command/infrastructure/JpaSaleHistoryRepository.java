package com.newbit.newbitfeatureservice.purchase.command.infrastructure;

import com.newbit.newbitfeatureservice.purchase.command.domain.aggregate.SaleHistory;
import com.newbit.newbitfeatureservice.purchase.command.domain.repository.SaleHistoryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaSaleHistoryRepository extends SaleHistoryRepository, JpaRepository<SaleHistory, Long> {
}
