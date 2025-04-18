package com.newbit.newbitfeatureservice.payment.command.domain.repository;

import com.newbit.newbitfeatureservice.payment.command.domain.aggregate.Payment;
import com.newbit.newbitfeatureservice.payment.command.domain.aggregate.Refund;

import java.util.List;
import java.util.Optional;

public interface RefundRepository {
    Refund save(Refund refund);
    Optional<Refund> findById(Long id);
    List<Refund> findByPayment(Payment payment);
    List<Refund> findByPaymentPaymentId(Long paymentId);
    List<Refund> findAll();
    void deleteById(Long id);
} 