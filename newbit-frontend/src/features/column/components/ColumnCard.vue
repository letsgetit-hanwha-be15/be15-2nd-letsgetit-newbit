<script setup>
import { computed, ref } from 'vue'
import dayjs from 'dayjs'

const props = defineProps({
  column: {
    type: Object,
    required: true
  }
})

// 좋아요 상태
const isLiked = ref(false)
const toggleLike = () => {
  isLiked.value = !isLiked.value
}

// 이미지
const fallbackImg = new URL('@/assets/image/product-skeleton.png', import.meta.url).href
const heartDefault = new URL('@/assets/image/heart-default.png', import.meta.url).href
const heartActive = new URL('@/assets/image/heart-active.png', import.meta.url).href
const diamondIcon = new URL('@/assets/image/diamond-icon.png', import.meta.url).href

const formattedDate = computed(() => {
  return props.column.createdAt
      ? dayjs(new Date(props.column.createdAt)).format('YYYY.MM.DD')
      : ''
})

</script>

<template>
  <div class="flex justify-between items-start p-5 border border-[var(--newbitdivider)] rounded-lg shadow-sm">
    <!-- 텍스트 -->
    <div class="flex flex-col justify-between flex-1 pr-4">
      <!-- 제목 -->
      <div class="mb-20">
        <h2 class="text-heading3">{{ column.title }}</h2>
      </div>

      <!-- 다이아 가격 -->
      <div v-if="column.diamondCount !== undefined" class="flex items-center gap-1 text-13px-regular text-[var(--newbitdark)] mb-4">
        <img :src="diamondIcon" alt="다이아" class="w-4 h-4" />
        <span>{{ column.diamondCount }}</span>
      </div>

      <!-- 좋아요 + 작성자 + 날짜 -->
      <div class="flex items-center gap-3 text-13px-regular text-[var(--newbitgray)]">
        <!-- 하트 -->
        <button v-if="column.likeCount !== undefined" @click="toggleLike" class="w-5 h-4.5">
          <img :src="isLiked ? heartActive : heartDefault" alt="하트" class="w-full h-full" />
        </button>

        <!-- 작성자와 작성일 -->
        <span>
          {{ column.mentorNickname }}
          <template v-if="formattedDate"> | 작성 일시 {{ formattedDate }}</template>
        </span>
      </div>
    </div>

    <!-- 썸네일 이미지 -->
    <div class="w-[300px] h-[180px]">
      <img
          :src="column.thumbnailUrl || fallbackImg"
          @error="(e) => (e.target.src = fallbackImg)"
          alt="썸네일"
          class="w-full h-full object-cover rounded-lg"
      />
    </div>
  </div>
</template>

<style scoped>
</style>
