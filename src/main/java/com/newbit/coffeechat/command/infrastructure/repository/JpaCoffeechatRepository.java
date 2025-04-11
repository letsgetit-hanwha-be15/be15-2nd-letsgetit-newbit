package com.newbit.coffeechat.command.infrastructure.repository;

import com.newbit.coffeechat.command.domain.repository.CoffeechatRepository;
import com.newbit.coffeechat.command.domain.aggregate.Coffeechat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaCoffeechatRepository extends CoffeechatRepository, JpaRepository<Coffeechat, Long> {
}
