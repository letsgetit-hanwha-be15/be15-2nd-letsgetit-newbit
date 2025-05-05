<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import SeriesEditModal from '@/features/column/components/SeriesEditModal.vue'

// 현재 로그인 유저가 이 시리즈의 멘토인지 여부 (임시)
const isMentor = ref(false)

const route = useRoute()
const seriesId = route.params.id

// 시리즈 상세 정보 및 칼럼 목록
const series = ref(null)
const columns = ref([])

const isEditModalOpen = ref(false)
const openEditModal = () => {
  isEditModalOpen.value = true
}

const fetchSeriesDetail = async () => {
  // TODO: 실제 API 연동
  series.value = {
    id: 1,
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
      date: '2025.04.02',
      likeCount: 10,
      diamondCount: 10,
      thumbnailUrl: 'https://example.com/thumb1.jpg',
      writer: '김멘토'
    },
    // 더미 데이터 계속
  ]
}

onMounted(fetchSeriesDetail)
</script>

<template>
  <section class="max-w-[1000px] mx-auto px-6 py-10">
    <!-- 상단 시리즈 정보 -->
    <div class="flex gap-6 items-start mb-10">
      <img
          :src="series?.thumbnailUrl || '/default.jpg'"
          class="w-[300px] h-[180px] object-cover rounded"
          alt="시리즈 썸네일"
      />
      <div class="flex-1">
        <h1 class="text-heading2 mb-2">{{ series?.title }}</h1>
        <p class="text-14px-regular text-[var(--newbitgray)] mb-3">
          {{ series?.description }}
        </p>
        <p class="text-13px-regular mb-4">{{ series?.columnCount }}개의 칼럼 | {{ series?.mentorNickname }}</p>

        <!-- 멘토/사용자 버튼 -->
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

    <!-- 칼럼 리스트 -->
    <div class="space-y-6">
      <div
          v-for="column in columns"
          :key="column.id"
          class="flex items-start justify-between p-5 border rounded shadow-sm"
      >
        <div class="flex-1 pr-4">
          <h2 class="text-heading3 mb-2">{{ column.title }}</h2>
          <p class="text-13px-regular text-[var(--newbitgray)]">
            ♥ {{ column.likeCount }} | 💎 {{ column.diamondCount }} | 작성일 {{ column.date }}
          </p>
        </div>
        <img
            :src="column.thumbnailUrl || '/default.jpg'"
            class="w-[180px] h-[120px] object-cover rounded"
            alt="칼럼 썸네일"
        />
      </div>
    </div>

    <!-- 시리즈 수정 모달 -->
    <SeriesEditModal
        v-model:visible="isEditModalOpen"
        :series="series"
        @edit="(updated) => series = updated"
    />
  </section>
</template>