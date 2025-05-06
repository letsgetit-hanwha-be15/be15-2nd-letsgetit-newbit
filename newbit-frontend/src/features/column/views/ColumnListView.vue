<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import ColumnCard from '@/features/column/components/ColumnCard.vue'
import PagingBar from "@/components/common/PagingBar.vue"
import {
  getPublicColumnList,
  searchPublicColumns
} from '@/api/column.js'

const router = useRouter()
const columns = ref([])
const currentPage = ref(1)
const totalPage = ref(1)
const searchKeyword = ref('')
const error = ref(null)

const fetchColumns = async () => {
  try {
    let res
    const page = currentPage.value - 1 // 백엔드는 0부터 시작
    const size = 5

    if (searchKeyword.value.trim()) {
      res = await searchPublicColumns({ keyword: searchKeyword.value }, page, size)
    } else {
      res = await getPublicColumnList(page, size)
    }

    const content = res.data.content || []
    columns.value = content.map(col => ({
      id: col.columnId,
      title: col.title,
      thumbnailUrl: col.thumbnailUrl,
      mentorNickname: col.mentorNickname,
      diamondCount: col.price,
      likeCount: col.likeCount,
      createdAt: col.createdAt // createdAt 필드가 없으면 기본값
    }))
    totalPage.value = res.data.totalPages
    error.value = null
  } catch (e) {
    console.error(e)
    error.value = '칼럼 목록을 불러오는 데 실패했습니다.'
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchColumns()
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchColumns()
}

const onClickCreate = () => {
  router.push('/columns/requests')
}

const goToSeries = () => {
  router.push('/series')
}

onMounted(() => {
  fetchColumns()
})
</script>

<template>
  <section class="px-6 py-8">
    <!-- 탭 -->
    <div class="flex gap-6 mb-6 border-b border-[var(--newbitdivider)] text-13px-regular">
      <span class="pb-2 border-b-2 border-[var(--newbitnormal)] text-[var(--newbitnormal)] font-bold cursor-pointer">
        칼럼
      </span>
      <span class="pb-2 text-[var(--newbitgray)] cursor-pointer" @click="goToSeries">
        시리즈
      </span>
    </div>

    <!-- 검색 + 등록 버튼 -->
    <div class="flex justify-between items-center mb-6">
      <div class="flex gap-2 flex-1">
        <input
            v-model="searchKeyword"
            type="text"
            placeholder="작성자나 제목으로 검색해보세요"
            class="flex-1 border border-[var(--newbitdivider)] rounded px-4 py-2 text-13px-regular"
        />
        <button
            @click="handleSearch"
            class="bg-[var(--newbitnormal)] text-white px-4 py-2 rounded text-13px-bold"
        >
          검색
        </button>
      </div>
      <button
          @click="onClickCreate"
          class="bg-[var(--newbitnormal)] text-white px-4 py-2 rounded text-13px-bold ml-4 whitespace-nowrap"
      >
        칼럼 등록
      </button>
    </div>

    <!-- 칼럼 카드 리스트 -->
    <div v-if="columns.length > 0" class="space-y-6">
      <ColumnCard
          v-for="column in columns"
          :key="column.id"
          :column="column"
      />
    </div>
    <div v-else class="text-center text-[var(--newbitgray)] py-20">
      검색 결과가 없습니다.
    </div>

    <!-- 페이지네이션 -->
    <PagingBar
        :currentPage="currentPage"
        :totalPage="totalPage"
        @page-change="handlePageChange"
    />

    <!-- 에러 메시지 -->
    <div v-if="error" class="text-red-500 mt-4 text-center">{{ error }}</div>
  </section>
</template>

<style scoped>
</style>