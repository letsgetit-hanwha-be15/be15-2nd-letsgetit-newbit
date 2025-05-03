<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import ColumnCard from '@/features/column/components/ColumnCard.vue'

const router = useRouter()
const searchKeyword = ref('')
const isSubmitting = ref(false)
const error = ref(null)

// 테스트용 더미 데이터 (API 연동 전)
const columns = ref([
  {
    id: 1,
    title: '스펙업 절대 없는 위기 대응 전략',
    writer: '김멘토',
    date: '2025.07.02',
    diamondCount: 10,
    thumbnailUrl: 'https://via.placeholder.com/300x180'
  },
  {
    id: 2,
    title: '팀장 없어도 굴러가는 시스템 만들기',
    writer: '오멘토',
    date: '2025.06.13',
    diamondCount: 5,
    thumbnailUrl: '' // 썸네일 없음 -> 기본 이미지로 처리됨
  },
  {
    id: 3,
    title: '일의 맥락을 발견하는 5가지 방법',
    writer: '윤멘티',
    date: '2025.04.08',
    diamondCount: 5,
    thumbnailUrl: ''
  }
])

const filteredColumns = computed(() =>
    columns.value.filter(
        (c) =>
            c.title.includes(searchKeyword.value) ||
            c.writer.includes(searchKeyword.value)
    )
)

const handleSearch = () => {
  console.log('검색:', searchKeyword.value)
}

const onClickCreate = () => {

  router.push('/columns/requests')

}
</script>

<template>
  <section class="px-6 py-8">
    <!-- 탭 -->
    <div class="flex gap-6 mb-6 border-b border-[var(--newbitdivider)] text-13px-regular">
      <span class="pb-2 border-b-2 border-[var(--newbitnormal)] text-[var(--newbitnormal)] font-bold cursor-pointer">
        칼럼
      </span>
      <span class="pb-2 text-[var(--newbitgray)] cursor-pointer">시리즈</span>
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
    <div class="space-y-6">
      <ColumnCard
          v-for="column in filteredColumns"
          :key="column.id"
          :column="column"
      />
    </div>

    <!-- 페이지네이션 -->
    <div class="flex justify-center gap-2 mt-10 text-13px-regular text-[var(--newbitgray)]">
      <span class="cursor-pointer">← Previous</span>
      <span class="font-bold text-[var(--newbitnormal)]">1</span>
      <span class="cursor-pointer">2</span>
      <span class="cursor-pointer">3</span>
      <span class="cursor-pointer">Next →</span>
    </div>

    <!-- 에러 메시지 -->
    <div v-if="error" class="text-red-500 mt-4">{{ error }}</div>
  </section>
</template>

<style scoped>
</style>