package com.newbit.purchase.command.infrastructure;

import com.newbit.purchase.command.domain.aggregate.DiamondHistory;
import com.newbit.purchase.command.domain.repository.DiamondHistoryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaDiamondHistoryRepository extends DiamondHistoryRepository, JpaRepository<DiamondHistory, Long> {
}
