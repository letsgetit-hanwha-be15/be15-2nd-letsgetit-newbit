package com.newbit.coffeechat.command.domain.repository;

import com.newbit.coffeechat.command.domain.aggregate.RequestTime;

public interface RequestTimeRepository {
    RequestTime save(RequestTime requestTime);
}
