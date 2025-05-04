<script setup>
import { useRoute, useRouter } from "vue-router";
import PaymentWidget from "@/features/payment/components/PaymentWidget.vue";
import { paymentService } from "@/features/payment/services/paymentService";
import { onMounted, ref } from "vue";

const route = useRoute();
const router = useRouter();

const paymentError = ref(false);
const errorMessage = ref("");

// URL 쿼리 파라미터에서 결제 정보 가져오기
const orderId = `ORDER-${Date.now()}`;
const amount = Number(route.query.amount) || 0;
const orderName = route.query.orderName || "";
const customerKey = `CUSTOMER-${route.query.userId || "guest"}`;
const userId = Number(route.query.userId) || 9;

onMounted(() => {
  if (!amount || !orderName) {
    alert("올바르지 않은 결제 정보입니다.");
    router.push("/products");
  }
});

const handlePaymentSuccess = async (result) => {
  try {
    await paymentService.verifyPayment(result.paymentKey, orderId, amount);
    router.push({
      path: "/payments/success",
      query: { orderId, orderName },
    });
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
  <div class="max-w-4xl mx-auto py-12 px-4">
    <h1 class="text-3xl font-bold mb-8 text-center">결제하기</h1>

    <div
      v-if="paymentError"
      class="bg-white rounded-lg shadow-lg p-6 text-center"
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
          class="bg-blue-600 hover:bg-blue-700 text-white px-6 py-2 rounded-lg font-semibold transition"
        >
          다시 시도
        </button>
        <button
          @click="goToList"
          class="bg-gray-100 hover:bg-gray-200 text-gray-700 px-6 py-2 rounded-lg font-semibold transition"
        >
          목록으로
        </button>
      </div>
    </div>

    <div v-else class="bg-white rounded-lg shadow-lg overflow-hidden">
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
</style>
