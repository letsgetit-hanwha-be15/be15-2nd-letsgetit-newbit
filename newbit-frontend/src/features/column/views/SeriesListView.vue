<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import SeriesCard from '@/features/column/components/SeriesCard.vue'
import PagingBar from '@/components/common/PagingBar.vue'
import SeriesCreateModal from '@/features/column/components/SeriesCreateModal.vue'

// 상태
const searchKeyword = ref('')
const currentPage = ref(1)
const totalPage = 3
const isCreateModalOpen = ref(false)

const isMyPage = false // TODO: 로그인한 유저의 멘토 여부 확인 후 수정

// 임시 시리즈 목록
const allSeries = ref([
  {
    id: 1,
    title: '시리즈 제목',
    mentorNickname: '오멘토',
    columnCount: 3,
    thumbnailUrl: '',
    subscribed: true
  },
  {
    id: 2,
    title: '버텨야 할 때와 그만두어야 할 때를 구분하기',
    mentorNickname: 'Onda',
    columnCount: 3,
    thumbnailUrl: '',
    subscribed: false
  }
])

// 검색 + 공개 조건 필터링
const filteredSeries = computed(() =>
    allSeries.value
        .filter(s =>
            s.title.includes(searchKeyword.value) || s.mentorNickname.includes(searchKeyword.value)
        )
        .filter(s => isMyPage || s.columnCount > 0)
)

// 이벤트 핸들러
const handleSearch = () => {
  console.log('검색어:', searchKeyword.value)
}

const handlePageChange = (page) => {
  currentPage.value = page
}

const openCreateModal = () => {
  isCreateModalOpen.value = true
}

const handleCreateSeries = (payload) => {
  console.log('생성된 시리즈:', payload)
  // TODO: API 호출 및 리스트 갱신
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
          : 'text-[var(--newbitgray)]'"
      >
        칼럼
      </router-link>
      <router-link
          to="/series"
          class="pb-2 cursor-pointer"
          :class="$route.path.startsWith('/series')
          ? 'border-b-2 border-[var(--newbitnormal)] text-[var(--newbitnormal)] font-bold'
          : 'text-[var(--newbitgray)]'"
      >
        시리즈
      </router-link>
    </div>

    <!-- 검색창 + 발행 버튼 -->
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
          @click="openCreateModal"
          class="bg-[var(--newbitnormal)] text-white px-4 py-2 rounded text-13px-bold ml-4 whitespace-nowrap"
      >
        시리즈 발행
      </button>
    </div>

    <!-- 카드 리스트 -->
    <div class="grid grid-cols-3 gap-6">
      <SeriesCard
          v-for="series in filteredSeries"
          :key="series.id"
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
