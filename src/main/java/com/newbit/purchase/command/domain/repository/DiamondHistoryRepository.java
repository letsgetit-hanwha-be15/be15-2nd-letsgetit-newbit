package com.newbit.purchase.command.domain.repository;

import com.newbit.purchase.command.domain.aggregate.DiamondHistory;

public interface DiamondHistoryRepository {
    DiamondHistory save(DiamondHistory diamondHistory);
}