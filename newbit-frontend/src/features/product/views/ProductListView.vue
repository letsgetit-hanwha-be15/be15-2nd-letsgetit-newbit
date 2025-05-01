<script setup>
import { useRouter } from "vue-router";
import diamondIcon from "@/assets/image/diamond-icon.png"; // 다이아몬드 아이콘 이미지 필요

const router = useRouter();

// TODO : API 연결이 안되서 프로덕트 리스트 하드 코딩한 부분 추후 수정
const products = [
  {
    id: 1,
    title: "다이아 100개 패키지",
    price: 10000,
    priceLabel: "/VAT 포함",
    description: "API 연결 안되서 상품 리스트 하드코딩 해놓음 추후 수정 필요",
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
    description: "API 연결 안되서 상품 리스트 하드코딩 해놓음 추후 수정 필요",
    image: diamondIcon,
    includes: [
      "300개의 다이아몬드",
      "프리미엄 프로필 뱃지",
      "3개월 동안 사용 가능",
      "10% 추가 보너스",
    ],
  },
  {
    id: 3,
    title: "API 연결 안되서 상품 리스트 하드코딩 해놓음 추후 수정 필요",
    price: 100000,
    priceLabel: "/VAT 포함",
    description: "API 연결 안되서 상품 리스트 하드코딩 해놓음 추후 수정 필요",
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
  router.push({
    path: "/products/payment",
    query: {
      productId: product.id,
      amount: product.price,
      orderName: product.title,
      userId: "test-user", // TODO : 실제 구현시 로그인된 사용자 ID를 사용
    },
  });
};
</script>

<template>
  <div class="max-w-6xl mx-auto py-12 px-4">
    <h1 class="text-3xl font-bold mb-2 text-center">다이아 구매 페이지</h1>
    <p class="text-gray-500 mb-10 text-center">
      Lorem ipsum dolor sit amet consectetur adipiscing eli<br />
      mattis sit phasellus mollis sit aliquam sit nullam.
    </p>

    <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
      <div
        v-for="product in products"
        :key="product.id"
        class="bg-white rounded-xl shadow-sm p-6 flex flex-col"
        :class="{ 'ring-2 ring-blue-500': product.id === 2 }"
      >
        <div class="flex items-center mb-4">
          <div
            class="w-12 h-12 bg-gray-100 rounded-lg flex items-center justify-center mr-4"
          >
            <img
              v-if="product.image"
              :src="product.image"
              :alt="product.title"
              class="w-8 h-8 object-contain"
            />
          </div>
          <div class="flex-1">
            <h3 class="text-lg font-bold">{{ product.title }}</h3>
            <div class="flex items-baseline">
              <span class="text-2xl font-bold"
                >{{ product.price.toLocaleString() }}원</span
              >
              <span class="text-gray-500 ml-1">{{ product.priceLabel }}</span>
            </div>
          </div>
        </div>

        <p class="text-gray-600 mb-6">{{ product.description }}</p>

        <div class="mt-auto">
          <h4 class="font-medium mb-2">What's included?</h4>
          <ul class="space-y-2 mb-6">
            <li
              v-for="(item, index) in product.includes"
              :key="index"
              class="flex items-center text-gray-600"
            >
              <span class="w-2 h-2 bg-gray-400 rounded-full mr-2"></span>
              {{ item }}
            </li>
          </ul>

          <button
            @click="handlePurchase(product)"
            class="w-full bg-blue-600 hover:bg-blue-700 text-white py-3 rounded-lg font-semibold transition"
          >
            구매하기
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>
