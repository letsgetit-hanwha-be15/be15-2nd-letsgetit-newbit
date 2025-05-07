<script setup>
import { useRouter } from "vue-router";
import { ref } from "vue";
import { useAuthStore } from "@/features/stores/auth.js";
import { purchasePerkByPoint } from "@/api/purchase";
import { getUserBalance } from "@/api/user";
import productSkeleton from "@/assets/image/product-skeleton.png";
import colorQuizImg from "@/assets/image/perk-color-quiz.png";

const router = useRouter();
const authStore = useAuthStore();
const showModal = ref(false);
const isLoading = ref(false);
const errorMsg = ref("");

const perks = [
  {
    id: 1,
    title: "마음 여행",
    price: 0,
    priceLabel: "무료",
    description: "질문에 답하며 나를 발견하는\n간단한 심리 테스트",
    image: productSkeleton,
    route: "/perks/quiz-free",
    bgColor: "from-purple-400 to-indigo-500",
    iconColor: "text-indigo-100",
    badgeColor: "bg-indigo-100 text-indigo-800",
  },
  {
    id: 2,
    title: "칼라 심리테스트",
    price: 100,
    priceLabel: "100포인트",
    description: "색깔로 알아보는\n나의 숨겨진 성격",
    image: colorQuizImg,
    route: "/perks/quiz",
    serviceId: 2,
    bgColor: "from-pink-500 to-rose-500",
    iconColor: "text-rose-100",
    badgeColor: "bg-rose-100 text-rose-800",
  },
];

const selectedPerk = ref(null);
const modalPoint = ref(null);
const modalDiamond = ref(null);

const openModal = async (perk) => {
  selectedPerk.value = perk;
  showModal.value = true;
  isLoading.value = false;
  errorMsg.value = "";
  try {
    const res = await getUserBalance();
    if (res.data && res.data.success) {
      const { pointBalance, diamondBalance } = res.data.data || {};
      modalPoint.value = pointBalance;
      modalDiamond.value = diamondBalance;
    } else {
      modalPoint.value = null;
      modalDiamond.value = null;
    }
  } catch (e) {
    modalPoint.value = null;
    modalDiamond.value = null;
  }
};

const goToQuiz = (perk) => {
  if (perk.price > 0) {
    openModal(perk);
  } else {
    router.push(perk.route);
  }
};

const handlePurchase = async () => {
  isLoading.value = true;
  errorMsg.value = "";
  try {
    await purchasePerkByPoint({
      userId: authStore.userId,
      serviceId: selectedPerk.value.serviceId,
    });
    try {
      const res = await getUserBalance();
      if (res.data && res.data.success) {
        const { pointBalance, diamondBalance } = res.data.data || {};
        modalPoint.value = pointBalance;
        modalDiamond.value = diamondBalance;
      }
    } catch (e) {}
    if (selectedPerk.value?.route === "/perks/quiz") {
      localStorage.setItem("perk_color_quiz_purchased", "true");
    }
    showModal.value = false;
    router.push(selectedPerk.value.route);
  } catch (e) {
    errorMsg.value = "구매에 실패했습니다. 포인트 잔액을 확인해주세요.";
  } finally {
    isLoading.value = false;
  }
};
</script>

