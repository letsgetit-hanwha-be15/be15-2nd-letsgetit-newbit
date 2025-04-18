package com.newbit.coffeechat.command.domain.repository;

import com.newbit.coffeechat.command.domain.aggregate.Coffeechat;
import com.newbit.coffeechat.command.domain.aggregate.RequestTime;

import java.util.List;
import java.util.Optional;

public interface RequestTimeRepository {
    RequestTime save(RequestTime requestTime);

    Optional<RequestTime> findById(Long requestTimeId);

    List<RequestTime> findAllByCoffeechatId(Long coffeechatId);

    void deleteById(Long requestTimeId);
}
