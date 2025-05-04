<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import api from "@/api/axios";
import { paymentApi } from "@/api/payment";

const route = useRoute();
const router = useRouter();

const paymentKey = ref("");
const orderId = ref("");
const amount = ref("");
const isConfirming = ref(false);
const paymentConfirmed = ref(false);
const errorMessage = ref("");

onMounted(async () => {
  paymentKey.value = route.query.paymentKey || "";
  orderId.value = route.query.orderId || "";
  amount.value = route.query.amount || "";

  if (paymentKey.value && orderId.value && amount.value) {
    await confirmPayment();
  }
});

const confirmPayment = async () => {
  if (isConfirming.value) return;

  isConfirming.value = true;

  try {
    const response = await api.post(paymentApi.endpoints.confirm, {
      paymentKey: paymentKey.value,
      orderId: orderId.value,
      amount: amount.value,
    });

    if (response.status === 200) {
      paymentConfirmed.value = true;
    } else {
      errorMessage.value = "결제 승인 처리 중 오류가 발생했습니다.";
    }
  } catch (error) {
    console.error("결제 승인 오류:", error);
    errorMessage.value =
      error.response?.data?.message || "결제 승인 처리 중 오류가 발생했습니다.";
  } finally {
    isConfirming.value = false;
  }
};

const goToHome = () => {
  router.push("/");
};
</script>

<template>
  <div class="success-container">
    <div v-if="isConfirming" class="loading">
      <h2>결제 승인 처리 중...</h2>
      <div class="spinner"></div>
    </div>

    <div v-else-if="paymentConfirmed" class="success">
      <div class="success-icon">✓</div>
      <h1>결제가 완료되었습니다!</h1>
      <div class="payment-details">
        <p><strong>주문번호:</strong> {{ orderId }}</p>
        <p>
          <strong>결제금액:</strong> {{ Number(amount).toLocaleString() }}원
        </p>
      </div>
      <button class="home-button" @click="goToHome">홈으로 이동</button>
    </div>

    <div v-else-if="errorMessage" class="error">
      <h2>결제 승인 실패</h2>
      <p>{{ errorMessage }}</p>
      <button class="retry-button" @click="confirmPayment">다시 시도</button>
      <button class="home-button" @click="goToHome">홈으로 이동</button>
    </div>
  </div>
</template>

<style scoped>
.success-container {
  max-width: 500px;
  margin: 50px auto;
  padding: 32px;
  text-align: center;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.success-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 24px;
  background-color: #3182f6;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
}

.payment-details {
  margin: 24px 0;
  padding: 16px;
  background-color: #f8f9fa;
  border-radius: 4px;
  text-align: left;
}

.home-button,
.retry-button {
  padding: 12px 24px;
  background-color: #3182f6;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.home-button:hover,
.retry-button:hover {
  background-color: #1c64f2;
}

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.spinner {
  width: 50px;
  height: 50px;
  border: 5px solid #f3f3f3;
  border-top: 5px solid #3182f6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 20px 0;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.error {
  color: #dc3545;
}

.retry-button {
  margin-right: 10px;
  background-color: #6c757d;
}

.retry-button:hover {
  background-color: #5a6268;
}
</style>
