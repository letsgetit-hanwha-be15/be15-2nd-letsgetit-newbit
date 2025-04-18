package com.newbit.newbitfeatureservice.purchase.command.domain.repository;

import com.newbit.newbitfeatureservice.purchase.command.domain.aggregate.DiamondHistory;

public interface DiamondHistoryRepository {
    DiamondHistory save(DiamondHistory diamondHistory);
}