<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";

const route = useRoute();
const router = useRouter();

const errorCode = ref("");
const errorMessage = ref("");
const orderId = ref("");

onMounted(() => {
  router.replace("/products");

  errorCode.value = route.query.code || "";
  errorMessage.value = route.query.message || "알 수 없는 오류가 발생했습니다.";
  orderId.value = route.query.orderId || "";
});

const retryPayment = () => {
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
    <h1 class="text-heading2 text-newbitred mb-2">결제가 실패했습니다</h1>
    <div class="fail-details-box">
      <div v-if="errorCode" class="fail-details-row">
        <span class="label text-13px-bold">오류 코드</span>
        <span class="value text-13px-regular">{{ errorCode }}</span>
      </div>
      <div class="fail-details-row">
        <span class="label text-13px-bold">오류 메시지</span>
        <span class="value text-13px-regular">{{ errorMessage }}</span>
      </div>
      <div v-if="orderId" class="fail-details-row">
        <span class="label text-13px-bold">주문번호</span>
        <span class="value text-13px-regular">{{ orderId }}</span>
      </div>
    </div>
    <button class="retry-button text-button" @click="retryPayment">
      결제 다시 시도
    </button>
    <button class="home-button text-button" @click="goToHome">
      홈으로 이동
    </button>
  </div>
</template>

<style scoped>
.failed-container {
  max-width: 480px;
  margin: 50px auto;
  padding: 32px;
  text-align: center;
  border-radius: 12px;
  background: var(--newbitbackground);
  box-shadow: var(--drop-shadow);
}
.failed-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 24px;
  background-color: var(--newbitred);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
}
.fail-details-box {
  background: var(--newbitlightmode);
  border-radius: 8px;
  padding: 24px 20px 16px 20px;
  margin: 28px 0 24px 0;
  display: flex;
  flex-direction: column;
  gap: 14px;
  align-items: flex-start;
  box-shadow: var(--drop-shadow);
}
.fail-details-row {
  display: flex;
  justify-content: space-between;
  width: 100%;
  font-size: 17px;
}
.label {
  color: var(--newbitgray);
  font-weight: 500;
  min-width: 80px;
}
.value {
  color: var(--newbittext);
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
  background-color: var(--newbitnormal);
  color: #fff;
}
.retry-button:hover {
  background-color: var(--newbitdark);
}
.home-button {
  background-color: var(--newbitgray);
  color: #fff;
}
.home-button:hover {
  background-color: var(--newbitdark);
}
</style>
