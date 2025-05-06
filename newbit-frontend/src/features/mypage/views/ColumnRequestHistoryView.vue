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
              반려 사유 : {{ item.rejectedReason }}
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
import { ref, onMounted } from 'vue'
import { getMyColumnRequests } from '@/api/column'
import { useToast } from 'vue-toastification'

const toast = useToast()

const columnRequests = ref([])

const diamondIcon = new URL('@/assets/image/diamond-icon.png', import.meta.url).href
const fallbackImg = new URL('@/assets/image/product-skeleton.png', import.meta.url).href

const statusColor = (status) => {
  if (status === '승인') return 'text-blue-500'
  if (status === '반려') return 'text-[var(--newbitred)]'
  return 'text-[var(--newbitgray)]'
}

// 요청 타입 한글 변환
const requestTypeToKorean = (type) => {
  if (type === 'CREATE') return '등록'
  if (type === 'UPDATE') return '수정'
  if (type === 'DELETE') return '삭제'
  return ''
}

onMounted(async () => {
  try {
    const res = await getMyColumnRequests()
    columnRequests.value = res.data.data.map((item) => ({
      id: item.columnRequestId,
      title: item.title,
      diamondCount: item.price || 0,
      date: item.createdAt?.substring(0, 10).replace(/-/g, '.'),
      type: requestTypeToKorean(item.requestType),
      status: item.isApproved === null ? '진행중' : item.isApproved ? '승인' : '반려',
      rejectedReason: item.rejectedReason || '',
      thumbnailUrl: item.thumbnailUrl,
    }))
  } catch (e) {
    toast.error('칼럼 요청 목록을 불러오지 못했어요.')
  }
})
</script>
<style scoped>
</style>