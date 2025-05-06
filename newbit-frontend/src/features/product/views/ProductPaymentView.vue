<script setup>
import { useRoute, useRouter } from "vue-router";
import PaymentWidget from "@/features/payment/components/PaymentWidget.vue";
import { paymentService } from "@/features/payment/services/paymentService";
import { onMounted, ref, onUnmounted, computed } from "vue";
import { useAuthStore } from "@/features/stores/auth";

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const paymentError = ref(false);
const errorMessage = ref("");
const isPageLoading = ref(true);

const orderId = `ORDER-${Date.now()}`;
const amount = Number(route.query.amount) || 0;
const orderName = route.query.orderName || "";
const customerKey = `CUSTOMER-${route.query.userId || "guest"}`;

const userId = computed(() => {
  try {
    const token = authStore.accessToken;
    if (!token) return null;
    const payload = JSON.parse(atob(token.split(".")[1]));
    return payload.userId;
  } catch (e) {
    return null;
  }
});

onMounted(() => {
  // 뒤로가기 방지: popstate 발생 시 상품 목록으로 이동
  const handlePopState = () => {
    router.replace("/products");
  };
  window.addEventListener("popstate", handlePopState);

  if (!amount || !orderName) {
    alert("올바르지 않은 결제 정보입니다.");
    router.push("/products");
    return;
  }

  setTimeout(() => {
    isPageLoading.value = false;
  }, 500);

  onUnmounted(() => {
    window.removeEventListener("popstate", handlePopState);
  });
});

const handlePaymentSuccess = async (result) => {
  try {
    await paymentService.verifyPayment(result.paymentKey, orderId, amount);
    window.location.replace(
      `/payments/success?orderId=${orderId}&orderName=${orderName}`
    );
  } catch (error) {
    handlePaymentError(error);
  }
};

const handlePaymentError = (error) => {
  paymentError.value = true;
  errorMessage.value = error?.message || "결제 중 오류가 발생했습니다.";
};

const handleRetry = () => {
  paymentError.value = false;
  errorMessage.value = "";
};

const goToList = () => {
  router.push("/products");
};
</script>

<template>
  <div class="max-w-4xl mx-auto py-12 px-4 fade-in">
    <h1 class="text-3xl font-bold mb-8 text-center">결제하기</h1>

    <!-- 페이지 로딩 상태 -->
    <div
      v-if="isPageLoading"
      class="payment-page-loading bg-white p-8 rounded-lg shadow-sm"
    >
      <div class="flex justify-center mb-6">
        <svg
          class="animate-spin h-10 w-10 text-blue-600"
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
        >
          <circle
            class="opacity-25"
            cx="12"
            cy="12"
            r="10"
            stroke="currentColor"
            stroke-width="4"
          ></circle>
          <path
            class="opacity-75"
            fill="currentColor"
            d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
          ></path>
        </svg>
      </div>
      <div class="text-center text-gray-600">
        결제 페이지를 준비 중입니다...
      </div>
      <div class="mt-8 space-y-3">
        <div class="h-12 bg-gray-100 rounded animate-pulse"></div>
        <div class="h-10 bg-gray-100 rounded animate-pulse"></div>
        <div class="h-36 bg-gray-100 rounded animate-pulse"></div>
      </div>
    </div>

    <div
      v-else-if="paymentError"
      class="bg-white p-6 text-center rounded-lg shadow-sm slide-up"
    >
      <div class="text-red-600 text-lg mb-4">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          class="h-12 w-12 mx-auto mb-4"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
          />
        </svg>
        {{ errorMessage }}
      </div>
      <div class="space-x-4">
        <button
          @click="handleRetry"
          class="bg-blue-600 hover:bg-blue-700 text-white px-6 py-2 rounded-lg font-semibold transition transform hover:scale-[1.02] active:scale-[0.98]"
        >
          다시 시도
        </button>
        <button
          @click="goToList"
          class="bg-gray-100 hover:bg-gray-200 text-gray-700 px-6 py-2 rounded-lg font-semibold transition transform hover:scale-[1.02] active:scale-[0.98]"
        >
          목록으로
        </button>
      </div>
    </div>

    <div v-else class="bg-white overflow-hidden rounded-lg shadow-sm slide-up">
      <PaymentWidget
        :orderId="orderId"
        :amount="amount"
        :orderName="orderName"
        :customerKey="customerKey"
        :userId="userId"
        @success="handlePaymentSuccess"
        @error="handlePaymentError"
      />
    </div>
  </div>
</template>

<style scoped>
.space-x-4 > * + * {
  margin-left: 1rem;
}

.fade-in {
  animation: fadeIn 0.5s ease-out;
}

.slide-up {
  animation: slideUp 0.4s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.payment-page-loading {
  min-height: 400px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.animate-pulse {
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

@keyframes pulse {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}
</style>
