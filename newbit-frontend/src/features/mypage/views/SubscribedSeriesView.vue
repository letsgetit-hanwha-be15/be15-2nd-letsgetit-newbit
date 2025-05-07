<script setup>
import { onMounted, ref } from 'vue'
import { getSubscribedSeries } from '@/api/column.js'
import SeriesCard from '@/features/column/components/SeriesCard.vue' // 카드 컴포넌트

const seriesList = ref([])

const fetchSubscribedSeries = async () => {
  try {
    const res = await getSubscribedSeries()

    // 응답이 배열인지 확인 후 바로 할당
    if (Array.isArray(res.data)) {
      seriesList.value = res.data
    } else {
      console.warn('예상과 다른 응답 형식:', res.data)
    }
  } catch (e) {
    console.error('구독한 시리즈 조회 실패', e)
  }
}

onMounted(fetchSubscribedSeries)
</script>

<template>
  <div class="p-4">
    <h2 class="text-18px-bold mb-4">구독한 시리즈</h2>

    <div v-if="seriesList.length === 0" class="text-gray-400">
      아직 구독한 시리즈가 없습니다.
    </div>

    <div v-else class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6">
      <SeriesCard
          v-for="series in seriesList"
          :key="series.seriesId"
          :series="series"
      />
    </div>
  </div>
</template>

<style scoped>
.text-18px-bold {
  font-size: 18px;
  font-weight: 700;
}
</style>