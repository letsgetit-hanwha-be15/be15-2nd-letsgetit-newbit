package com.newbit.newbitfeatureservice.payment.query.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.newbit.newbitfeatureservice.payment.command.domain.aggregate.Payment;
import com.newbit.newbitfeatureservice.payment.command.domain.aggregate.PaymentStatus;

public interface PaymentQueryRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT p FROM Payment p WHERE p.userId = :userId")
    Page<Payment> findByUserId(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT p FROM Payment p WHERE p.userId = :userId AND p.paymentStatus IN :statuses")
    Page<Payment> findByUserIdAndStatusIn(
            @Param("userId") Long userId, 
            @Param("statuses") List<PaymentStatus> statuses,
            Pageable pageable);
    
    @Query("SELECT p FROM Payment p WHERE p.orderId = :orderId OR p.paymentKey = :paymentKey")
    Payment findByOrderIdOrPaymentKey(
            @Param("orderId") String orderId, 
            @Param("paymentKey") String paymentKey);
    
    @Query("SELECT DISTINCT p FROM Payment p LEFT JOIN Refund r ON p.paymentId = r.payment.paymentId " +
           "WHERE r.refundId IS NOT NULL")
    Page<Payment> findAllWithRefunds(Pageable pageable);
    
    @Query("SELECT DISTINCT p FROM Payment p LEFT JOIN Refund r ON p.paymentId = r.payment.paymentId " +
           "WHERE p.userId = :userId AND r.refundId IS NOT NULL")
    Page<Payment> findByUserIdWithRefunds(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT COUNT(r) FROM Refund r WHERE r.payment.paymentId = :paymentId")
    Long countRefundsByPaymentId(@Param("paymentId") Long paymentId);
} 