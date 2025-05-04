<script setup>
import { onMounted, ref, onUnmounted } from "vue";
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

const paymentMethodWidget = ref(null);
const isAgreementValid = ref(false);

const {
  isReady,
  initPayment,
  initPaymentWidget,
  setPaymentAmount,
  renderPaymentMethods,
  requestPaymentWithWidget,
} = usePayment({
  onSuccess: (result) => emit("success", result),
  onError: (error) => emit("error", error),
});

const setupPaymentWidget = async () => {
  if (!isReady.value) return;

  try {
    // 결제 위젯 초기화 - 백엔드에서 처리할 수 있도록 CUSTOMER- 접두사 추가
    const formattedCustomerKey = `CUSTOMER-${props.customerKey}`;
    const widget = await initPaymentWidget(formattedCustomerKey);

    // 금액 설정
    await setPaymentAmount(props.amount);

    // 결제 수단 UI 렌더링
    paymentMethodWidget.value = await renderPaymentMethods("#payment-method");

    // 약관 UI 렌더링
    const agreementWidget = await widget.renderAgreement({
      selector: "#agreement",
    });

    // 약관 동의 상태 감시
    agreementWidget.on("agreementStatusChange", (status) => {
      isAgreementValid.value = !!status.agreedRequiredTerms;
    });
    // 디폴트 false 보장
    isAgreementValid.value = false;
  } catch (error) {
    emit("error", error);
  }
};

onMounted(async () => {
  await initPayment();
  setupPaymentWidget();
});

onUnmounted(() => {
  // 위젯 정리 (필요한 경우)
  if (paymentMethodWidget.value) {
    paymentMethodWidget.value.destroy();
  }
});

const handlePayment = () => {
  requestPaymentWithWidget({
    orderId: props.orderId,
    orderName: props.orderName,
    amount: props.amount,
    customerName: "고객명",
    // customerKey는 결제 요청에 포함하지 않음
  });
};
</script>

<template>
  <div class="payment-widget-container p-6">
    <div class="mb-4 space-y-4">
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

    <!-- 토스페이먼츠 결제 위젯 영역 -->
    <div class="mb-6">
      <div id="payment-method" class="mb-4"></div>
      <div id="agreement" class="mb-4"></div>
    </div>

    <button
      @click="handlePayment"
      :disabled="!isReady || !isAgreementValid"
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
