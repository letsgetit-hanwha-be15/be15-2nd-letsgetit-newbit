package com.newbit.coffeechat.command.infrastructure;

import com.newbit.coffeechat.command.domain.aggregate.Coffeechat;
import com.newbit.coffeechat.command.domain.repository.CoffeechatRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaCoffeechatRepository extends CoffeechatRepository, JpaRepository<Coffeechat, Long> {
}
