<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import dayjs from 'dayjs'
import { useAuthStore } from '@/features/stores/auth'
import { getColumnDetail } from '@/api/column'
import { purchaseColumn } from '@/api/purchase'
import {getUserBalance} from "@/api/user.js";

const authStore = useAuthStore()
const router = useRouter()
const route = useRoute()

const columnId = Number(route.params.id)
const userId = authStore.userId

const column = ref(null)
const isLiked = ref(false)
const isDeleteModalVisible = ref(false)
const showPurchaseModal = ref(false)
const isLoading = ref(false);
const errorMsg = ref("");
const modalPoint = ref(null);
const modalDiamond = ref(null);

const fallbackImg = new URL('@/assets/image/product-skeleton.png', import.meta.url).href
const heartDefault = new URL('@/assets/image/heart-default.png', import.meta.url).href
const heartActive = new URL('@/assets/image/heart-active.png', import.meta.url).href

const isMentor = authStore.userRole === 'MENTOR'

const toggleLike = () => {
  isLiked.value = !isLiked.value
  column.value.likeCount += isLiked.value ? 1 : -1
}

const goToEdit = () => {
  router.push(`/columns/edit/${columnId}`)
}

const formattedDate = computed(() =>
    column.value?.createdAt ? dayjs(column.value.createdAt).format('YYYY.MM.DD') : ''
)

const confirmDelete = () => {
  alert('삭제 요청이 전송되었습니다.')
  isDeleteModalVisible.value = false
}

const cancelPurchase = () => {
  showPurchaseModal.value = false
  router.push('/columns')
}

const confirmPurchase = async () => {
  isLoading.value = true;
  errorMsg.value = "";
  try {
    await purchaseColumn({ columnId: Number(columnId) })
    try {
      const res = await getUserBalance();
      if (res.data && res.data.success) {
        const { pointBalance, diamondBalance } = res.data.data || {};
        modalPoint.value = pointBalance;
        modalDiamond.value = diamondBalance;
      }
    } catch (e) {}
    showPurchaseModal.value = false
    await fetchColumnDetail()
  } catch (e) {
    console.error('구매 실패', e)
    alert('구매에 실패했습니다.')
  }
}

const fetchColumnDetail = async () => {
  try {
    const res = await getColumnDetail(columnId, userId)
    column.value = res.data
  } catch (err) {
    console.error('칼럼 상세 조회 실패', err)
    if (err.response?.data?.errorCode === '60004') {
      await openModal();
    }
  }
}

const openModal = async () => {
  showPurchaseModal.value = true
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



onMounted(fetchColumnDetail)
</script>

<template>
  <div
      v-if="showPurchaseModal"
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
        <h2 class="text-xl font-bold text-gray-800">칼럼을 구매하시겠습니까?</h2>
      </div>


      <div
          v-if="errorMsg"
          class="bg-red-50 text-red-600 p-4 rounded-lg mb-6 text-center"
      >
        {{ errorMsg }}
      </div>

      <div class="flex gap-4">
        <button
            @click="cancelPurchase"
            class="flex-1 py-3 px-5 rounded-xl border border-gray-300 text-gray-700 font-medium hover:bg-gray-50 transition-colors"
        >
          취소
        </button>
        <button
            @click="confirmPurchase"
            :disabled="isLoading"
            class="flex-1 py-3 px-5 rounded-xl bg-[var(--newbitnormal)] text-white font-medium hover:bg-[var(--newbitnormal-hover)] disabled:opacity-70"
        >
          {{ isLoading ? "구매 중..." : "구매하기" }}
        </button>
      </div>
    </div>
  </div>

  <div class="max-w-[900px] mx-auto py-8 px-4" v-if="column">
    <!-- 목록으로 -->
    <router-link
        to="/columns"
        class="inline-flex items-center gap-2 text-[var(--newbittext)] text-13px-regular bg-[var(--newbitlightmode)] border border-[var(--newbitdivider)] px-4 py-2 rounded-lg shadow-sm hover:bg-[var(--newbitlightmode-hover)] transition mb-6"
    >
      <span class="text-xl">←</span>
      <span>목록으로</span>
    </router-link>

    <!-- 썸네일 + 텍스트 -->
    <div class="flex gap-6 mb-6">
      <img
          :src="column.thumbnailUrl || fallbackImg"
          @error="(e) => (e.target.src = fallbackImg)"
          class="w-[280px] h-[180px] rounded-lg object-cover bg-gray-100"
          alt="썸네일"
      />
      <div class="flex flex-col justify-between h-[180px] flex-1">
        <h1 class="text-heading2">{{ column.title }}</h1>
        <div class="flex flex-col gap-2.5 text-13px-regular text-[var(--newbitgray)]">
          <span>멘토 {{ column.mentorNickname }}</span>
          <span>작성일 {{ formattedDate }}</span>
          <button
              @click="toggleLike"
              class="flex items-center gap-1 px-3 py-1 w-fit border border-[var(--newbitdivider)] rounded-md text-[var(--newbittext)] hover:bg-[var(--newbitlightmode-hover)] transition"
          >
            <img :src="isLiked ? heartActive : heartDefault" class="w-5 h-4.5" alt="하트" />
            <span>{{ column.likeCount }}</span>
          </button>
        </div>
      </div>
    </div>

    <!-- 본문 -->
    <div class="bg-[var(--newbitlightmode)] p-6 rounded-lg whitespace-pre-wrap text-16px-regular leading-relaxed border border-[var(--newbitdivider)]">
      {{ column.content }}
    </div>

    <!-- 멘토 전용 버튼 -->
    <div v-if="isMentor" class="flex justify-end gap-2 mt-6">
      <button @click="goToEdit" class="bg-blue-500 text-white px-4 py-2 rounded">수정</button>
      <button @click="() => isDeleteModalVisible = true" class="bg-[var(--newbitred)] text-white px-4 py-2 rounded">삭제</button>
    </div>

    <!-- 삭제 모달 -->
    <div
        v-if="isDeleteModalVisible"
        class="fixed inset-0 bg-black bg-opacity-30 flex items-center justify-center z-50"
    >
      <div class="bg-white p-6 rounded-lg w-[380px] text-center shadow-lg">
        <h2 class="text-heading4 mb-3">칼럼 삭제</h2>
        <p class="text-14px-regular text-[var(--newbitgray)] mb-5">
          해당 컨텐츠에 대해 삭제 요청을 보내시겠습니까?
        </p>
        <div class="flex justify-end gap-2">
          <button @click="isDeleteModalVisible = false" class="bg-[var(--newbitred)] text-white px-4 py-2 rounded">아니요</button>
          <button @click="confirmDelete" class="bg-blue-500 text-white px-4 py-2 rounded">네</button>
        </div>
      </div>
    </div>
  </div>

  <div v-else class="text-center py-20 text-[var(--newbitgray)]">칼럼을 불러오는 중입니다...</div>
</template>

<style scoped></style>