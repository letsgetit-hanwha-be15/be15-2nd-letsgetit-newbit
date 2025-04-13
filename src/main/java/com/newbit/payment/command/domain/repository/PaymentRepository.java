package com.newbit.payment.command.domain.repository;

import com.newbit.payment.command.domain.aggregate.Payment;

import java.util.Optional;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findById(Long paymentId);
    Optional<Payment> findByPaymentKey(String paymentKey);
    Optional<Payment> findByOrderId(String orderId);
} 