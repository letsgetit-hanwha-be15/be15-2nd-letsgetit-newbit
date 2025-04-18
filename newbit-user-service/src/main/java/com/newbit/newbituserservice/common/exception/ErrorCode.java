package com.newbit.newbituserservice.common.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public enum ErrorCode {

    /*--------------- 회원 ------------------*/
    //회원
    ALREADY_REGISTERED_EMAIL("10001", "이미 가입한 아이디입니다.", HttpStatus.CONFLICT),
    FIND_EMAIL_BY_NAME_AND_PHONE_ERROR("10002", "이름 혹은 핸드폰번호를 잘못 입력하셨습니다.", HttpStatus.BAD_REQUEST),
    MAIL_SEND_FAIL("10003", "메일 전송에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_NOT_FOUND("10004", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    MENTOR_NOT_FOUND("10005", "해당 멘토를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    USER_INFO_NOT_FOUND("10006", "정보 조회에 실패했습니다.", HttpStatus.NOT_FOUND),
    ALREADY_REGISTERED_PHONENUMBER("10007", "이미 존재하는 핸드폰 번호입니다.", HttpStatus.BAD_REQUEST),
    ALREADY_REGISTERED_NICKNAME("10008", "이미 존재하는 닉네임입니다.", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD_FORMAT("10009", "최소 8자, 영문자, 숫자, 특수문자 포함해야합니다.", HttpStatus.BAD_REQUEST),
    INVALID_CURRENT_PASSWORD("10010", "비밀번호가 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    //인증
    JWT_INVALID("10011", "유효하지 않은 JWT 토큰입니다.", HttpStatus.UNAUTHORIZED),
    JWT_EXPIRED("10012", "만료된 JWT 토큰입니다.", HttpStatus.UNAUTHORIZED),
    JWT_UNSUPPORTED("10013", "지원하지 않는 JWT 토큰입니다.", HttpStatus.BAD_REQUEST),
    JWT_CLAIMS_EMPTY("10014", "JWT 클레임이 비어 있습니다.", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR("10015", "내부 서버 오류입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    // 입력 값 검증 오류
    VALIDATION_ERROR("11001", "입력 값 검증 오류입니다.", HttpStatus.BAD_REQUEST),

    /*--------------- 게시판 ------------------*/
    //게시글
    POST_NOT_FOUND("20001", "해당 게시글이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    UNAUTHORIZED_TO_UPDATE_POST("20002", "게시글은 작성자만 수정할 수 있습니다.", HttpStatus.FORBIDDEN),
    ONLY_USER_CAN_CREATE_POST("20003", "게시글은 회원만 작성할 수 있습니다.", HttpStatus.FORBIDDEN),
    UNAUTHORIZED_TO_DELETE_POST("20004", "게시글은 작성자만 삭제할 수 있습니다.", HttpStatus.FORBIDDEN),
    ONLY_ADMIN_CAN_CREATE_NOTICE("20005", "공지사항은 관리자만 등록할 수 있습니다.", HttpStatus.FORBIDDEN),
    ONLY_ADMIN_CAN_UPDATE_NOTICE("20006", "공지사항은 관리자만 수정할 수 있습니다.", HttpStatus.FORBIDDEN),
    ONLY_ADMIN_CAN_DELETE_NOTICE("20007", "공지사항은 관리자만 삭제할 수 있습니다.", HttpStatus.FORBIDDEN),
    NOT_A_NOTICE("20008", "해당 게시글은 공지사항이 아닙니다.", HttpStatus.BAD_REQUEST),
    POST_LIKE_NOT_FOUND("20009", "해당 게시글 좋아요를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    LIKE_PROCESSING_ERROR("20010", "좋아요 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_SAVE_ERROR("20011", "파일 저장에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_DELETE_ERROR("20012", "파일 삭제에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    //댓글
    COMMENT_NOT_FOUND("20013", "해당 댓글이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    UNAUTHORIZED_TO_DELETE_COMMENT("20014", "댓글은 작성자만 삭제할 수 있습니다.", HttpStatus.FORBIDDEN),
    UNAUTHORIZED_TO_CREATE_COMMENT("20015", "댓글 작성은 회원만 가능합니다.", HttpStatus.FORBIDDEN),
    COMMENT_POST_MISMATCH("20016", "해당 댓글은 게시글과 매칭되지 않습니다.", HttpStatus.BAD_REQUEST),

    /*--------------- 칼럼 ------------------*/
    // 칼럼
    COLUMN_NOT_FOUND("21001", "해당 칼럼을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    COLUMN_NOT_OWNED("21002", "해당 칼럼에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN),
    COLUMN_ALREADY_IN_SERIES("21003", "해당 칼럼은 이미 다른 시리즈에 속해있습니다.", HttpStatus.BAD_REQUEST),
    COLUMN_REQUEST_NOT_FOUND("21004", "해당 칼럼 요청을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_REQUEST_TYPE("21005", "잘못된 요청 타입 입니다.", HttpStatus.BAD_REQUEST),
    COLUMN_LIKE_NOT_FOUND("21006", "해당 칼럼 좋아요를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    // 시리즈
    SERIES_CREATION_REQUIRES_COLUMNS("21007", "시리즈는 최소 1개 이상의 칼럼으로 생성되어야 합니다.", HttpStatus.BAD_REQUEST),
    SERIES_NOT_FOUND("21008", "해당 시리즈를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    SUBSCRIPTION_NOT_FOUND("21009", "해당 구독 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    SUBSCRIPTION_PROCESSING_ERROR("21010", "구독 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    /*--------------- 커피챗 ------------------*/
    // 커피챗
    COFFEECHAT_NOT_FOUND("30001", "해당 커피챗을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    REQUEST_TIME_NOT_FOUND("30002", "해당 커피챗 시간 요청내역을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    COFFEECHAT_ALREADY_EXIST("30003", "해당 멘토와의 커피챗이 이미 존재합니다.", HttpStatus.CONFLICT),
    REQUEST_DATE_IN_PAST("30004", "시작 날짜가 오늘보다 이전입니다.", HttpStatus.UNPROCESSABLE_ENTITY),
    COFFEECHAT_CANCEL_NOT_ALLOWED("30005", "본인의 커피챗만 취소 가능합니다.", HttpStatus.FORBIDDEN),
    COFFEECHAT_NOT_REFUNDABLE("30006", "커피챗이 환불 가능한 상태가 아닙니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_COFFEECHAT_STATUS_CANCEL("30007", "커피챗이 취소 가능한 상태가 아닙니다.", HttpStatus.BAD_REQUEST),
    //리뷰
    INVALID_COFFEECHAT_STATUS_COMPLETE("30008", "커피챗이 리뷰를 장성할 수 있는 상태가 아닙니다.", HttpStatus.BAD_REQUEST),
    REVIEW_ALREADY_EXIST("30009", "해당 커피챗에 대한 리뷰가 이미 존재합니다.", HttpStatus.CONFLICT),
    REVIEW_CREATE_NOT_ALLOWED("30010", "본인이 멘티인 커피챗만 리뷰 작성 가능합니다.", HttpStatus.FORBIDDEN),
    REVIEW_CANCEL_NOT_ALLOWED("30011", "본인의 리뷰만 취소 가능합니다.", HttpStatus.FORBIDDEN),
    REVIEW_NOT_FOUND("30012", "해당 리뷰를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    /*--------------- 커피레터 ------------------*/
    COFFEELETTER_NOT_FOUND("31001", "해당 커피레터를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    COFFEELETTER_ROOM_NOT_FOUND("31002", "해당 룸을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    COFFEELETTER_INVALID_ACCESS("31003", "커피레터에 접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
    COFFEELETTER_ALREADY_EXIST("31004", "커피레터가 이미 존재합니다.", HttpStatus.CONFLICT),

    /*--------------- 구매 ------------------*/
    COLUMN_ALREADY_PURCHASED("60001", "이미 구매한 칼럼입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INSUFFICIENT_DIAMOND("60002", "보유한 다이아가 부족합니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    COLUMN_FREE_CANNOT_PURCHASE("60003", "무료 칼럼은 구매할 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    COLUMN_NOT_PURCHASED("60004", "칼럼을 구매한 사용자만 조회할 수 있습니다.", HttpStatus.FORBIDDEN),
    COFFEECHAT_NOT_PURCHASABLE("60005", "커피챗이 구매할 수 없는 상태입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    ALREADY_MENTOR("60006", "이미 멘토인 회원입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INSUFFICIENT_POINT("60007", "보유한 포인트가 부족합니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_PURCHASE_TYPE("60008", "알수없는 재화 타입", HttpStatus.INTERNAL_SERVER_ERROR),
    POINT_TYPE_NOT_FOUND("60009", "포인트 유형이 잘못 되었습니다.", HttpStatus.NOT_FOUND),
    INVALID_TIP_AMOUNT("60010", "잘못된 팁 제공량 입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    COFFEECHAT_PURCHASE_NOT_ALLOWED("60011", "본인의 커피챗만 구매 가능합니다.", HttpStatus.FORBIDDEN),

    /*--------------- 결제 ------------------*/
    PAYMENT_NOT_FOUND("61001", "결제 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PAYMENT_AMOUNT_MISMATCH("61002", "결제 금액이 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    PAYMENT_ALREADY_PROCESSED("61003", "이미 처리된 결제입니다.", HttpStatus.BAD_REQUEST),
    PAYMENT_APPROVAL_FAILED("61004", "결제 승인에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    PAYMENT_REFUND_FAILED("61005", "결제 환불에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    PAYMENT_NOT_REFUNDABLE("61006", "환불 가능한 결제가 아닙니다.", HttpStatus.BAD_REQUEST),
    PAYMENT_DETAILS_NOT_FOUND("61007", "결제 상세 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    VIRTUAL_ACCOUNT_ISSUANCE_FAILED("61008", "가상계좌 발급에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    PAYMENT_REFUND_NOT_FOUND("61009", "환불 내역을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PAYMENT_CANNOT_BE_CANCELLED("61010", "결제를 취소할 수 없는 상태입니다.", HttpStatus.BAD_REQUEST),
    PAYMENT_NOT_PARTIAL_CANCELABLE("61011", "부분 취소가 불가능한 결제입니다.", HttpStatus.BAD_REQUEST),
    REFUND_AMOUNT_EXCEEDS_BALANCE("61012", "환불 금액이 잔액을 초과합니다.", HttpStatus.BAD_REQUEST),
    PAYMENT_CANCEL_FAILED("61013", "결제 취소 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    /*--------------- 정산 ------------------*/
    SETTLEMENT_NOT_FOUND("62001", "정산 내역을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    /*--------------- 상품 ------------------*/
    PRODUCT_NOT_FOUND("70001", "해당 상품을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PRODUCT_INACTIVE("70002", "비활성화된 상품입니다.", HttpStatus.BAD_REQUEST),
    PRODUCT_PRICE_MISMATCH("70003", "상품 가격이 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    PRODUCT_INVALID_DIAMOND_AMOUNT("70004", "올바르지 않은 다이아몬드 수량입니다.", HttpStatus.BAD_REQUEST),
    PRODUCT_NAME_DUPLICATE("70005", "이미 존재하는 상품명입니다.", HttpStatus.CONFLICT),
    PRODUCT_ORDER_INVALID("70006", "주문 정보를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    PRODUCT_PURCHASE_UNAUTHORIZED("70007", "상품 구매 권한이 없습니다.", HttpStatus.FORBIDDEN),

    /*--------------- 신고 ------------------*/
    REPORT_NOT_FOUND("80001", "해당 신고를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    REPORT_TYPE_NOT_FOUND("80002", "해당 신고 유형을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    REPORT_ALREADY_PROCESSED("80003", "이미 처리된 신고입니다.", HttpStatus.BAD_REQUEST),
    REPORT_CONTENT_NOT_FOUND("80004", "신고된 컨텐츠를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    SELF_REPORT_NOT_ALLOWED("80005", "자신의 컨텐츠는 신고할 수 없습니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_REPORT("80006", "이미 신고한 컨텐츠입니다.", HttpStatus.CONFLICT),
    UNAUTHORIZED_REPORT_ACCESS("80007", "신고 정보에 접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
    INVALID_REPORT_STATUS_CHANGE("80008", "유효하지 않은 신고 상태 변경입니다.", HttpStatus.BAD_REQUEST),
    MAXIMUM_REPORTS_REACHED("80009", "최대 신고 횟수를 초과했습니다.", HttpStatus.TOO_MANY_REQUESTS),
    REPORT_PROCESS_ERROR("80010", "신고 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    /*--------------- 알림 ------------------*/
    NOTIFICATION_TYPE_NOT_FOUND("90001", "잘못된 알림 유형 입니다.", HttpStatus.NOT_FOUND),
    NOTIFICATION_NOT_FOUND("90002", "알림을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    UNAUTHORIZED_ACCESS("90003", "인증되지 않은 접근", HttpStatus.UNAUTHORIZED);

    /*--------------- 기타 ------------------*/

    /*--------------- 추가 결제 에러 코드 ------------------*/

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
