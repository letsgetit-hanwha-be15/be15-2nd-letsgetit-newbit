import { paymentApi } from "@/api/payment";
import api from "@/api/axios";

export const paymentService = {
  verifyPayment: async (paymentKey, orderId, amount) => {
    return api.post(paymentApi.endpoints.verify, {
      paymentKey,
      orderId,
      amount,
    });
  },

  getPaymentStatus: async (orderId) => {
    return api.get(`${paymentApi.endpoints.status}/${orderId}`);
  },

  cancelPayment: async (paymentKey, reason) => {
    return api.post(paymentApi.endpoints.cancel, {
      paymentKey,
      reason,
    });
  },

  // 결제 승인(토스)
  confirmPayment: async (paymentKey, orderId, amount) => {
    return api.post(paymentApi.endpoints.confirm, {
      paymentKey,
      orderId,
      amount,
    });
  },

  // 전체 결제 내역 조회(페이징)
  getAllPayments: async (params) => {
    return api.get(paymentApi.endpoints.query, { params });
  },

  // 사용자별 결제 내역 조회
  getPaymentsByUserId: async (userId, params) => {
    return api.get(`${paymentApi.endpoints.queryByUser}/${userId}`, { params });
  },

  // 결제 상태별 내역 조회
  getPaymentsByStatus: async (status, params) => {
    return api.get(paymentApi.endpoints.queryByStatus, {
      params: { status, ...params },
    });
  },

  // 사용자별 결제 상태별 내역 조회
  getPaymentsByUserIdAndStatus: async (userId, statuses, params) => {
    return api.get(
      `${paymentApi.endpoints.queryByUserAndStatus}/${userId}/status`,
      { params: { statuses, ...params } }
    );
  },

  // 환불 내역이 있는 결제 조회
  getPaymentsWithRefunds: async (params) => {
    return api.get(paymentApi.endpoints.queryRefunds, { params });
  },

  // 사용자별 환불 내역이 있는 결제 조회
  getPaymentsWithRefundsByUserId: async (userId, params) => {
    return api.get(
      `${paymentApi.endpoints.queryRefundsByUser}/${userId}/refunds`,
      { params }
    );
  },

  // 결제 상세 정보 조회(결제ID)
  getPaymentDetail: async (paymentId) => {
    return api.get(`${paymentApi.endpoints.detail}/${paymentId}`);
  },

  // 주문번호/결제키로 결제 정보 조회
  searchPayment: async (orderId, paymentKey) => {
    return api.get(paymentApi.endpoints.search, {
      params: { orderId, paymentKey },
    });
  },

  // 결제 전체 취소(결제ID)
  refundPayment: async (paymentId, reason) => {
    return api.post(`${paymentApi.endpoints.refund}/${paymentId}`, { reason });
  },

  // 결제 부분 취소(결제ID)
  refundPaymentPartial: async (paymentId, cancelAmount, reason) => {
    return api.post(
      `${paymentApi.endpoints.refundPartial}/${paymentId}/partial`,
      { cancelAmount, reason }
    );
  },

  // 가상계좌 결제 취소(결제ID)
  refundVirtualAccountPayment: async (paymentId, request) => {
    return api.post(
      `${paymentApi.endpoints.refundVirtual}/${paymentId}/virtual`,
      request
    );
  },

  // 결제별 환불 내역 조회(결제ID)
  getRefundsByPaymentId: async (paymentId) => {
    return api.get(`${paymentApi.endpoints.refundList}/${paymentId}`);
  },

  // 환불 상세 조회(환불ID)
  getRefundDetail: async (refundId) => {
    return api.get(`${paymentApi.endpoints.refundDetail}/${refundId}`);
  },

  // 전체 환불 내역 조회(페이징)
  getAllRefunds: async (params) => {
    return api.get(paymentApi.endpoints.refundQuery, { params });
  },

  // 결제별 환불 내역 조회(페이징)
  getRefundsByPaymentIdPaged: async (paymentId, params) => {
    return api.get(
      `${paymentApi.endpoints.refundQueryByPayment}/${paymentId}`,
      { params }
    );
  },

  // 사용자별 환불 내역 조회(페이징)
  getRefundsByUserId: async (userId, params) => {
    return api.get(`${paymentApi.endpoints.refundQueryByUser}/${userId}`, {
      params,
    });
  },

  // 부분 환불 내역 조회(결제ID, 페이징)
  getPartialRefundsByPaymentId: async (paymentId, params) => {
    return api.get(
      `${paymentApi.endpoints.refundQueryPartial}/${paymentId}/partial`,
      { params }
    );
  },

  // 환불 상세 정보 조회(환불ID)
  getRefundQueryDetail: async (refundId) => {
    return api.get(`${paymentApi.endpoints.refundQueryDetail}/${refundId}`);
  },
};
