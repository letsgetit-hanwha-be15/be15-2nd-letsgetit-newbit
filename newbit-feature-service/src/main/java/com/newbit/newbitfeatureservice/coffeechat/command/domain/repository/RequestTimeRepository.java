package com.newbit.newbitfeatureservice.coffeechat.command.domain.repository;

import com.newbit.newbitfeatureservice.coffeechat.command.domain.aggregate.RequestTime;

import java.util.List;
import java.util.Optional;

public interface RequestTimeRepository {
    RequestTime save(RequestTime requestTime);

    Optional<RequestTime> findById(Long requestTimeId);

    List<RequestTime> findAllByCoffeechatId(Long coffeechatId);

    void deleteById(Long requestTimeId);
}
