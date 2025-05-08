<template>
  <section class="w-full max-w-[900px] mx-auto px-6 py-8">
    <h2 class="text-heading2 mb-8">칼럼 요청 내역</h2>

    <div v-if="columnRequests.length > 0" class="flex flex-col gap-6">
      <div
          v-for="item in columnRequests"
          :key="item.id"
          class="flex justify-between items-start p-5 border border-[var(--newbitdivider)] rounded-lg shadow-sm bg-white"
      >
        <!-- 왼쪽 텍스트 -->
        <div class="flex flex-col justify-between flex-1 pr-4">
          <h3 class="text-heading3 mb-4">{{ item.title }}</h3>

          <div class="flex items-center gap-4 text-13px-regular text-[var(--newbitgray)] mb-2">
            <div class="flex items-center gap-1">
              <img :src="diamondIcon" alt="다이아" class="w-4 h-4" />
              <span>{{ item.diamondCount }}</span>
            </div>
            <span>|</span>
            <span>요청 일시 {{ item.date }}</span>
            <span>|</span>
            <span>{{ item.type }}</span>
            <span>|</span>
            <span>상태: <span :class="statusColor(item.status)">{{ item.status }}</span></span>
          </div>

          <p v-if="item.status === '반려'" class="text-13px-regular text-[var(--newbitgray)]">
            반려 사유 : {{ item.rejectedReason }}
          </p>
        </div>

        <!-- 썸네일 -->
        <div class="w-[180px] h-[120px]">
          <img
              :src="item.thumbnailUrl || fallbackImg"
              @error="(e) => (e.target.src = fallbackImg)"
              alt="썸네일"
              class="w-full h-full object-cover rounded-lg"
          />
        </div>
      </div>
    </div>

    <div v-else class="text-center text-[var(--newbitgray)] py-20">
      칼럼 요청 내역이 없습니다.
    </div>

    <!-- 페이지네이션 -->
    <PagingBar
        class="mt-10"
        :currentPage="currentPage"
        :totalPages="totalPage"
        @page-change="handlePageChange"
    />
  </section>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { useToast } from 'vue-toastification'
import { getMyColumnRequests } from '@/api/column'
import PagingBar from '@/components/common/PagingBar.vue'

const toast = useToast()
const currentPage = ref(1)
const totalPage = ref(1)
const pageSize = 5
const columnRequests = ref([])

const diamondIcon = new URL('@/assets/image/diamond-icon.png', import.meta.url).href
const fallbackImg = new URL('@/assets/image/product-skeleton.png', import.meta.url).href

const statusColor = (status) => {
  if (status === '승인') return 'text-blue-500'
  if (status === '반려') return 'text-[var(--newbitred)]'
  return 'text-[var(--newbitgray)]'
}

const requestTypeToKorean = (type) => {
  if (type === 'CREATE') return '등록'
  if (type === 'UPDATE') return '수정'
  if (type === 'DELETE') return '삭제'
  return ''
}

const fetchColumns = async () => {
  try {
    const res = await getMyColumnRequests({
      page: currentPage.value - 1,
      size: pageSize
    })

    const { content, totalPages } = res.data.data
    totalPage.value = totalPages

    columnRequests.value = content.map((item) => ({
      id: item.columnRequestId,
      title: item.title,
      diamondCount: item.price || 0,
      date: item.createdAt?.substring(0, 10).replace(/-/g, '.'),
      type: requestTypeToKorean(item.requestType),
      status: (() => {
        if (item.isApproved === null) return '진행중'
        if (item.isApproved === true) return '승인'
        if (item.isApproved === false && !item.rejectedReason) return '진행중'
        return '반려'
      })(),
      rejectedReason: item.rejectedReason || '',
      thumbnailUrl: item.thumbnailUrl,
    }))
  } catch (e) {
    toast.error('칼럼 요청 목록을 불러오지 못했어요.')
  }
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchColumns()
}

onMounted(() => {
  fetchColumns()
})
</script>