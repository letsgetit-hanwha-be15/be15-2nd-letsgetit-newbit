<script setup>
import { ref } from 'vue'

defineProps({
  column: {
    type: Object,
    required: true
  }
})

// 상태 관리
const isLiked = ref(false)
const toggleLike = () => {
  isLiked.value = !isLiked.value
}

// 이미지 경로
const fallbackImg = new URL('@/assets/image/product-skeleton.png', import.meta.url).href
const heartDefault = new URL('@/assets/image/heart-default.png', import.meta.url).href
const heartActive = new URL('@/assets/image/heart-active.png', import.meta.url).href
const diamondIcon = new URL('@/assets/image/diamond-icon.png', import.meta.url).href
</script>

<template>
  <div class="flex justify-between items-start p-5 border border-[var(--newbitdivider)] rounded-lg shadow-sm">
    <!-- 텍스트 -->
    <div class="flex flex-col justify-between flex-1 pr-4">
      <!-- 제목 -->
      <h2 class="text-heading3 mb-20">
        {{ column.title }}
      </h2>

      <!-- 다이아 -->
      <div class="flex items-center gap-1 text-13px-regular text-[var(--newbitdark)] mb-4">
        <img :src="diamondIcon" alt="다이아" class="w-4 h-4" />
        <span>{{ column.likeCount }}</span>
      </div>

      <!-- 좋아요 + 작성자 + 날짜 -->
      <div class="flex items-center gap-3 text-13px-regular text-[var(--newbitgray)]">
        <!-- 하트 버튼 -->
        <button @click="toggleLike" class="w-5 h-4.5">
          <img :src="isLiked ? heartActive : heartDefault" alt="좋아요 하트" class="w-full h-full" />
        </button>

        <!-- 작성자 · 작성일 -->
        <span>{{ column.writer }} | 작성일 {{ column.date }}</span>
      </div>
    </div>

    <!-- 썸네일 -->
    <div class="w-[300px] h-[180px]">
      <img
          :src="column.thumbnailUrl || fallbackImg"
          @error="(e) => (e.target.src = fallbackImg)"
          alt="thumbnail"
          class="w-full h-full object-cover rounded-lg"
      />
    </div>
  </div>
</template>

<style scoped>
</style>
