<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getMentorColumnList } from '@/api/column'
import ColumnCard from "@/features/column/components/ColumnCard.vue"
import PagingBar from "@/components/common/PagingBar.vue"

// 라우트에서 mentorId 가져오기 (e.g. /mentors/:id)
const route = useRoute()
const mentorId = Number(route.params.id)

// 상태 변수
const myColumns = ref([])
const currentPage = ref(1)
const totalPages = ref(1)

const pageSize = 3 // 한 페이지당 3개

const getColumns = async (page = 1) => {
  try {
    const res = await getMentorColumnList(mentorId, page - 1, pageSize)
    const data = res.data.data

    myColumns.value = data.content
    totalPages.value = data.totalPages
    currentPage.value = data.number + 1
  } catch (e) {
    console.error('칼럼 목록 조회 실패:', e)
  }
}

// 페이지 변경 핸들러
const handlePageChange = (page) => {
  getColumns(page)
}

// 최초 로딩
onMounted(() => {
  getColumns()
})
</script>

<template>
  <div class="w-full max-w-4xl mx-auto p-6">
    <div v-if="myColumns.length > 0" class="space-y-6">
      <ColumnCard
          v-for="column in myColumns"
          :key="column.columnId"
          :column="column"
      />
    </div>

    <div v-else class="text-gray-500 text-sm py-10">작성한 칼럼이 없습니다.</div>

    <PagingBar
        class="mt-8"
        :current-page="currentPage"
        :total-pages="totalPages"
        @page-change="handlePageChange"
    />
  </div>
</template>
