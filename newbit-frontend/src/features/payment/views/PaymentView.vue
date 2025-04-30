<script setup>
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import PaymentWidget from "../components/PaymentWidget.vue";

const route = useRoute();
const orderId = ref("");
const amount = ref(0);
const orderName = ref("");
const customerKey = ref("");

// URL 쿼리 파라미터나 필요한 정보를 설정 테스트
onMounted(() => {
  orderId.value = route.query.orderId || `ORDER-${Date.now()}`;
  amount.value = parseInt(route.query.amount) || 10000;
  orderName.value = route.query.orderName || "다이아 100개";
  customerKey.value = `CUSTOMER-${route.query.userId || 1}`;
});
</script>

<template>
  <div class="payment-page">
    <h1>결제하기</h1>
    <PaymentWidget
      :orderId="orderId"
      :amount="amount"
      :orderName="orderName"
      :customerKey="customerKey"
    />
  </div>
</template>

<style scoped>
.payment-page {
  padding: 32px;
  max-width: 800px;
  margin: 0 auto;
}

h1 {
  margin-bottom: 32px;
  text-align: center;
}
</style>
