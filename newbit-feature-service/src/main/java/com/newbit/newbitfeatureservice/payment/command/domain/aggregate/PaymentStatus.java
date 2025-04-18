package com.newbit.newbitfeatureservice.payment.command.domain.aggregate;

public enum PaymentStatus {
    // 초기 상태
    READY,            // 결제 생성 초기 상태, 인증 전
    
    // 진행 상태
    IN_PROGRESS,      // 인증 완료, 승인 전
    WAITING_FOR_DEPOSIT, // 가상계좌 입금 대기
    
    // 완료 상태
    DONE,             // 승인 완료
    
    // 취소/환불 상태
    CANCELED,         // 전액 취소됨
    PARTIAL_CANCELED, // 부분 취소됨
    
    // 실패 상태
    ABORTED,          // 결제 승인 실패
    EXPIRED           // 결제 유효시간 만료
} 