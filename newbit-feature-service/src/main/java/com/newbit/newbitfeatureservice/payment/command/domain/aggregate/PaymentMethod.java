package com.newbit.newbitfeatureservice.payment.command.domain.aggregate;

public enum PaymentMethod {
    CARD,               // 카드 결제
    VIRTUAL_ACCOUNT,    // 가상계좌
    TRANSFER,           // 계좌이체
    MOBILE_PHONE,       // 휴대폰 결제
    GIFT_CERTIFICATE,   // 상품권
    EASY_PAY            // 간편결제
} 