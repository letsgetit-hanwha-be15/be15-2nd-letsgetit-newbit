<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getPublicSeriesList, searchPublicSeriesList } from '@/api/column'
import SeriesCard from '@/features/column/components/SeriesCard.vue'
import PagingBar from '@/components/common/PagingBar.vue'
import SeriesCreateModal from '@/features/column/components/SeriesCreateModal.vue'

// 상태
const searchKeyword = ref('')
const currentPage = ref(1)
const totalPage = ref(0)
const isCreateModalOpen = ref(false)
const allSeries = ref([])
const isLoading = ref(false)

const isMyPage = false // TODO: 로그인한 유저의 멘토 여부 확인 후 수정

const fetchSeriesList = async () => {
  isLoading.value = true
  try {
    const res = searchKeyword.value.trim()
        ? await searchPublicSeriesList({ keyword: searchKeyword.value }, currentPage.value - 1, 9)
        : await getPublicSeriesList(currentPage.value - 1, 9)

    allSeries.value = res.data.data.content
    totalPage.value = res.data.totalPages
  } catch (err) {
    console.error('시리즈 목록 조회 실패:', err)
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  fetchSeriesList()
})

const handleSearch = () => {
  currentPage.value = 1
  fetchSeriesList()
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchSeriesList()
}

const openCreateModal = () => {
  isCreateModalOpen.value = true
}

const handleCreateSeries = (payload) => {
  console.log('생성된 시리즈:', payload)
  fetchSeriesList()
}
</script>

<template>
  <section class="px-6 py-8 max-w-[1100px] mx-auto">
    <!-- 탭 -->
    <div class="flex gap-6 mb-6 border-b border-[var(--newbitdivider)] text-13px-regular">
      <router-link
          to="/columns"
          class="pb-2 cursor-pointer"
          :class="$route.path === '/columns'
          ? 'border-b-2 border-[var(--newbitnormal)] text-[var(--newbitnormal)] font-bold'
          : 'text-[var(--newbitgray)]'">
        칼럼
      </router-link>
      <router-link
          to="/series"
          class="pb-2 cursor-pointer"
          :class="$route.path.startsWith('/series')
          ? 'border-b-2 border-[var(--newbitnormal)] text-[var(--newbitnormal)] font-bold'
          : 'text-[var(--newbitgray)]'">
        시리즈
      </router-link>
    </div>

    <!-- 검색창 + 발행 버튼 -->
    <div class="flex justify-between items-center mb-6">
      <div class="flex gap-2 flex-1">
        <input
            v-model="searchKeyword"
            type="text"
            placeholder="시리즈 제목으로 검색해보세요"
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
          @click="openCreateModal"
          class="bg-[var(--newbitnormal)] text-white px-4 py-2 rounded text-13px-bold ml-4 whitespace-nowrap"
      >
        시리즈 발행
      </button>
    </div>

    <!-- 카드 리스트 -->
    <div class="grid grid-cols-3 gap-6">
      <SeriesCard
          v-for="series in allSeries"
          :key="series.seriesId"
          :series="series"
      />
    </div>

    <!-- 페이지네이션 -->
    <PagingBar
        :currentPage="currentPage"
        :totalPage="totalPage"
        @page-change="handlePageChange"
    />

    <!-- 발행 모달 -->
    <SeriesCreateModal
        v-model:modelValue="isCreateModalOpen"
        @create="handleCreateSeries"
    />
  </section>
</template>
