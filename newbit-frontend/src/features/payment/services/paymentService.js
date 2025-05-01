import { paymentConfig } from "@/config/payment.config";
import { createApiClient } from "@/config/axios.config";

const { baseUrl, endpoints } = paymentConfig.api;
const apiClient = createApiClient(baseUrl);

export const paymentService = {
  verifyPayment: async (paymentKey, orderId, amount) => {
    return apiClient.post(endpoints.verify, {
      paymentKey,
      orderId,
      amount,
    });
  },

  getPaymentStatus: async (orderId) => {
    return apiClient.get(`${endpoints.status}/${orderId}`);
  },

  cancelPayment: async (paymentKey, reason) => {
    return apiClient.post(endpoints.cancel, {
      paymentKey,
      reason,
    });
  },
};
