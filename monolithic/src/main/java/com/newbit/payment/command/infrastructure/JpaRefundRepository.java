package com.newbit.payment.command.infrastructure;

import com.newbit.payment.command.domain.aggregate.Refund;
import com.newbit.payment.command.domain.repository.RefundRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaRefundRepository extends RefundRepository, JpaRepository<Refund, Long> {
}