package com.newbit.purchase.command.domain.repository;

import com.newbit.purchase.command.domain.aggregate.PointHistory;

public interface PointHistoryRepository {
    PointHistory save(PointHistory pointHistory);
}
