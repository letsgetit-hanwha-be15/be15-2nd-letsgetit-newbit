<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";

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
    <div class="fail-details-box">
      <div v-if="errorCode" class="fail-details-row">
        <span class="label">오류 코드</span>
        <span class="value">{{ errorCode }}</span>
      </div>
      <div class="fail-details-row">
        <span class="label">오류 메시지</span>
        <span class="value">{{ errorMessage }}</span>
      </div>
      <div v-if="orderId" class="fail-details-row">
        <span class="label">주문번호</span>
        <span class="value">{{ orderId }}</span>
      </div>
    </div>
    <button class="retry-button" @click="retryPayment">결제 다시 시도</button>
    <button class="home-button" @click="goToHome">홈으로 이동</button>
  </div>
</template>

<style scoped>
.failed-container {
  max-width: 480px;
  margin: 50px auto;
  padding: 32px;
  text-align: center;
  border-radius: 12px;
  box-shadow: 0 2px 16px rgba(0, 0, 0, 0.08);
  background: #fff;
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
.fail-details-box {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 24px 20px 16px 20px;
  margin: 28px 0 24px 0;
  display: flex;
  flex-direction: column;
  gap: 14px;
  align-items: flex-start;
}
.fail-details-row {
  display: flex;
  justify-content: space-between;
  width: 100%;
  font-size: 17px;
}
.label {
  color: #555;
  font-weight: 500;
  min-width: 80px;
}
.value {
  color: #222;
  font-weight: 600;
  word-break: break-all;
  text-align: right;
}
.home-button,
.retry-button {
  width: 100%;
  margin-top: 12px;
  padding: 14px 0;
  font-size: 17px;
  font-weight: 600;
  border-radius: 6px;
  border: none;
  cursor: pointer;
  transition: background-color 0.2s;
}
.retry-button {
  background-color: #3182f6;
  color: #fff;
}
.retry-button:hover {
  background-color: #1c64f2;
}
.home-button {
  background-color: #6c757d;
  color: #fff;
}
.home-button:hover {
  background-color: #5a6268;
}
</style>
