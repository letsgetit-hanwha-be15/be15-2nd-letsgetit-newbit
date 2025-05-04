<script setup>
import { onMounted, ref, onUnmounted } from "vue";
import { usePayment } from "../composables/usePayment";
import { paymentService } from "../services/paymentService";

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
  userId: {
    type: Number,
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
    const formattedCustomerKey = `CUSTOMER-${props.customerKey}`;
    const widget = await initPaymentWidget(formattedCustomerKey);

    await setPaymentAmount(props.amount);

    paymentMethodWidget.value = await renderPaymentMethods("#payment-method");

    const agreementWidget = await widget.renderAgreement({
      selector: "#agreement",
    });

    agreementWidget.on("agreementStatusChange", (status) => {
      isAgreementValid.value = !!status.agreedRequiredTerms;
    });
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
  if (paymentMethodWidget.value) {
    paymentMethodWidget.value.destroy();
  }
});

const handlePayment = async () => {
  try {
    await paymentService.saveOrder({
      orderId: props.orderId,
      userId: props.userId,
      orderName: props.orderName,
      amount: props.amount,
    });
    requestPaymentWithWidget({
      orderId: props.orderId,
      orderName: props.orderName,
      amount: props.amount,
      customerName: "고객명",
    });
  } catch (error) {
    emit("error", error);
  }
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
