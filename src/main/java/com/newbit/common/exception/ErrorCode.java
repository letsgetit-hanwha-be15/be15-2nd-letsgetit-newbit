package com.newbit.common.exception;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public enum ErrorCode {
    // 상품 관련 오류
    PRODUCT_NOT_FOUND("10001", "해당 상품을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PRODUCT_INACTIVE("10002", "비활성화된 상품입니다.", HttpStatus.BAD_REQUEST),
    PRODUCT_PRICE_MISMATCH("10003", "상품 가격이 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    PRODUCT_INVALID_DIAMOND_AMOUNT("10004", "올바르지 않은 다이아몬드 수량입니다.", HttpStatus.BAD_REQUEST),
    PRODUCT_NAME_DUPLICATE("10005", "이미 존재하는 상품명입니다.", HttpStatus.CONFLICT),
    PRODUCT_ORDER_INVALID("10006", "주문 정보를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    PRODUCT_PURCHASE_UNAUTHORIZED("10007", "상품 구매 권한이 없습니다.", HttpStatus.FORBIDDEN),

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
    INVALID_PASSWORD_FORMAT("20009", "최소 8자, 영문자, 숫자, 특수문자 포함해야합니다.", HttpStatus.BAD_REQUEST),
    INVALID_CURRENT_PASSWORD("20010", "비밀번호가 올바르지 않습니다.", HttpStatus.BAD_REQUEST),

    /*--------------- 커피챗 오류 ------------------*/
    // 커피챗
    COFFEECHAT_NOT_FOUND("70001", "해당 커피챗을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    REQUEST_TIME_NOT_FOUND("70002", "해당 커피챗 시간 요청내역을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    COFFEECHAT_ALREADY_EXIST("70003", "해당 멘토와의 커피챗이 이미 존재합니다.", HttpStatus.CONFLICT),
    REQUEST_DATE_IN_PAST("70006", "시작 날짜가 오늘보다 이전입니다.", HttpStatus.UNPROCESSABLE_ENTITY),
    COFFEECHAT_CANCEL_NOT_ALLOWED("70007", "본인의 커피챗만 취소 가능합니다.", HttpStatus.FORBIDDEN),
    COFFEECHAT_NOT_REFUNDABLE("70008", "커피챗이 환불 가능한 상태가 아닙니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_COFFEECHAT_STATUS_CANCEL("70009", "커피챗이 취소 가능한 상태가 아닙니다.", HttpStatus.BAD_REQUEST),

    //리뷰
    INVALID_COFFEECHAT_STATUS_COMPLETE("70010", "커피챗이 리뷰를 장성할 수 있는 상태가 아닙니다.", HttpStatus.BAD_REQUEST),
    REVIEW_ALREADY_EXIST("70011", "해당 커피챗에 대한 리뷰가 이미 존재합니다.", HttpStatus.CONFLICT),
    REVIEW_CREATE_NOT_ALLOWED("70012", "본인이 멘티인 커피챗만 리뷰 작성 가능합니다.", HttpStatus.FORBIDDEN),
    REVIEW_CANCEL_NOT_ALLOWED("70013", "본인의 리뷰만 취소 가능합니다.", HttpStatus.FORBIDDEN),
    REVIEW_NOT_FOUND("70014", "해당 리뷰를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    /*--------------- 구매 오류 ------------------*/
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

    /*--------------- 칼럼/시리즈 오류 ------------------*/
    // 칼럼 오류
    COLUMN_NOT_FOUND("60003", "해당 칼럼을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    COLUMN_NOT_OWNED("60011", "해당 칼럼에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN),
    COLUMN_ALREADY_IN_SERIES("60012", "해당 칼럼은 이미 다른 시리즈에 속해있습니다.", HttpStatus.BAD_REQUEST),
    COLUMN_REQUEST_NOT_FOUND("60013", "해당 칼럼 요청을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_REQUEST_TYPE("60014", "잘못된 요청 타입 입니다.", HttpStatus.BAD_REQUEST),
    // 시리즈 오류
    SERIES_CREATION_REQUIRES_COLUMNS("300000", "시리즈는 최소 1개 이상의 칼럼으로 생성되어야 합니다.", HttpStatus.BAD_REQUEST),
    SERIES_NOT_FOUND("300001", "해당 시리즈를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    /*--------------- JWT 토큰 오류 ------------------*/
    JWT_INVALID("80001", "유효하지 않은 JWT 토큰입니다.", HttpStatus.UNAUTHORIZED),
    JWT_EXPIRED("80002", "만료된 JWT 토큰입니다.", HttpStatus.UNAUTHORIZED),
    JWT_UNSUPPORTED("80003", "지원하지 않는 JWT 토큰입니다.", HttpStatus.BAD_REQUEST),
    JWT_CLAIMS_EMPTY("80004", "JWT 클레임이 비어 있습니다.", HttpStatus.BAD_REQUEST),

    /*----------------알림-------------------------*/
    NOTIFICATION_TYPE_NOT_FOUND("90001", "잘못된 알림 유형 입니다.", HttpStatus.NOT_FOUND),
    NOTIFICATION_NOT_FOUND("90002", "알림을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    UNAUTHORIZED_ACCESS("90003", "인증되지 않은 접근", HttpStatus.UNAUTHORIZED),

    /*----------------좋아요 관련-------------------*/
    POST_LIKE_NOT_FOUND("100001", "해당 게시글 좋아요를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    COLUMN_LIKE_NOT_FOUND("100002", "해당 칼럼 좋아요를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    LIKE_PROCESSING_ERROR("100003", "좋아요 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    /*----------------구독 관련-------------------*/
    SUBSCRIPTION_NOT_FOUND("110001", "해당 구독 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    SUBSCRIPTION_PROCESSING_ERROR("110002", "구독 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    /*---------------- 게시글 -------------------------*/
    POST_NOT_FOUND("150000", "해당 게시글이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    UNAUTHORIZED_TO_UPDATE_POST("150001", "게시글은 작성자만 수정할 수 있습니다.", HttpStatus.FORBIDDEN),
    ONLY_USER_CAN_CREATE_POST("150002", "게시글은 일반 사용자만 작성할 수 있습니다.", HttpStatus.FORBIDDEN),
    UNAUTHORIZED_TO_DELETE_POST("150003", "게시글은 작성자만 삭제할 수 있습니다.", HttpStatus.FORBIDDEN),
    ONLY_ADMIN_CAN_CREATE_NOTICE("150004", "공지사항은 관리자만 등록할 수 있습니다.", HttpStatus.FORBIDDEN),
    ONLY_ADMIN_CAN_UPDATE_NOTICE("150005", "공지사항은 관리자만 수정할 수 있습니다.", HttpStatus.FORBIDDEN),
    ONLY_ADMIN_CAN_DELETE_NOTICE("150006", "공지사항은 관리자만 삭제할 수 있습니다.", HttpStatus.FORBIDDEN),
    NOT_A_NOTICE("150007", "해당 게시글은 공지사항이 아닙니다.", HttpStatus.BAD_REQUEST),

    /*---------------- 댓글 -------------------------*/
    COMMENT_NOT_FOUND("160000", "해당 댓글이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    UNAUTHORIZED_TO_DELETE_COMMENT("160001", "댓글은 작성자만 삭제할 수 있습니다.", HttpStatus.FORBIDDEN),
    UNAUTHORIZED_TO_CREATE_COMMENT("160002", "댓글 작성은 회원만 가능합니다.", HttpStatus.FORBIDDEN),
    COMMENT_POST_MISMATCH("160003", "해당 댓글은 게시글과 매칭되지 않습니다.", HttpStatus.BAD_REQUEST),

    /*---------------- 정산 -------------------------*/
    SETTLEMENT_NOT_FOUND("110000", "정산 내역을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  
    /*--------------- 결제 오류 ------------------*/
    PAYMENT_NOT_FOUND("81001", "결제 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PAYMENT_AMOUNT_MISMATCH("81002", "결제 금액이 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    PAYMENT_ALREADY_PROCESSED("81003", "이미 처리된 결제입니다.", HttpStatus.BAD_REQUEST),
    PAYMENT_APPROVAL_FAILED("81004", "결제 승인에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    PAYMENT_REFUND_FAILED("81005", "결제 환불에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    PAYMENT_NOT_REFUNDABLE("81006", "환불 가능한 결제가 아닙니다.", HttpStatus.BAD_REQUEST),
    PAYMENT_DETAILS_NOT_FOUND("81007", "결제 상세 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    VIRTUAL_ACCOUNT_ISSUANCE_FAILED("81008", "가상계좌 발급에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    PAYMENT_REFUND_NOT_FOUND("81012", "환불 내역을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    /*--------------- 추가 결제 에러 코드 ------------------*/
    PAYMENT_CANNOT_BE_CANCELLED("81013", "결제를 취소할 수 없는 상태입니다.", HttpStatus.BAD_REQUEST),
    PAYMENT_NOT_PARTIAL_CANCELABLE("81014", "부분 취소가 불가능한 결제입니다.", HttpStatus.BAD_REQUEST),
    REFUND_AMOUNT_EXCEEDS_BALANCE("81015", "환불 금액이 잔액을 초과합니다.", HttpStatus.BAD_REQUEST),
    PAYMENT_CANCEL_FAILED("81016", "결제 취소 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
