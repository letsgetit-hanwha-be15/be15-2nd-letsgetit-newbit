<script setup>
import { ref, onMounted } from 'vue'
import { getPublicSeriesList } from '@/api/column' // 실제 경로로 수정

const seriesList = ref([])

onMounted(async () => {
  try {
    const res = await getPublicSeriesList(0, 4)
    seriesList.value = res.data.data.content // 응답 구조에 따라 조정
  } catch (e) {
    console.error('시리즈 조회 실패:', e)
  }
})
</script>

<template>
  <div class="w-full h-full p-5 rounded-lg border border-gray-200 bg-white">
    <h2 class="text-heading3 text-newbittext font-bold mb-4">시리즈</h2>

    <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
      <div
          v-for="series in seriesList"
          :key="series.seriesId"
          class="rounded-xl overflow-hidden shadow border border-gray-100"
      >
        <!-- 썸네일 -->
        <div class="relative">
          <img
              :src="series.thumbnailUrl || '/assets/default-series.jpg'"
              alt="시리즈 썸네일"
              class="w-full h-[120px] object-cover"
          />
          <button
              class="absolute top-2 right-2 bg-blue-500 text-white text-xs px-3 py-1 rounded-full"
          >
            구독하기
          </button>
        </div>

        <!-- 텍스트 정보 -->
        <div class="p-3">
          <p class="text-xs text-gray-500 mb-1">
            {{ series.writer }} · {{ series.columnCount }}개의 칼럼
          </p>
          <p class="text-sm text-newbittext font-semibold leading-snug line-clamp-2">
            {{ series.title }}
          </p>
        </div>
      </div>
    </div>
  </div>
</template>
