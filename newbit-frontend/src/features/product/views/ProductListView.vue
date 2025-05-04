<script setup>
import { useRouter } from "vue-router";
import { ref } from "vue";
import diamondIcon from "@/assets/image/diamond-icon.png";

const router = useRouter();
const selectedProductId = ref(null);
const isProcessing = ref(false);

// TODO : API 연결이 안되서 프로덕트 리스트 하드 코딩한 부분 추후 수정, 시간 없으면 안할 수도 있음
const products = [
  {
    id: 1,
    title: "다이아 100개 패키지",
    price: 10000,
    priceLabel: "/VAT 포함",
    description: "기본 다이아몬드 패키지로 다양한 서비스를 이용해보세요.",
    image: diamondIcon,
    includes: [
      "100개의 다이아몬드",
      "기본 프로필 뱃지",
      "1개월 동안 사용 가능",
    ],
  },
  {
    id: 2,
    title: "다이아 300개 패키지",
    price: 30000,
    priceLabel: "/VAT 포함",
    description: "인기 다이아몬드 패키지로 더 많은 혜택을 누려보세요.",
    image: diamondIcon,
    includes: [
      "300개의 다이아몬드",
      "프리미엄 프로필 뱃지",
      "3개월 동안 사용 가능",
      "300 포인트 보너스",
    ],
  },
  {
    id: 3,
    title: "다이아 1000개 패키지",
    price: 100000,
    priceLabel: "/VAT 포함",
    description: "최고급 다이아몬드 패키지로 VIP 혜택을 누려보세요.",
    image: diamondIcon,
    includes: [
      "1,000개의 다이아몬드",
      "VIP 프로필 뱃지",
      "6개월 동안 사용 가능",
      "1000 포인트 보너스",
    ],
  },
];

const handlePurchase = (product) => {
  selectedProductId.value = product.id;
  isProcessing.value = true;

  setTimeout(() => {
    router.push({
      path: "/products/payment",
      query: {
        productId: product.id,
        amount: product.price,
        orderName: product.title,
        userId: "test-user", // TODO : 실제 구현시 로그인된 사용자 ID를 사용
      },
    });
  }, 600);
};
</script>

<template>
  <div class="max-w-6xl mx-auto py-12 px-4">
    <h1 class="text-heading1 mb-2 text-center">다이아 구매 페이지</h1>
    <p class="text-16px-regular text-gray-500 mb-10 text-center">
      원하는 다이아 패키지를 선택하고 구매하여<br />
      다양한 서비스를 이용해보세요.
    </p>

    <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
      <div
        v-for="product in products"
        :key="product.id"
        class="product-card bg-white rounded-xl shadow-sm p-6 flex flex-col transition-all duration-300 hover:shadow-md"
        :class="{
          'selected-card': selectedProductId === product.id && isProcessing,
          'scale-up': selectedProductId === null,
        }"
      >
        <div class="flex items-center mb-4">
          <div
            class="w-12 h-12 bg-blue-50 rounded-lg flex items-center justify-center mr-4 transition-transform"
            style="background: var(--newbitlight)"
          >
            <img
              v-if="product.image"
              :src="product.image"
              :alt="product.title"
              class="w-8 h-8 object-contain"
            />
          </div>
          <div class="flex-1">
            <h3 class="text-heading3">{{ product.title }}</h3>
            <div class="flex items-baseline">
              <span class="text-2xl font-bold text-newbitnormal"
                >{{ product.price.toLocaleString() }}원</span
              >
              <span class="text-gray-500 ml-1">{{ product.priceLabel }}</span>
            </div>
          </div>
        </div>

        <p class="text-13px-regular text-gray-600 mb-6">
          {{ product.description }}
        </p>

        <div class="mt-auto">
          <h4 class="font-medium mb-2 text-13px-bold">포함 내용</h4>
          <ul class="space-y-2 mb-6">
            <li
              v-for="(item, index) in product.includes"
              :key="index"
              class="flex items-center text-gray-600"
            >
              <span
                class="w-2 h-2 rounded-full mr-2"
                style="background: var(--newbitnormal)"
              ></span>
              {{ item }}
            </li>
          </ul>

          <button
            @click="handlePurchase(product)"
            :disabled="isProcessing"
            class="w-full text-button py-3 rounded-lg font-semibold transition transform hover:scale-[1.02] active:scale-[0.98]"
            :style="{
              background:
                isProcessing && selectedProductId === product.id
                  ? 'var(--newbitgray)'
                  : 'var(--newbitnormal)',
              color: '#fff',
            }"
          >
            <span v-if="selectedProductId === product.id && isProcessing">
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
              처리 중...
            </span>
            <span v-else>구매하기</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.product-card {
  backface-visibility: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1.5px solid var(--newbitdivider);
  background: var(--newbitlightmode);
}

.product-card:hover {
  transform: translateY(-5px);
  border-color: var(--newbitnormal);
  box-shadow: var(--drop-shadow);
}

.selected-card {
  transform: translateY(-12px) scale(1.05);
  border-color: var(--newbitnormal);
  box-shadow: 0 16px 32px -8px var(--newbitnormal);
  position: relative;
  z-index: 10;
}

.scale-up {
  animation: scaleUp 0.5s ease-out;
}

@keyframes scaleUp {
  0% {
    opacity: 0.7;
    transform: scale(0.95);
  }
  100% {
    opacity: 1;
    transform: scale(1);
  }
}
</style>
