<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getMentorSeriesList } from '@/api/column'
import SeriesCard from "@/features/column/components/SeriesCard.vue"
import PagingBar from "@/components/common/PagingBar.vue"

// 라우트에서 mentorId 가져오기
const route = useRoute()
const mentorId = Number(route.params.id)

// 상태 변수
const seriesList = ref([])
const currentPage = ref(1)
const totalPage = ref(1)
const pageSize = 6 // 페이지당 3개

// 시리즈 목록 불러오기
const fetchSeriesList = async (page = 1) => {
  try {
    const res = await getMentorSeriesList(mentorId, page - 1, pageSize)
    const data = res.data.data

    seriesList.value = data.content
    totalPage.value = data.totalPages
    currentPage.value = data.number + 1
  } catch (e) {
    console.error('시리즈 목록 조회 실패:', e)
  }
}

// 페이지 변경 핸들러
const handlePageChange = (page) => {
  fetchSeriesList(page)
}

// 최초 로딩
onMounted(() => {
  fetchSeriesList()
})
</script>

<template>
  <div class="w-full max-w-6xl mx-auto p-6">
    <div v-if="seriesList.length > 0" class="grid grid-cols-3 gap-6">
      <SeriesCard
          v-for="series in seriesList"
          :key="series.seriesId"
          :series="series"
      />
    </div>

    <div v-else class="text-gray-500 text-sm py-10">작성한 시리즈가 없습니다.</div>

    <PagingBar
        class="mt-8"
        :current-page="currentPage"
        :total-pages="totalPage"
        @page-change="handlePageChange"
    />
  </div>
</template>
