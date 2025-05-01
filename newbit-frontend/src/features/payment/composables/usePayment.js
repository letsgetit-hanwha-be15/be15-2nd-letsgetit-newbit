import { ref } from "vue";
import { getPaymentConfig } from "@/config/payment.config";

export function usePayment({ onSuccess, onError }) {
  const config = getPaymentConfig();
  const clientKey = ref(config.toss.currentClientKey);
  const tossPayments = ref(null);
  const isReady = ref(false);
  const isLoading = ref(false);

  const initPayment = async () => {
    if (isLoading.value) return;

    try {
      isLoading.value = true;

      if (!window.TossPayments) {
        const script = document.createElement("script");
        script.src = config.toss.scriptUrl;

        script.onload = () => {
          initializeTossPayments();
        };

        script.onerror = (error) => {
          onError?.(new Error("결제 시스템을 로드하는데 실패했습니다."));
        };

        document.head.appendChild(script);
      } else {
        initializeTossPayments();
      }
    } catch (error) {
      onError?.(error);
    } finally {
      isLoading.value = false;
    }
  };

  const initializeTossPayments = () => {
    try {
      tossPayments.value = window.TossPayments(clientKey.value);
      isReady.value = true;
    } catch (error) {
      onError?.(new Error("결제 시스템 초기화에 실패했습니다."));
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

  return {
    isReady,
    isLoading,
    initPayment,
    requestPayment,
  };
}
