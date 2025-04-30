<script setup>
import { useRouter } from "vue-router";
import productSkeleton from "@/assets/image/product-skeleton.png";
import colorQuizImg from "@/assets/image/perk-color-quiz.png";

const router = useRouter();

const perks = [
  {
    id: 1,
    title: "내 마음의 여행",
    price: 0,
    priceLabel: "무료",
    description: "어쩌구 저쩌구\n내 마음을 찾아 떠나는 여행",
    image: productSkeleton,
    route: "/perks/quiz-free",
  },
  {
    id: 2,
    title: "내 마음의 프리미엄 여행",
    price: 100,
    priceLabel: "100포인트",
    description: "프리미엄 열차를 타고\n내 마음을 찾아 떠나는 여행",
    image: colorQuizImg,
    route: "/perks/quiz",
  },
];
const goToQuiz = (perk) => {
  router.push(perk.route);
};
</script>

<template>
  <div class="max-w-6xl mx-auto py-12 px-4">
    <h1 class="text-3xl font-bold mb-2 text-center">심리 테스트 목록</h1>
    <p class="text-gray-500 mb-10 text-center">
      Lorem ipsum dolor sit amet consectetur adipiscing elit tortor<br />
      eu egestas morbi sem vulputate etiam facilisis.
    </p>
    <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
      <div
        v-for="perk in perks"
        :key="perk.id"
        class="flex bg-white rounded-xl shadow-sm p-6 items-center"
      >
        <div
          class="w-28 h-28 bg-gray-100 rounded-lg flex items-center justify-center mr-8"
        >
          <img
            v-if="perk.image"
            :src="perk.image"
            alt="perk image"
            class="w-20 h-20 object-cover rounded"
          />
          <div v-else class="text-4xl text-gray-300" />
        </div>
        <div class="flex-1">
          <div class="flex items-center mb-2">
            <span class="text-lg font-bold">{{ perk.title }}</span>
            <span class="ml-3 text-blue-600 font-semibold text-base">{{
              perk.priceLabel
            }}</span>
          </div>
          <div
            class="text-gray-600 mb-4 whitespace-pre-line"
            v-html="perk.description.replace(/\\n/g, '<br>')"
          ></div>
          <button
            class="bg-blue-600 hover:bg-blue-700 text-white px-5 py-2 rounded font-semibold transition"
            @click="goToQuiz(perk)"
          >
            진행하기
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
