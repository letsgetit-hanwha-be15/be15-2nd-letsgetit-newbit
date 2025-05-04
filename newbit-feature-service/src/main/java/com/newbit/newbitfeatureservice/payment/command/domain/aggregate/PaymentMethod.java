package com.newbit.newbitfeatureservice.payment.command.domain.aggregate;

public enum PaymentMethod {
    CARD,               // 카드 결제
    VIRTUAL_ACCOUNT,    // 가상계좌
    TRANSFER,           // 계좌이체
    MOBILE_PHONE,       // 휴대폰 결제
    GIFT_CERTIFICATE,   // 상품권
    EASY_PAY;            // 간편결제

    public static PaymentMethod fromTossMethod(String tossMethod) {
        if (tossMethod == null) return null;
        switch (tossMethod) {
            case "카드": return CARD;
            case "가상계좌": return VIRTUAL_ACCOUNT;
            case "계좌이체": return TRANSFER;
            case "휴대폰": return MOBILE_PHONE;
            case "상품권": return GIFT_CERTIFICATE;
            case "간편결제": return EASY_PAY;
            default: return null; // 혹은 CARD 등 기본값
        }
    }
} 