package com.newbit.newbitfeatureservice.payment.query.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.newbit.newbitfeatureservice.payment.command.domain.aggregate.Refund;

public interface RefundQueryRepository extends JpaRepository<Refund, Long> {

    @Query("SELECT r FROM Refund r WHERE r.payment.paymentId = :paymentId")
    Page<Refund> findByPaymentId(@Param("paymentId") Long paymentId, Pageable pageable);
    
    @Query("SELECT r FROM Refund r JOIN r.payment p WHERE p.userId = :userId")
    Page<Refund> findByUserId(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT r FROM Refund r WHERE r.payment.paymentId = :paymentId AND r.isPartialRefund = true")
    Page<Refund> findPartialRefundsByPaymentId(@Param("paymentId") Long paymentId, Pageable pageable);
    
    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM Refund r WHERE r.payment.paymentId = :paymentId")
    Long getTotalRefundAmountByPaymentId(@Param("paymentId") Long paymentId);
} 