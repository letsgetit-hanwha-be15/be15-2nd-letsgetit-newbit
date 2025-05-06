export const paymentApi = {
  endpoints: {
    // 결제 관련
    order: "/feature/payments/order", // 주문 정보 저장
    confirm: "/feature/payments/confirm", // 결제 승인(토스)
    cancel: "/feature/payments/cancel", // 결제 취소(키 기반)
    status: "/feature/payments", // 결제 상태 조회(주문번호 기반)

    // 조회 관련
    query: "/feature/payments/query", // 결제 내역 조회(페이징)
    queryByUser: "/feature/payments/query/users", // 사용자별 결제 내역
    queryByStatus: "/feature/payments/query/status", // 상태별 결제 내역
    queryByUserAndStatus: "/feature/payments/query/users", // 사용자+상태별 결제 내역
    queryRefunds: "/feature/payments/query/refunds", // 환불 내역 있는 결제
    queryRefundsByUser: "/feature/payments/query/users", // 사용자별 환불 내역 있는 결제
    detail: "/feature/payments/query", // 결제 상세(결제ID)
    search: "/feature/payments/query/search", // 주문번호/결제키로 결제 조회

    // 환불 관련
    refund: "/feature/refunds/payments", // 결제 전체 취소
    refundPartial: "/feature/refunds/payments", // 결제 부분 취소
    refundVirtual: "/feature/refunds/payments", // 가상계좌 결제 취소
    refundList: "/feature/refunds/payments", // 결제별 환불 내역
    refundDetail: "/feature/refunds", // 환불 상세

    // 환불 조회
    refundQuery: "/feature/refunds/query", // 전체 환불 내역
    refundQueryByPayment: "/feature/refunds/query/payments", // 결제별 환불 내역
    refundQueryByUser: "/feature/refunds/query/users", // 사용자별 환불 내역
    refundQueryPartial: "/feature/refunds/query/payments", // 부분 환불 내역
    refundQueryDetail: "/feature/refunds/query", // 환불 상세
  },
};
