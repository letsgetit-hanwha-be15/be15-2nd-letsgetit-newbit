package com.newbit.common.exception;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public enum ErrorCode {
    PRODUCT_NOT_FOUND("10001", "해당 상품을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    FILE_SAVE_ERROR("20001", "파일 저장에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_DELETE_ERROR("20002", "파일 삭제에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    VALIDATION_ERROR("40001", "입력 값 검증 오류입니다.", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR("50000", "내부 서버 오류입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    ALREADY_REGISTERED_EMAIL("20001", "이미 가입한 아이디입니다.", HttpStatus.CONFLICT),
    FIND_EMAIL_BY_NAME_AND_PHONE_ERROR("20002", "이름 혹은 핸드폰번호를 잘못 입력하셨습니다.", HttpStatus.BAD_REQUEST),
    MAIL_SEND_FAIL("20003", "메일 전송에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_NOT_FOUND("20004", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    MENTOR_NOT_FOUND("20005", "해당 멘토를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    USER_INFO_NOT_FOUND("20006", "정보 조회에 실패했습니다.", HttpStatus.NOT_FOUND),
    ALREADY_REGISTERED_PHONENUMBER("20007", "이미 존재하는 핸드폰 번호입니다.", HttpStatus.BAD_REQUEST),
    ALREADY_REGISTERED_NICKNAME("20008", "이미 존재하는 닉네임입니다.", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD_FORMAT("20009", "비밀번호 형식이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_CURRENT_PASSWORD("20010", "비밀번호가 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    COFFEECHAT_NOT_FOUND("70001", "해당 커피챗을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    REQUEST_TIME_NOT_FOUND("70002", "해당 커피챗 시간 요청내역을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    COFFEECHAT_ALREADY_EXIST("70003", "해당 멘토와의 커피챗이 이미 존재합니다.", HttpStatus.CONFLICT),
    REQUEST_DATE_IN_PAST("70006", "시작 날짜가 오늘보다 이전입니다.", HttpStatus.UNPROCESSABLE_ENTITY),
    COFFEECHAT_CANCEL_NOT_ALLOWED("70007", "본인의 커피챗만 취소 가능합니다.", HttpStatus.FORBIDDEN),
    COFFEECHAT_NOT_REFUNDABLE("70008", "커피챗이 환불 가능한 상태가 아닙니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_COFFEECHAT_STATUS("70009", "커피챗이 취소 가능한 상태가 아닙니다.", HttpStatus.BAD_REQUEST),
    COLUMN_ALREADY_PURCHASED("60000", "이미 구매한 칼럼입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INSUFFICIENT_DIAMOND("60001", "보유한 다이아가 부족합니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    COLUMN_FREE_CANNOT_PURCHASE("60002", "무료 칼럼은 구매할 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    COLUMN_NOT_PURCHASED("60004", "칼럼을 구매한 사용자만 조회할 수 있습니다.", HttpStatus.FORBIDDEN),
    COFFEECHAT_NOT_PURCHASABLE("60005", "커피챗이 구매할 수 없는 상태입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    ALREADY_MENTOR("60006", "이미 멘토인 회원입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INSUFFICIENT_POINT("60007", "보유한 포인트가 부족합니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_PURCHASE_TYPE("60008", "알수없는 재화 타입", HttpStatus.INTERNAL_SERVER_ERROR),
    POINT_TYPE_NOT_FOUND("60009", "포인트 유형이 잘못 되었습니다.", HttpStatus.NOT_FOUND),
    INVALID_TIP_AMOUNT("600010", "잘못된 팁 제공량 입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    COFFEECHAT_PURCHASE_NOT_ALLOWED("60011", "본인의 커피챗만 구매 가능합니다.", HttpStatus.FORBIDDEN),
    COLUMN_NOT_FOUND("60003", "해당 칼럼을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    COLUMN_NOT_OWNED("60011", "해당 칼럼에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN),
    COLUMN_ALREADY_IN_SERIES("60012", "해당 칼럼은 이미 다른 시리즈에 속해있습니다.", HttpStatus.BAD_REQUEST),
    SERIES_CREATION_REQUIRES_COLUMNS("300000", "시리즈는 최소 1개 이상의 칼럼으로 생성되어야 합니다.", HttpStatus.BAD_REQUEST),
    SERIES_NOT_FOUND("300001", "해당 시리즈를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    JWT_INVALID("80001", "유효하지 않은 JWT 토큰입니다.", HttpStatus.UNAUTHORIZED),
    JWT_EXPIRED("80002", "만료된 JWT 토큰입니다.", HttpStatus.UNAUTHORIZED),
    JWT_UNSUPPORTED("80003", "지원하지 않는 JWT 토큰입니다.", HttpStatus.BAD_REQUEST),
    JWT_CLAIMS_EMPTY("80004", "JWT 클레임이 비어 있습니다.", HttpStatus.BAD_REQUEST),
    NOTIFICATION_TYPE_NOT_FOUND("90001", "잘못된 알림 유형 입니다.", HttpStatus.NOT_FOUND),
    NOTIFICATION_NOT_FOUND("90002", "알림을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    UNAUTHORIZED_ACCESS("90003", "인증되지 않은 접근", HttpStatus.UNAUTHORIZED),
    POST_LIKE_NOT_FOUND("100001", "해당 게시글 좋아요를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    COLUMN_LIKE_NOT_FOUND("100002", "해당 칼럼 좋아요를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    LIKE_PROCESSING_ERROR("100003", "좋아요 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    /*----------------구독 관련-------------------*/
    SUBSCRIPTION_NOT_FOUND("110001", "해당 구독 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    SUBSCRIPTION_PROCESSING_ERROR("110002", "구독 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
