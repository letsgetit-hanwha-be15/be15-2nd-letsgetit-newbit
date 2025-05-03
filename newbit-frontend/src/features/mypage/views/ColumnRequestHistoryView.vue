<template>
  <div class="flex">
    <!-- 메인 콘텐츠 -->
    <div class="flex-1 px-10 py-8">
      <h2 class="text-heading2 mb-8">칼럼 요청 내역</h2>

      <div class="flex flex-col gap-8">
        <div
            v-for="item in columnRequests"
            :key="item.id"
            class="flex justify-between border-b pb-3"
        >
          <!-- 왼쪽: 텍스트 -->
          <div class="flex flex-col gap-2 flex-1">
            <!-- 제목 -->
            <h3 class="text-16px-bold text-[var(--newbittext)] mb-8">
              {{ item.title }}
            </h3>

            <!-- 메타 정보 -->
            <div class="flex items-center gap-4 text-13px-regular text-[var(--newbitgray)]">
              <div class="flex items-center gap-1">
                <img :src="diamondIcon" alt="다이아" class="w-4 h-4" />
                <span>{{ item.diamondCount }} </span>
              </div>
              <span>|</span>
              <span>요청 일시 {{ item.date }}</span>
              <span>|</span>
              <span>{{ item.type }}</span>
              <span>|</span>
              <span>상태: <span :class="statusColor(item.status)">{{ item.status }}</span></span>
            </div>

            <!-- 반려 사유 -->
            <p v-if="item.status === '반려'" class="text-13px-regular text-[var(--newbitgray)]">
              반려 사유 : {{ item.rejectionReason }}
            </p>
          </div>

          <!-- 썸네일 -->
          <img
              :src="item.thumbnailUrl || fallbackImg"
              @error="(e) => (e.target.src = fallbackImg)"
              alt="썸네일"
              class="w-[160px] h-[100px] object-cover rounded-md"
          />
        </div>
      </div>

      <!-- 페이지네이션 (임시) -->
      <div class="flex justify-center mt-10 gap-2 text-13px-regular text-[var(--newbitgray)]">
        <span class="cursor-pointer">← Previous</span>
        <span class="font-bold text-[var(--newbitnormal)]">1</span>
        <span class="cursor-pointer">2</span>
        <span class="cursor-pointer">3</span>
        <span class="cursor-pointer">Next →</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const diamondIcon = new URL('@/assets/image/diamond-icon.png', import.meta.url).href
const fallbackImg = new URL('@/assets/image/product-skeleton.png', import.meta.url).href

// 임시 데이터
const columnRequests = ref([
  {
    id: 1,
    title: '[인터뷰 1] 스펙의 전례 없는 위기 대응 전략: "통제할 수 있는 것에 집중하자"',
    diamondCount: 10,
    date: '2025.07.02',
    type: '등록',
    status: '반려',
    rejectionReason: '내용에 비해 가격이 상대적으로 높습니다. 가격 재고 부탁합니다.',
    thumbnailUrl: 'https://via.placeholder.com/160x100?text=썸네일1',
  },
  {
    id: 2,
    title: '[5분 순삭] 팀장없이도 굴러가는 시스템 만들기',
    diamondCount: 5,
    date: '2025.05.10',
    type: '수정',
    status: '승인',
    rejectionReason: '',
    thumbnailUrl: 'https://via.placeholder.com/160x100?text=썸네일2',
  },
  {
    id: 3,
    title: '직장생활을 바꿀 수 있는 한 단어, 일의 맥락을 발견하는 다섯가지 방법',
    diamondCount: 5,
    date: '2025.04.12',
    type: '수정',
    status: '진행중',
    rejectionReason: '',
    thumbnailUrl: 'https://via.placeholder.com/160x100?text=썸네일3',
  }
])

const statusColor = (status) => {
  if (status === '승인') return 'text-blue-500'
  if (status === '반려') return 'text-[var(--newbitred)]'
  return 'text-[var(--newbitgray)]'
}
</script>

<style scoped>
</style>