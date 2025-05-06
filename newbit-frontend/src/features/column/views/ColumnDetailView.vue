<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getColumnDetail } from '@/api/column.js'
import dayjs from 'dayjs'
import { useAuthStore } from '@/features/stores/auth'

const authStore = useAuthStore()

const route = useRoute()
const router = useRouter()

const columnId = Number(route.params.id)
const userId = authStore.userId || 12;
// const userId = 12;
const column = ref(null)

const isMentor = authStore.userRole === 'MENTOR'
// const isOwner = computed(() => column.value?.mentorId === authStore.mentorId)  // 추후에 적용

const isLiked = ref(false)
const toggleLike = () => {
  isLiked.value = !isLiked.value
  column.value.likeCount += isLiked.value ? 1 : -1
}

const fallbackImg = new URL('@/assets/image/product-skeleton.png', import.meta.url).href
const heartDefault = new URL('@/assets/image/heart-default.png', import.meta.url).href
const heartActive = new URL('@/assets/image/heart-active.png', import.meta.url).href

const isDeleteModalVisible = ref(false)
const confirmDelete = () => {
  alert('삭제 요청이 전송되었습니다.')
  isDeleteModalVisible.value = false
}

const goToEdit = () => {
  router.push(`/columns/edit/${columnId}`)
}

const formattedDate = computed(() => {
  return column.value?.createdAt
      ? dayjs(column.value.createdAt).format('YYYY.MM.DD')
      : ''
})

onMounted(async () => {
  try {
    const res = await getColumnDetail(columnId, userId)
    column.value = res.data
  } catch (err) {
    console.error('칼럼 상세 조회 실패', err)
  }
})
</script>

<template>
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
    <div v-if="isDeleteModalVisible" class="fixed inset-0 bg-black bg-opacity-30 flex items-center justify-center z-50">
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