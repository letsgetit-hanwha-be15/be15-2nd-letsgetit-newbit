<script setup>
import { ref, onMounted, onUnmounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import api from "@/api/axios";
import { paymentApi } from "@/api/payment";

const route = useRoute();
const router = useRouter();

const paymentKey = ref("");
const orderId = ref("");
const amount = ref("");
const orderName = ref("");
const isConfirming = ref(false);
const paymentConfirmed = ref(false);
const errorMessage = ref("");
const receiptUrl = ref("");

let blockPopState = false;

onMounted(async () => {
  paymentKey.value = route.query.paymentKey || "";
  orderId.value = route.query.orderId || "";
  amount.value = route.query.amount || "";
  orderName.value = "";

  if (!paymentKey.value || !orderId.value || !amount.value) {
    router.replace("/products");
    return;
  }

  const handlePopState = (e) => {
    if (isConfirming.value) {
      history.pushState(null, "", location.href);
    }
  };
  window.addEventListener("popstate", handlePopState);

  const preventEvent = (e) => {
    if (isConfirming.value) {
      e.preventDefault();
      e.stopPropagation();
      return false;
    }
  };
  window.addEventListener("keydown", preventEvent, true);
  window.addEventListener("mousedown", preventEvent, true);
  window.addEventListener("touchstart", preventEvent, true);

  await confirmPayment();

  onUnmounted(() => {
    window.removeEventListener("popstate", handlePopState);
    window.removeEventListener("keydown", preventEvent, true);
    window.removeEventListener("mousedown", preventEvent, true);
    window.removeEventListener("touchstart", preventEvent, true);
  });
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
      orderName.value = response.data.orderName || route.query.orderName || "";
      receiptUrl.value = response.data.receipt?.url || "";
    } else {
      errorMessage.value = "결제 승인 처리 중 오류가 발생했습니다.";
    }
  } catch (error) {
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
      <div class="backdrop"></div>
      <div class="spinner-center">
        <h2>결제 승인 처리 중...</h2>
        <div class="spinner"></div>
      </div>
    </div>

    <div v-else-if="paymentConfirmed" class="success">
      <div class="success-icon">✓</div>
      <h1>결제가 완료되었습니다!</h1>
      <div class="payment-details-box">
        <div class="payment-details-row">
          <span class="label">주문번호</span>
          <span class="value">{{ orderId }}</span>
        </div>
        <div class="payment-details-row">
          <span class="label">상품명</span>
          <span class="value">{{ orderName }}</span>
        </div>
        <div class="payment-details-row">
          <span class="label">결제금액</span>
          <span class="value">{{ Number(amount).toLocaleString() }}원</span>
        </div>
        <div v-if="receiptUrl" class="receipt-btn-row center">
          <a
            :href="receiptUrl"
            target="_blank"
            class="receipt-btn"
            role="button"
          >
            영수증 보기
          </a>
        </div>
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
  max-width: 480px;
  margin: 50px auto;
  padding: 32px;
  text-align: center;
  border-radius: 12px;
  background: #fff;
}

.backdrop {
  position: fixed;
  z-index: 1000;
  left: 0;
  top: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.35);
  pointer-events: all;
}
.spinner-center {
  position: fixed;
  z-index: 1100;
  left: 0;
  top: 0;
  width: 100vw;
  height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  pointer-events: none;
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

.payment-details-box {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 24px 20px 16px 20px;
  margin: 28px 0 24px 0;
  display: flex;
  flex-direction: column;
  gap: 14px;
  align-items: flex-start;
}

.payment-details-row {
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
.receipt-btn-row {
  width: 100%;
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}
.receipt-btn {
  display: inline-block;
  padding: 10px 22px;
  background-color: #3182f6;
  color: #fff;
  border-radius: 4px;
  font-size: 15px;
  font-weight: 600;
  text-decoration: none;
  transition: background 0.2s;
  box-shadow: 0 2px 8px rgba(49, 130, 246, 0.08);
}
.receipt-btn:hover {
  background-color: #1c64f2;
}
.home-button,
.retry-button {
  width: 100%;
  margin-top: 12px;
  padding: 14px 0;
  background-color: #3182f6;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 17px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s;
}
.home-button:hover,
.retry-button:hover {
  background-color: #1c64f2;
}
.retry-button {
  margin-right: 0;
  background-color: #6c757d;
}
.retry-button:hover {
  background-color: #5a6268;
}
.receipt-btn-row.center {
  width: 100%;
  display: flex;
  justify-content: center;
  margin-top: 18px;
}
</style>
