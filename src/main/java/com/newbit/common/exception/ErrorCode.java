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
    MENTOR_NOT_FOUND("20005", "해당 멘토를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    USER_INFO_NOT_FOUND("20006", "정보 조회에 실패했습니다.", HttpStatus.NOT_FOUND),
    ALREADY_REGISTERED_PHONENUMBER("20007", "이미 존재하는 핸드폰 번호입니다.", HttpStatus.BAD_REQUEST),
    ALREADY_REGISTERED_NICKNAME("20008", "이미 존재하는 닉네임입니다.", HttpStatus.BAD_REQUEST),
    /*--------------- 커피챗 오류 ------------------*/
    COFFEECHAT_NOT_FOUND("70001", "해당 커피챗을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    REQUEST_TIME_NOT_FOUND("70002", "해당 커피챗 시간 요청내역을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    COFFEECHAT_ALREADY_EXIST("70003", "해당 멘토와의 커피챗이 이미 존재합니다.", HttpStatus.CONFLICT),
    INVALID_REQUEST_DATE("70004", "시작 날짜와 끝 날짜가 다릅니다.", HttpStatus.UNPROCESSABLE_ENTITY),
    INVALID_REQUEST_TIME("70005", "시작 시간과 끝 시간 구매 수량 x 30분 보다 작습니다.", HttpStatus.UNPROCESSABLE_ENTITY),
    REQUEST_DATE_IN_PAST("70006", "시작 날짜가 오늘보다 이전입니다.", HttpStatus.UNPROCESSABLE_ENTITY),
    COFFEECHAT_NOT_REFUNDABLE("70008", "커피챗이 환불 가능한 상태가 아닙니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    /*--------------- 구매 오류 ------------------*/
    COLUMN_ALREADY_PURCHASED("60000", "이미 구매한 칼럼입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INSUFFICIENT_DIAMOND("60001", "보유한 다이아가 부족합니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    COLUMN_FREE_CANNOT_PURCHASE("60002", "무료 칼럼은 구매할 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    COLUMN_NOT_FOUND("60003", "해당 칼럼을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    COLUMN_NOT_PURCHASED("60004", "칼럼을 구매한 사용자만 조회할 수 있습니다.", HttpStatus.FORBIDDEN),
    COFFEECHAT_NOT_PURCHASABLE("60005", "커피챗이 구매할 수 없는 상태입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    ALREADY_MENTOR("60005", "이미 멘토인 회원입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INSUFFICIENT_POINT("60006", "보유한 포인트가 부족합니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_PURCHASE_TYPE("60007", "알수없는 재화 타입", HttpStatus.INTERNAL_SERVER_ERROR),
    POINT_TYPE_NOT_FOUND("60008", "포인트 유형이 잘못 되었습니다.", HttpStatus.NOT_FOUND),
    INVALID_TIP_AMOUNT("60009", "잘못된 팁 제공량 입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    COFFEECHAT_PURCHASE_NOT_ALLOWED("60010", "본인의 커피챗만 구매 가능합니다.", HttpStatus.FORBIDDEN),


    /*--------------- JWT 토큰 오류 ------------------*/
    JWT_INVALID("80001", "유효하지 않은 JWT 토큰입니다.", HttpStatus.UNAUTHORIZED),
    JWT_EXPIRED("80002", "만료된 JWT 토큰입니다.", HttpStatus.UNAUTHORIZED),
    JWT_UNSUPPORTED("80003", "지원하지 않는 JWT 토큰입니다.", HttpStatus.BAD_REQUEST),
    JWT_CLAIMS_EMPTY("80004", "JWT 클레임이 비어 있습니다.", HttpStatus.BAD_REQUEST),

    /*----------------알림-------------------------*/
    NOTIFICATION_TYPE_NOT_FOUND("90001", "잘못된 알림 유형 입니다.", HttpStatus.NOT_FOUND);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
