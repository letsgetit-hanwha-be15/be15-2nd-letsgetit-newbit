package com.newbit.coffeechat.command.domain.repository;

import com.newbit.coffeechat.command.domain.aggregate.Coffeechat;
import java.util.Optional;

public interface CoffeechatRepository {
    Coffeechat save(Coffeechat coffeechat);

    Optional<Coffeechat> findById(Long coffeechatId);
}
