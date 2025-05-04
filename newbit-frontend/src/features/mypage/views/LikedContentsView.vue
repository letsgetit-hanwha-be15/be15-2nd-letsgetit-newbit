<script setup>
import {ref} from "vue";
import ColumnCard from "@/features/column/components/ColumnCard.vue";
import PostTable from "@/features/post/components/PostTable.vue";
import PagingBar from "@/features/post/components/PagingBar.vue";

const selectedTab = ref("column")

const columns = ref([
  {
    id: 1,
    title: '스펙업 절대 없는 위기 대응 전략',
    writer: '김멘토',
    date: '2025.07.02',
    likeCount: 10,
    thumbnailUrl: 'https://via.placeholder.com/300x180'
  },
  {
    id: 2,
    title: '팀장 없어도 굴러가는 시스템 만들기',
    writer: '오멘토',
    date: '2025.06.13',
    likeCount: 5,
    thumbnailUrl: '' // 썸네일 없음 -> 기본 이미지로 처리됨
  },
  {
    id: 3,
    title: '일의 맥락을 발견하는 5가지 방법',
    writer: '윤멘티',
    date: '2025.04.08',
    likeCount: 5,
    thumbnailUrl: ''
  }
])

const posts = ref([
  { id: 2, title: '저녁 메뉴 추천 받습니다.', writerNickname: '안산 김기홍', categoryId: 1, createdAt: '2025.05.01', likeCount: 0, serialNumber: 1013 },
  { id: 3, title: '아스날 챔스 우승 축하드립니다. 감사합니다', writerNickname: '서산 김기홍', categoryId: 2, createdAt: '2025.05.01', likeCount: 884, serialNumber: 1012 },
  { id: 4, title: '이직 준비중입니다.', writerNickname: '일산 김기홍', categoryId: 3, createdAt: '2025.05.01', likeCount: 2, serialNumber: 1011 },
  { id: 5, title: '오늘 근로자의 날입니다', writerNickname: '부산 김기홍', categoryId: 4, createdAt: '2025.05.01', likeCount: 1, serialNumber: 1010 },
  { id: 6, title: '오버워치 1대1 신청 받습니다.', writerNickname: '마산 김기홍', categoryId: 1, createdAt: '2025.05.01', likeCount: 0, serialNumber: 1009 },
  { id: 7, title: '루미큐브 한판 하실분', writerNickname: '군산 김기홍', categoryId: 2, createdAt: '2025.05.01', likeCount: 0, serialNumber: 1008 },
  { id: 8, title: '요즘 할만한 게임 추천해주세요', writerNickname: '서울 김기홍', categoryId: 1, createdAt: '2025.05.01', likeCount: 3, serialNumber: 1007 },
  { id: 9, title: '회사에서 개발 공부할 수 있는 방법?', writerNickname: '대전 김기홍', categoryId: 5, createdAt: '2025.05.01', likeCount: 5, serialNumber: 1006 },
  { id: 10, title: '카카오 코딩테스트 후기 공유합니다', writerNickname: '인천 김기홍', categoryId: 1, createdAt: '2025.05.01', likeCount: 7, serialNumber: 1005 }
])


</script>

<template>
  <div class="w-full max-w-4xl mx-auto p-6">
    <!-- 탭 -->
    <div class="flex gap-6 mb-6 border-b border-[var(--newbitdivider)] text-13px-regular">
      <span
          :class="[
          'pb-2 cursor-pointer',
          selectedTab === 'column'
            ? 'border-b-2 border-[var(--newbitnormal)] text-[var(--newbitnormal)] font-bold'
            : 'text-[var(--newbitgray)]'
        ]"
          @click="selectedTab = 'column'"
      >칼럼</span>
      <span
          :class="[
          'pb-2 cursor-pointer',
          selectedTab === 'post'
            ? 'border-b-2 border-[var(--newbitnormal)] text-[var(--newbitnormal)] font-bold'
            : 'text-[var(--newbitgray)]'
        ]"
          @click="selectedTab = 'post'"
      >게시글</span>
    </div>

    <!-- 콘텐츠 -->
    <h2 class="text-heading3 mb-4">
      {{ selectedTab === 'column' ? '좋아요한 칼럼' : '좋아요한 게시글' }}
    </h2>

    <div v-if="selectedTab === 'column'" class="space-y-6">
      <ColumnCard
          v-for="column in columns"
          :key="column.id"
          :column="column"
      />
    </div>

    <div v-else class="space-y-4">
      <PostTable :posts="posts" />
    </div>


    <PagingBar />
  </div>
</template>

<style scoped>

</style>