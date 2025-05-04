<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import api from "@/api/axios";
import { paymentApi } from "@/api/payment";

const route = useRoute();
const router = useRouter();

const errorCode = ref("");
const errorMessage = ref("");
const orderId = ref("");

onMounted(() => {
  // URL 쿼리 파라미터에서 오류 정보 가져오기
  errorCode.value = route.query.code || "";
  errorMessage.value = route.query.message || "알 수 없는 오류가 발생했습니다.";
  orderId.value = route.query.orderId || "";
});

const retryPayment = () => {
  // 결제 페이지로 다시 이동
  router.push({
    path: "/payments",
    query: {
      orderId: orderId.value,
    },
  });
};

const goToHome = () => {
  router.push("/");
};
</script>

<template>
  <div class="failed-container">
    <div class="failed-icon">✕</div>
    <h1>결제가 실패했습니다</h1>

    <div class="error-details">
      <p v-if="errorCode"><strong>오류 코드:</strong> {{ errorCode }}</p>
      <p><strong>오류 메시지:</strong> {{ errorMessage }}</p>
      <p v-if="orderId"><strong>주문번호:</strong> {{ orderId }}</p>
    </div>

    <div class="action-buttons">
      <button class="retry-button" @click="retryPayment">결제 다시 시도</button>
      <button class="home-button" @click="goToHome">홈으로 이동</button>
    </div>
  </div>
</template>

<style scoped>
.failed-container {
  max-width: 500px;
  margin: 50px auto;
  padding: 32px;
  text-align: center;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.failed-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 24px;
  background-color: #dc3545;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
}

.error-details {
  margin: 24px 0;
  padding: 16px;
  background-color: #f8f9fa;
  border-radius: 4px;
  text-align: left;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 16px;
}

.retry-button,
.home-button {
  padding: 12px 24px;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.retry-button {
  background-color: #3182f6;
}

.retry-button:hover {
  background-color: #1c64f2;
}

.home-button {
  background-color: #6c757d;
}

.home-button:hover {
  background-color: #5a6268;
}
</style>
