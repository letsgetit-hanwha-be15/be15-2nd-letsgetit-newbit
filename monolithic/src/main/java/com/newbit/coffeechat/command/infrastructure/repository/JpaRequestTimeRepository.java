package com.newbit.coffeechat.command.infrastructure.repository;

import com.newbit.coffeechat.command.domain.aggregate.RequestTime;
import com.newbit.coffeechat.command.domain.repository.RequestTimeRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaRequestTimeRepository  extends RequestTimeRepository, JpaRepository<RequestTime, Long> {
}