<template>
  <div
    class="min-h-screen bg-gradient-to-b from-gray-50 to-gray-100 py-16 px-4"
  >
    <div class="max-w-5xl mx-auto">
      <!-- 헤더 섹션 -->
      <div class="text-center mb-16">
        <h1
          class="text-4xl font-extrabold mb-3 text-transparent bg-clip-text bg-gradient-to-r from-blue-600 to-indigo-600"
        >
          심리 테스트
        </h1>
        <p class="text-lg text-gray-600 max-w-2xl mx-auto">
          나에 대해 더 알아가는 시간! 다양한 심리 테스트를 통해 새로운 나를
          발견해보세요.
        </p>
      </div>

      <!-- 카드 그리드 -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-10">
        <div
          v-for="perk in perks"
          :key="perk.id"
          class="bg-white rounded-2xl overflow-hidden shadow-lg transform hover:scale-105 transition-all duration-300"
        >
          <div :class="`bg-gradient-to-r ${perk.bgColor} p-10`">
            <div class="flex justify-between items-start">
              <div
                class="bg-white bg-opacity-20 backdrop-blur-sm rounded-xl p-5 w-20 h-20 flex items-center justify-center"
              >
                <img
                  v-if="perk.image"
                  :src="perk.image"
                  alt="perk image"
                  class="w-14 h-14 object-cover"
                />
                <div v-else :class="`text-4xl ${perk.iconColor}`">
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    class="h-12 w-12"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z"
                    />
                  </svg>
                </div>
              </div>
              <span
                :class="`${perk.badgeColor} text-sm font-semibold py-1 px-3 rounded-full`"
              >
                {{ perk.priceLabel }}
              </span>
            </div>
          </div>

          <div class="p-8">
            <h2 class="text-2xl font-bold mb-3">{{ perk.title }}</h2>
            <p class="text-gray-600 mb-6 whitespace-pre-line">
              {{ perk.description }}
            </p>
            <button
              :class="`w-full py-3 rounded-xl font-semibold transition-all duration-300 ${
                perk.price > 0
                  ? 'bg-gradient-to-r from-blue-500 to-indigo-600 text-white hover:from-blue-600 hover:to-indigo-700'
                  : 'bg-gradient-to-r from-green-500 to-teal-500 text-white hover:from-green-600 hover:to-teal-600'
              }`"
              @click="goToQuiz(perk)"
            >
              {{ perk.price > 0 ? "포인트로 시작하기" : "무료로 시작하기" }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 구매 모달 -->
    <div
      v-if="showModal"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-60 backdrop-blur-sm"
    >
      <div
        class="bg-white rounded-2xl shadow-xl p-8 w-full max-w-md mx-4 transform transition-all duration-300 scale-100"
      >
        <div class="mb-6 text-center">
          <div
            class="inline-flex mx-auto items-center justify-center w-16 h-16 rounded-full bg-blue-100 text-blue-600 mb-4"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="h-8 w-8"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
              />
            </svg>
          </div>
          <h2 class="text-2xl font-bold text-gray-800">포인트 구매 확인</h2>
        </div>

        <div class="mb-8">
          <div
            class="flex justify-between items-center bg-gray-50 rounded-lg p-4 mb-4"
          >
            <span class="text-gray-600">현재 보유 포인트</span>
            <span class="font-bold text-lg text-blue-600">{{
              modalPoint ?? "-"
            }}</span>
          </div>

          <div
            class="flex justify-between items-center bg-gray-50 rounded-lg p-4 mb-4"
          >
            <span class="text-gray-600">차감될 포인트</span>
            <span class="font-bold text-lg text-red-500">{{
              selectedPerk?.price
            }}</span>
          </div>

          <div
            class="flex justify-between items-center bg-gray-50 rounded-lg p-4"
          >
            <span class="text-gray-600">구매 후 잔여 포인트</span>
            <span class="font-bold text-lg text-green-600">{{
              modalPoint !== null
                ? modalPoint - (selectedPerk?.price || 0)
                : "-"
            }}</span>
          </div>
        </div>

        <div
          v-if="errorMsg"
          class="bg-red-50 text-red-600 p-4 rounded-lg mb-6 text-center"
        >
          {{ errorMsg }}
        </div>

        <div class="flex gap-4">
          <button
            @click="showModal = false"
            class="flex-1 py-3 px-5 rounded-xl border border-gray-300 text-gray-700 font-medium hover:bg-gray-50 transition-colors"
          >
            취소
          </button>
          <button
            @click="handlePurchase"
            :disabled="isLoading"
            class="flex-1 py-3 px-5 rounded-xl bg-gradient-to-r from-blue-500 to-indigo-600 text-white font-medium hover:from-blue-600 hover:to-indigo-700 transition-colors disabled:opacity-70"
          >
            {{ isLoading ? "구매 중..." : "구매하기" }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.scale-100 {
  scale: 1;
}
</style>
