import { ref } from "vue";
import { getPaymentConfig } from "@/config/payment.config";
import { loadTossPayments } from "@tosspayments/tosspayments-sdk";
import { ANONYMOUS } from "@tosspayments/tosspayments-sdk";

export function usePayment({ onSuccess, onError }) {
  const config = getPaymentConfig();
  const clientKey = ref(config.toss.currentClientKey);
  const tossPayments = ref(null);
  const isReady = ref(false);
  const isLoading = ref(false);
  const paymentWidget = ref(null);

  const initPayment = async () => {
    if (isLoading.value) return;

    try {
      isLoading.value = true;

      tossPayments.value = await loadTossPayments(clientKey.value);
      isReady.value = true;
    } catch (error) {
      onError?.(new Error("결제 시스템 초기화에 실패했습니다."));
    } finally {
      isLoading.value = false;
    }
  };

  const validatePaymentData = (paymentData) => {
    const required = ["amount", "orderId", "orderName"];
    const missing = required.filter((field) => !paymentData[field]);

    if (missing.length > 0) {
      throw new Error(`필수 결제 정보가 누락되었습니다: ${missing.join(", ")}`);
    }
  };

  const requestPayment = async (paymentData) => {
    if (!isReady.value) {
      throw new Error(
        "결제 시스템이 초기화되지 않았습니다. initPayment를 먼저 호출해주세요."
      );
    }

    try {
      validatePaymentData(paymentData);

      const response = await tossPayments.value.requestPayment({
        method: "CARD",
        ...paymentData,
        successUrl: `${window.location.origin}${config.urls.success}`,
        failUrl: `${window.location.origin}${config.urls.fail}`,
        windowTarget: "iframe",
      });

      onSuccess?.(response);
    } catch (error) {
      onError?.(error);
    }
  };

  const initPaymentWidget = async (customerKey = ANONYMOUS) => {
    if (!isReady.value) {
      throw new Error(
        "결제 시스템이 초기화되지 않았습니다. initPayment를 먼저 호출해주세요."
      );
    }

    try {
      paymentWidget.value = tossPayments.value.widgets({ customerKey });
      return paymentWidget.value;
    } catch (error) {
      onError?.(error);
      return null;
    }
  };

  const setPaymentAmount = async (amount) => {
    if (!paymentWidget.value) {
      throw new Error("결제 위젯이 초기화되지 않았습니다.");
    }

    try {
      await paymentWidget.value.setAmount({
        currency: "KRW",
        value: amount,
      });
    } catch (error) {
      onError?.(error);
    }
  };

  const renderPaymentMethods = async (selector, variantKey = "DEFAULT") => {
    if (!paymentWidget.value) {
      throw new Error("결제 위젯이 초기화되지 않았습니다.");
    }

    try {
      return await paymentWidget.value.renderPaymentMethods({
        selector,
        variantKey,
      });
    } catch (error) {
      onError?.(error);
      return null;
    }
  };

  const requestPaymentWithWidget = async (paymentData) => {
    if (!paymentWidget.value) {
      throw new Error("결제 위젯이 초기화되지 않았습니다.");
    }

    try {
      validatePaymentData(paymentData);

      await paymentWidget.value.requestPayment({
        orderId: paymentData.orderId,
        orderName: paymentData.orderName,
        customerEmail: paymentData.customerEmail,
        customerName: paymentData.customerName,
        successUrl: `${window.location.origin}${config.urls.success}`,
        failUrl: `${window.location.origin}${config.urls.fail}`,
      });
    } catch (error) {
      onError?.(error);
    }
  };

  return {
    isReady,
    isLoading,
    initPayment,
    requestPayment,
    initPaymentWidget,
    setPaymentAmount,
    renderPaymentMethods,
    requestPaymentWithWidget,
  };
}
