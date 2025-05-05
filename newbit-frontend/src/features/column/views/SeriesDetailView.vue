<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import SeriesEditModal from '@/features/column/components/SeriesEditModal.vue'
import ColumnCard from '@/features/column/components/ColumnCard.vue'

const isMentor = ref(true) // 실제로는 로그인 사용자와 비교해서 설정
const route = useRoute()
const seriesId = route.params.id

const series = ref(null)
const columns = ref([])
const isEditModalOpen = ref(false)

const openEditModal = () => {
  isEditModalOpen.value = true
}

const fallbackImg = new URL('@/assets/image/product-skeleton.png', import.meta.url).href

const displayThumbnail = computed(() =>
    series.value?.thumbnailUrl?.trim() !== ''
        ? series.value.thumbnailUrl
        : fallbackImg
)

const fetchSeriesDetail = async () => {
  series.value = {
    id: Number(seriesId),
    title: 'AI 시대에 내 몸값을 높여줄 5가지 습관',
    description: '시리즈 설명 예시입니다',
    thumbnailUrl: '',
    columnCount: 3,
    subscribed: true,
    mentorNickname: '김멘토'
  }

  columns.value = [
    {
      id: 1,
      title: '스펙의 전례 없는 위기 대응 전략',
      mentorNickname: '김멘토',
      date: '2025.04.02',
      likeCount: 10,
      diamondCount: 10,
      thumbnailUrl: '', // fallback 처리
    },
    {
      id: 2,
      title: '팀 없이도 굴러가는 시스템 만들기',
      mentorNickname: '김멘토',
      date: '2025.04.10',
      likeCount: 8,
      diamondCount: 5,
      thumbnailUrl: '',
    }
  ]
}

onMounted(fetchSeriesDetail)
</script>

<template>
  <section class="max-w-[1000px] mx-auto px-6 py-10">
    <!-- 시리즈 정보 -->
    <div class="flex gap-6 items-start mb-10">
      <img
          :src="series?.thumbnailUrl || fallbackImg"
          @error="(e) => e.target.src = fallbackImg"
          alt="시리즈 썸네일"
          class="w-[300px] h-[180px] object-cover rounded-lg"
      />
      <div class="flex-1">
        <h1 class="text-heading2 mb-2">{{ series?.title }}</h1>
        <p class="text-14px-regular text-[var(--newbitgray)] mb-3">{{ series?.description }}</p>
        <p class="text-13px-regular mb-4">
          {{ series?.columnCount }}개의 칼럼 | {{ series?.mentorNickname }}
        </p>

        <button
            v-if="isMentor"
            @click="openEditModal"
            class="px-4 py-2 bg-[var(--newbitnormal)] text-white text-13px-bold rounded"
        >
          시리즈 수정
        </button>
        <button
            v-else
            @click="series.subscribed = !series.subscribed"
            class="px-4 py-2 rounded text-white text-13px-bold"
            :class="series?.subscribed ? 'bg-[var(--newbitred)]' : 'bg-blue-500'"
        >
          {{ series?.subscribed ? '구독 취소' : '구독하기' }}
        </button>
      </div>
    </div>

    <!-- 칼럼 카드 리스트 -->
    <div class="space-y-6">
      <ColumnCard
          v-for="column in columns"
          :key="column.id"
          :column="column"
      />
    </div>

    <!-- 시리즈 수정 모달 -->
    <SeriesEditModal
        v-model="isEditModalOpen"
        :series="series"
        @update="(updated) => (series = updated)"
    />
  </section>
</template>
