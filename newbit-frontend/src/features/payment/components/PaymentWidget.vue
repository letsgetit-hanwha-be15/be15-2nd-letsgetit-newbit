<script setup>
import { onMounted, ref, onUnmounted, computed } from "vue";
import { usePayment } from "../composables/usePayment";
import { paymentService } from "../services/paymentService";
import { useRouter } from "vue-router";

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
const isWidgetLoading = ref(true);
const isWidgetVisible = ref(false);

const router = useRouter();

const {
  isReady,
  initPayment,
  initPaymentWidget,
  setPaymentAmount,
  renderPaymentMethods,
  requestPaymentWithWidget,
} = usePayment({
  onSuccess: (result) => emit("success", result),
  // TODO : 결제 위젯 에러 발생시 에러 캐치하려고 했으나 위젯이 iframe 으로 렌더링되어 캐치 불가능한 문제 발생(추정)
  // Toss payments위젯 내부 문제인지 아닌지 자세한 원인 파악 필요, 차후에 우선적으로 해결
  onError: (error) => {
    console.log("[PaymentWidget][onError] 결제 위젯 에러 발생:", error);
    setTimeout(() => {
      if (
        // 결제 위젯 초기화 오류 발생시 위젯 초기화 함수 호출(소용 없음)
        paymentMethodWidget.value &&
        typeof paymentMethodWidget.value.destroy === "function"
      ) {
        paymentMethodWidget.value.destroy();
      }
      const el = document.getElementById("payment-method");
      if (el) el.innerHTML = "";
      const dimmer = document.getElementById(
        "__tosspayments_payment-gateway_dimmer__"
      );
      if (dimmer) dimmer.remove();
      const iframe = document.getElementById(
        "__tosspayments_payment-gateway_iframe__"
      );
      if (iframe) iframe.remove();
      router.replace("/products");
    }, 2000);
    emit("error", error);
  },
});

const setupPaymentWidget = async () => {
  if (!isReady.value) return;

  try {
    const formattedCustomerKey = `CUSTOMER-${props.customerKey}`;
    const widget = await initPaymentWidget(formattedCustomerKey);

    await setPaymentAmount(props.amount);

    setTimeout(() => {
      isWidgetLoading.value = false;

      setTimeout(() => {
        isWidgetVisible.value = true;
      }, 100);
    }, 300);

    paymentMethodWidget.value = await renderPaymentMethods("#payment-method");

    const agreementWidget = await widget.renderAgreement({
      selector: "#agreement",
    });

    isAgreementValid.value = true;

    agreementWidget.on("agreementStatusChange", (status) => {
      isAgreementValid.value = !!status.agreedRequiredTerms;
    });
  } catch (error) {
    isWidgetLoading.value = false;
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

const buttonDisabled = computed(
  () => !isReady.value || !isAgreementValid.value || isWidgetLoading.value
);
</script>

<template>
  <div class="payment-widget-container p-6 max-w-xl mx-auto">
    <div class="mb-4 space-y-4">
      <div class="flex justify-between items-center text-lg">
        <span class="font-medium text-gray-600 text-13px-bold">상품</span>
        <span class="font-semibold text-heading3">{{ orderName }}</span>
      </div>
      <div class="flex justify-between items-center text-lg">
        <span class="font-medium text-gray-600 text-13px-bold">결제 금액</span>
        <span
          class="font-semibold text-heading3"
          style="color: var(--newbitnormal)"
          >{{ amount.toLocaleString() }}원</span
        >
      </div>
      <hr class="border-gray-200" />
    </div>

    <!-- 토스페이먼츠 결제 위젯 영역 -->
    <div class="mb-6 relative">
      <!-- 로딩 스켈레톤 -->
      <div v-if="isWidgetLoading" class="payment-widget-skeleton">
        <div class="skeleton-header mb-3"></div>
        <div class="skeleton-method mb-4">
          <div class="skeleton-method-item"></div>
          <div class="skeleton-method-item"></div>
          <div class="skeleton-method-item"></div>
        </div>
        <div class="skeleton-agreement">
          <div class="skeleton-agreement-item"></div>
          <div class="skeleton-agreement-item"></div>
        </div>
      </div>

      <!-- 실제 위젯 -->
      <div
        :class="{ 'widget-visible': isWidgetVisible }"
        class="widget-container"
        v-show="!isWidgetLoading"
      >
        <div id="payment-method" class="mb-4"></div>
        <div id="agreement" class="mb-4"></div>
      </div>
    </div>

    <button
      @click="handlePayment"
      :disabled="buttonDisabled"
      class="w-full text-button py-4 rounded-lg font-semibold text-lg transition transform hover:scale-[1.02] active:scale-[0.98]"
      :style="{
        background: buttonDisabled
          ? 'var(--newbitgray)'
          : 'var(--newbitnormal)',
        color: '#fff',
      }"
    >
      <span v-if="isWidgetLoading">
        <svg
          class="inline-block animate-spin -ml-1 mr-2 h-5 w-5 text-white"
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
        >
          <circle
            class="opacity-25"
            cx="12"
            cy="12"
            r="10"
            stroke="currentColor"
            stroke-width="4"
          ></circle>
          <path
            class="opacity-75"
            fill="currentColor"
            d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
          ></path>
        </svg>
        로딩 중...
      </span>
      <span v-else>{{ amount.toLocaleString() }}원 결제하기</span>
    </button>
  </div>
</template>

<style scoped>
.payment-widget-container {
  width: 100%;
  background: var(--newbitbackground);
  box-shadow: var(--drop-shadow);
}

.payment-widget-skeleton {
  animation: pulse 1.5s infinite;
}

.skeleton-header {
  height: 24px;
  background-color: var(--newbitlightgray);
  border-radius: 4px;
}

.skeleton-method {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.skeleton-method-item {
  height: 40px;
  background-color: var(--newbitlightgray);
  border-radius: 8px;
}

.skeleton-agreement {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.skeleton-agreement-item {
  height: 20px;
  background-color: var(--newbitlightgray);
  border-radius: 4px;
}

.skeleton-agreement-item:last-child {
  width: 70%;
}

@keyframes pulse {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

.widget-container {
  opacity: 0;
  transition: opacity 0.4s ease-in-out, transform 0.4s ease-out;
  transform: translateY(10px);
}

.widget-visible {
  opacity: 1;
  transform: translateY(0);
}
</style>
