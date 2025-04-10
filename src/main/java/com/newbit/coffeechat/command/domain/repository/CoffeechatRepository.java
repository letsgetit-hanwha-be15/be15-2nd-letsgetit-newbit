package com.newbit.coffeechat.command.domain.repository;

import com.newbit.coffeechat.command.domain.aggregate.Coffeechat;

public interface CoffeechatRepository {
    Coffeechat save(Coffeechat coffeechat);
}
