package com.newbit.common.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public enum ErrorCode {
    // 상품 관련 오류
    PRODUCT_NOT_FOUND("10001", "해당 상품을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // 파일 관련 오류
    FILE_SAVE_ERROR("20001", "파일 저장에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_DELETE_ERROR("20002", "파일 삭제에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    // 입력 값 검증 오류
    VALIDATION_ERROR("40001", "입력 값 검증 오류입니다.", HttpStatus.BAD_REQUEST),

    // 그 외 기타 오류
    INTERNAL_SERVER_ERROR("50000", "내부 서버 오류입니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    /*--------------- 회원 오류 ------------------*/
    ALREADY_REGISTERED_EMAIL("20001", "이미 가입한 아이디입니다.", HttpStatus.CONFLICT),
    FIND_EMAIL_BY_NAME_AND_PHONE_ERROR("20002", "이름 혹은 핸드폰번호를 잘못 입력하셨습니다.", HttpStatus.BAD_REQUEST),
    MAIL_SEND_FAIL("20003", "메일 전송에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_NOT_FOUND("20004", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    /*--------------- 커피챗 오류 ------------------*/
    COFFEECHAT_NOT_FOUND("70001", "해당 커피챗을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    REQUEST_TIME_NOT_FOUND("70002", "해당 커피챗 시간 요청내역을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    COFFEECHAT_ALREADY_EXIST("70003", "해당 멘토와의 커피챗이 이미 존재합니다.", HttpStatus.CONFLICT),

    /*--------------- 구매 오류 ------------------*/
    COLUMN_ALREADY_PURCHASED("60000", "이미 구매한 칼럼입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INSUFFICIENT_DIAMOND("60001", "보유한 다이아가 부족합니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    COLUMN_FREE_CANNOT_PURCHASE("60002", "무료 칼럼은 구매할 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    COLUMN_NOT_FOUND("60003", "해당 칼럼을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    COLUMN_NOT_PURCHASED("60004", "칼럼을 구매한 사용자만 조회할 수 있습니다.", HttpStatus.FORBIDDEN),
    COFFEECHAT_NOT_PURCHASABLE("60005", "커피챗이 구매할 수 없는 상태입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    ALREADY_MENTOR("60005", "이미 멘토인 회원입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INSUFFICIENT_POINT("60006", "보유한 포인트가 부족합니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_PURCHASE_TYPE("60007", "알수없는 재화 타입", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
