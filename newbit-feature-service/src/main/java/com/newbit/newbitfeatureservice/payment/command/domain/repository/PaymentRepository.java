package com.newbit.newbitfeatureservice.payment.command.domain.repository;

import com.newbit.newbitfeatureservice.payment.command.domain.aggregate.Payment;

import java.util.Optional;
import java.util.List;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findById(Long id);
    Optional<Payment> findByPaymentKey(String paymentKey);
    Optional<Payment> findByOrderId(String orderId);
    boolean existsById(Long id);
    List<Payment> findAll();
    void deleteById(Long id);
} 