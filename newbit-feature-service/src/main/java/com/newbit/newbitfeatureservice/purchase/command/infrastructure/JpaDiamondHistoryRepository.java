package com.newbit.newbitfeatureservice.purchase.command.infrastructure;

import com.newbit.newbitfeatureservice.purchase.command.domain.aggregate.DiamondHistory;
import com.newbit.newbitfeatureservice.purchase.command.domain.repository.DiamondHistoryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaDiamondHistoryRepository extends DiamondHistoryRepository, JpaRepository<DiamondHistory, Long> {
}
