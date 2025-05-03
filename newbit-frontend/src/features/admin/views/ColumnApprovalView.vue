<template>
  <div class="column-approval-page">
    <h2 class="text-heading2 mb-6">칼럼 요청 관리</h2>

    <!-- 탭 -->
    <div class="tab-wrapper mb-6 flex gap-6 border-b border-[var(--newbitdivider)]">
      <button
          v-for="tab in tabs"
          :key="tab"
          @click="selectedTab = tab"
          :class="[
          'pb-2 text-13px-bold',
          selectedTab === tab
            ? 'text-[var(--newbitnormal)] border-b-2 border-[var(--newbitnormal)]'
            : 'text-[var(--newbitgray)]'
        ]"
      >
        {{ tab }}
      </button>
    </div>

    <!-- 칼럼 카드 목록 -->
    <div class="flex flex-col gap-6">
      <div
          v-for="column in filteredColumns"
          :key="column.id"
          class="flex justify-between items-start p-5 border border-[var(--newbitdivider)] rounded-lg shadow-sm bg-[var(--newbitlightmode)]"
      >
        <!-- 텍스트 -->
        <div class="flex flex-col justify-between flex-1 pr-4">
          <!-- 제목 -->
          <h2 class="text-heading3 mb-8">
            [{{ column.category }}] {{ column.title }}
          </h2>

          <!-- 다이아몬드 개수 -->
          <div class="flex items-center gap-1 text-13px-regular text-[var(--newbitdark)] mb-2">
            <img :src="diamondIcon" alt="다이아" class="w-4 h-4" />
            <span>{{ column.diamondCount }}</span>
          </div>

          <!-- 작성자 + 작성일 -->
          <div class="text-13px-regular text-[var(--newbitgray)] mb-2">
            {{ column.writer }} | 작성일 {{ column.date }}
          </div>

          <!-- 상세보기 버튼 -->
          <button
              class="bg-[var(--newbitnormal)] text-white px-3 py-1.5 rounded text-13px-regular w-fit"
              @click="handleDetailClick(column.id)"
          >
            상세보기
          </button>
        </div>

        <!-- 썸네일 -->
        <div class="w-[240px] h-[140px]">
          <img
              :src="column.thumbnailUrl || fallbackImg"
              @error="(e) => (e.target.src = fallbackImg)"
              alt="썸네일"
              class="w-full h-full object-cover rounded-md"
          />
        </div>
      </div>
    </div>

    <div class="flex justify-center gap-2 mt-10 text-13px-regular text-[var(--newbitgray)]">
      <span class="cursor-pointer">← Previous</span>
      <span class="font-bold text-[var(--newbitnormal)]">1</span>
      <span class="cursor-pointer">2</span>
      <span class="cursor-pointer">3</span>
      <span class="cursor-pointer">Next →</span>
    </div>

  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

// 탭 상태
const tabs = ['등록', '수정', '삭제']
const selectedTab = ref('등록')

// 다이아몬드 아이콘 및 기본 썸네일
const diamondIcon = new URL('@/assets/image/diamond-icon.png', import.meta.url).href
const fallbackImg = new URL('@/assets/image/product-skeleton.png', import.meta.url).href

// 더미 데이터 (후에 API 연동 예정)
const allColumns = ref([
  {
    id: 1,
    title: '스펙의 전례 없는 위기 대응 전략: "통제할 수 있는 것에 집중하자"',
    category: '인터뷰',
    diamondCount: 10,
    writer: '오멘토',
    date: '2025.04.02',
    thumbnailUrl: 'https://via.placeholder.com/160x100?text=썸네일1',
    type: '등록'
  },
  {
    id: 2,
    title: '팀장 없이도 굴러가는 시스템 만들기',
    category: '트러블슈팅',
    diamondCount: 5,
    writer: '트랄랄렐로',
    date: '2025.04.02',
    thumbnailUrl: 'https://via.placeholder.com/160x100?text=썸네일2',
    type: '등록'
  },
  {
    id: 3,
    title: '일의 "맥락"을 발견하는 다섯 가지 방법',
    category: '오피니언',
    diamondCount: 5,
    writer: '신데렐라',
    date: '2025.04.02',
    thumbnailUrl: 'https://via.placeholder.com/160x100?text=썸네일3',
    type: '등록'
  },
  {
    id: 4,
    title: '협업에서 마찰 없이 의견을 전달하는 방법',
    category: '커뮤니케이션',
    diamondCount: 8,
    writer: '멘토링마스터',
    date: '2025.04.03',
    thumbnailUrl: '',
    type: '수정'
  },
  {
    id: 5,
    title: '퇴사 전에 꼭 점검해야 할 체크리스트',
    category: '라이프',
    diamondCount: 3,
    writer: '파이널보스',
    date: '2025.04.04',
    thumbnailUrl: '',
    type: '삭제'
  }
])

// 필터링된 칼럼 리스트
const filteredColumns = computed(() =>
    allColumns.value.filter((col) => col.type === selectedTab.value)
)

// 상세보기 클릭 시 (임시)
const handleDetailClick = (id) => {
  alert(`상세보기 클릭: ${id}`)
}
</script>

<style scoped>
</style>
