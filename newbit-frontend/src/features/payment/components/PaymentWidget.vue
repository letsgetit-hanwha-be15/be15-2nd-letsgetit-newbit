<script setup>
import { onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import axios from "axios";

const route = useRoute();
const router = useRouter();

const props = defineProps({
  orderId: {
    type: String,
    required: true,
  },
  amount: {
    type: Number,
    required: true,
  },
  orderName: {
    type: String,
    required: true,
  },
  customerKey: {
    type: String,
    required: true,
  },
});

const clientKey = ref("test_ck_D5GePWvyJnrK0W0k6q8gLzN97Eoq"); // 토스페이먼츠 테스트 클라이언트 키
const paymentWidget = ref(null);
const paymentMethod = ref(null);
const isPaymentReady = ref(false);
const errorMessage = ref("");

onMounted(async () => {
  try {
    // 토스페이먼츠 SDK 로드
    if (!window.TossPayments) {
      console.log("Loading Toss Payments SDK...");
      const script = document.createElement("script");
      script.src = "https://js.tosspayments.com/v1";
      document.head.appendChild(script);

      script.onload = () => {
        console.log("Toss Payments SDK loaded successfully");
        initializeWidget();
      };

      script.onerror = (error) => {
        console.error("Failed to load Toss Payments SDK:", error);
        errorMessage.value = "결제 시스템을 로드하는데 실패했습니다.";
      };
    } else {
      console.log("Toss Payments SDK already loaded");
      initializeWidget();
    }
  } catch (error) {
    console.error("Error in onMounted:", error);
    errorMessage.value = "결제 시스템 초기화 중 오류가 발생했습니다.";
  }
});

const initializeWidget = async () => {
  try {
    console.log("Initializing payment widget...");
    const tossPayments = window.TossPayments(clientKey.value);

    if (!tossPayments) {
      throw new Error("TossPayments is not available");
    }

    // 결제 위젯 초기화
    paymentWidget.value = await tossPayments.requestPayment({
      orderId: props.orderId,
      orderName: props.orderName,
      customerName: "고객명",
      amount: props.amount,
      successUrl: `${window.location.origin}/payments/success`,
      failUrl: `${window.location.origin}/payments/failed`,
    });

    console.log("Payment widget initialized");

    // 결제 수단 렌더링
    paymentMethod.value = paymentWidget.value.renderPaymentMethods(
      "#payment-method",
      {
        value: props.amount,
      }
    );

    console.log("Payment methods rendered");
    isPaymentReady.value = true;
  } catch (error) {
    console.error("Error initializing payment widget:", error);
    errorMessage.value = "결제 위젯 초기화 중 오류가 발생했습니다.";
  }
};

const handlePayment = async () => {
  try {
    await paymentWidget.value.requestPayment();
  } catch (error) {
    console.error("결제 요청 실패:", error);
    errorMessage.value = "결제 요청 중 오류가 발생했습니다.";
  }
};
</script>

<template>
  <div class="payment-widget-container">
    <h2>결제 정보</h2>
    <div class="order-info">
      <p><strong>주문명:</strong> {{ orderName }}</p>
      <p><strong>주문번호:</strong> {{ orderId }}</p>
      <p><strong>결제금액:</strong> {{ amount.toLocaleString() }}원</p>
    </div>

    <div id="payment-widget" class="payment-widget">
      <!-- 토스페이먼츠 결제 위젯이 렌더링 될 영역 -->
      <div id="payment-method"></div>
    </div>

    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>

    <button
      class="payment-button"
      @click="handlePayment"
      :disabled="!isPaymentReady"
    >
      {{ amount.toLocaleString() }}원 결제하기
    </button>
  </div>
</template>

<style scoped>
.payment-widget-container {
  max-width: 480px;
  margin: 0 auto;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

.order-info {
  margin-bottom: 24px;
  padding: 16px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.payment-widget {
  margin-bottom: 24px;
}

.error-message {
  color: #dc3545;
  margin-bottom: 16px;
  padding: 8px;
  background-color: #f8d7da;
  border-radius: 4px;
}

.payment-button {
  width: 100%;
  padding: 12px;
  background-color: #3182f6;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s;
}

.payment-button:hover {
  background-color: #1c64f2;
}

.payment-button:disabled {
  background-color: #a0a0a0;
  cursor: not-allowed;
}
</style>
