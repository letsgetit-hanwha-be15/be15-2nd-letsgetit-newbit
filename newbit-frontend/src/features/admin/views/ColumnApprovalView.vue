<template>
  <div class="column-approval-page">
    <h2 class="text-heading2 mb-6">칼럼 요청 관리</h2>

    <!-- 탭 -->
    <div class="tab-wrapper mb-6 flex gap-6 border-b border-[var(--newbitdivider)]">
      <button
          v-for="tab in tabs"
          :key="tab"
          @click="changeTab(tab)"
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

    <!-- 요청 카드 목록 -->
    <div v-if="columnRequests.length" class="flex flex-col gap-6">
      <div
          v-for="item in columnRequests"
          :key="item.columnRequestId"
          class="flex justify-between items-start p-5 border border-[var(--newbitdivider)] rounded-lg shadow-sm bg-[var(--newbitlightmode)]"
      >
        <!-- 텍스트 -->
        <div class="flex flex-col justify-between flex-1 pr-4">
          <h2 class="text-heading3 mb-8">
            {{ item.title }}
          </h2>
          <div class="flex items-center gap-1 text-13px-regular text-[var(--newbitdark)] mb-2">
            <img :src="diamondIcon" alt="다이아" class="w-4 h-4" />
            <span>{{ item.price ?? 0 }}</span>
          </div>
          <div class="text-13px-regular text-[var(--newbitgray)] mb-2">
            {{ item.mentorNickname || '익명 멘토' }} | {{ formatDate(item.createdAt) }}
          </div>
          <button
              class="bg-[var(--newbitnormal)] text-white px-3 py-1.5 rounded text-13px-regular w-fit"
              @click="goToDetail(item.columnRequestId)"
          >
            상세보기
          </button>
        </div>

        <!-- 썸네일 -->
        <div class="w-[240px] h-[140px]">
          <img
              :src="item.thumbnailUrl || fallbackImg"
              @error="(e) => (e.target.src = fallbackImg)"
              class="w-full h-full object-cover rounded-md"
              alt="썸네일"
          />
        </div>
      </div>
    </div>

    <!-- 비어있을 때 -->
    <div v-else class="text-center text-[var(--newbitgray)] py-12">
      요청이 없습니다.
    </div>

    <!-- 페이징 -->
    <PagingBar
        :currentPage="currentPage + 1"
        :total-pages="totalPages"
        @page-change="handlePageChange"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import PagingBar from '@/components/common/PagingBar.vue'
import { getAllColumnRequests } from "@/api/column.js";

const router = useRouter()

const tabs = ['등록', '수정', '삭제']
const selectedTab = ref('등록')
const columnRequests = ref([])
const currentPage = ref(0)
const totalPages = ref(1)

const fallbackImg = new URL('@/assets/image/product-skeleton.png', import.meta.url).href
const diamondIcon = new URL('@/assets/image/diamond-icon.png', import.meta.url).href

const requestTypeMap = {
  등록: 'CREATE',
  수정: 'UPDATE',
  삭제: 'DELETE'
}

const fetchRequests = async () => {
  try {
    const res = await getAllColumnRequests({
      page: currentPage.value,
      size: 6
    })

    const result = res?.data?.data

    columnRequests.value = result.content.filter(
        (item) => item.requestType === requestTypeMap[selectedTab.value]
    )

    totalPages.value = result.totalPages
  } catch (err) {
    console.error('칼럼 요청 목록 조회 실패', err)
  }
}

const changeTab = (tab) => {
  selectedTab.value = tab
  currentPage.value = 0
}

const handlePageChange = (page) => {
  currentPage.value = page - 1 // PagingBar는 1부터 시작하므로 -1
}

const goToDetail = (id) => {
  router.push({
    name: 'AdminColumnDetail',
    params: { columnId: id },
    query: { type: requestTypeMap[selectedTab.value] }
  })
}

const formatDate = (isoDate) =>
    new Date(isoDate).toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit'
    })

watch([currentPage, selectedTab], fetchRequests)
onMounted(fetchRequests)
</script>

<style scoped></style>