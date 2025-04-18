package com.newbit.payment.command.infrastructure;

import com.newbit.payment.command.domain.aggregate.Payment;
import com.newbit.payment.command.domain.repository.PaymentRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaPaymentRepository extends PaymentRepository, JpaRepository<Payment, Long> {
} 