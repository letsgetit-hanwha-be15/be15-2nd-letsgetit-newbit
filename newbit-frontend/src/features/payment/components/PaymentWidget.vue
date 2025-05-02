<script setup>
import { onMounted } from "vue";
import { usePayment } from "../composables/usePayment";

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

const emit = defineEmits(["success", "error"]);

const { isReady, initPayment, requestPayment } = usePayment({
  onSuccess: (result) => emit("success", result),
  onError: (error) => emit("error", error),
});

onMounted(() => {
  initPayment();
});

const handlePayment = () => {
  requestPayment({
    amount: props.amount,
    orderId: props.orderId,
    orderName: props.orderName,
    customerName: "고객명",
  });
};
</script>

<template>
  <div class="payment-widget-container p-6">
    <div class="mb-8 space-y-4">
      <div class="flex justify-between items-center text-lg">
        <span class="font-medium text-gray-600">상품</span>
        <span class="font-semibold">{{ orderName }}</span>
      </div>
      <div class="flex justify-between items-center text-lg">
        <span class="font-medium text-gray-600">결제 금액</span>
        <span class="font-semibold">{{ amount.toLocaleString() }}원</span>
      </div>
      <hr class="border-gray-200" />
    </div>

    <button
      @click="handlePayment"
      :disabled="!isReady"
      class="w-full bg-blue-600 hover:bg-blue-700 disabled:bg-gray-400 text-white py-4 rounded-lg font-semibold text-lg transition"
    >
      {{ amount.toLocaleString() }}원 결제하기
    </button>
  </div>
</template>

<style scoped>
.payment-widget-container {
  width: 100%;
  background: white;
}
</style>
