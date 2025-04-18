package com.newbit.newbitfeatureservice.coffeechat.command.infrastructure.repository;

import com.newbit.newbitfeatureservice.coffeechat.command.domain.aggregate.RequestTime;
import com.newbit.newbitfeatureservice.coffeechat.command.domain.repository.RequestTimeRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaRequestTimeRepository  extends RequestTimeRepository, JpaRepository<RequestTime, Long> {
}
