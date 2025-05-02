<template>
  <div class="w-full max-w-4xl mx-auto">

    <div
        v-if="type === 'diamond' || type === 'point'"
        class="flex gap-2 mb-4"
    >
      <button
          v-for="label in ['전체', '적립', '사용']"
          :key="label"
          @click="selectedFilter = label"
          :class="[
          'px-4 py-2 rounded-full text-13px-regular',
          selectedFilter === label
            ? 'bg-[var(--newbitnormal)] text-white'
            : 'bg-gray-100 text-gray-700'
        ]"
      >
        {{ label }}
      </button>
    </div>

    <div v-if="type === 'sale'" class="flex flex-row justify-between py-2 text-[var(--newbitgray)] text-sm font-medium border-b">
      <div class="ps-24">판매 콘텐츠</div>
      <div class="flex flex-row gap-2">
        <div class="text-center">정산 일시</div>
        <div class="text-right min-w-[64px]">판매액(원)</div>
      </div>
    </div>


    <!-- 리스트 -->
    <div class="flex flex-col divide-y divide-[var(--newbitlightgray)]">
      <HistoryItem
          v-for="(item, index) in filteredHistories"
          :key="item.historyId"
          :item="normalizeItem(item)"
          :prevItem="index > 0 ? normalizeItem(filteredHistories[index - 1]) : null"
          :type="type"
      />
    </div>

    <!-- 임시 페이지네이션 -->
    <div
        v-if="pagination"
        class="flex justify-center items-center gap-4 mt-6 text-sm text-gray-700"
    >
      <button
          :disabled="pagination.currentPage === 1"
          @click="$emit('page-change', pagination.currentPage - 1)"
          class="px-3 py-1 border rounded disabled:text-gray-300 disabled:cursor-not-allowed"
      >
        Previous
      </button>
      <span>Page {{ pagination.currentPage }} / {{ pagination.totalPage }}</span>
      <button
          :disabled="pagination.currentPage === pagination.totalPage"
          @click="$emit('page-change', pagination.currentPage + 1)"
          class="px-3 py-1 border rounded disabled:text-gray-300 disabled:cursor-not-allowed"
      >
        Next
      </button>
    </div>
  </div>
</template>

<script setup>
import HistoryItem from './HistoryItem.vue'
import {ref, computed} from "vue";

const props = defineProps({
  histories: Array,
  type: String, // 'point' or 'diamond'
  pagination: Object, // currentPage, totalPage, totalItems
})

const emit = defineEmits(['page-change'])


const selectedFilter = ref('전체') // '전체' | '적립' | '사용'

// 금액 기준 필터링
const filteredHistories = computed(() => {
  if (selectedFilter.value === '적립') {
    return props.histories.filter(h => h.increaseAmount)
  }
  if (selectedFilter.value === '사용') {
    return props.histories.filter(h => h.decreaseAmount)
  }
  return props.histories
})


function normalizeItem(item) {
  let readableType = item.serviceType
  let readableInfo = item.serviceTitleOrUserNickname
  let amount = item.saleAmount
  if (item.serviceType === 'COFFEECHAT') {
    if(props.type === 'sale')
      readableType = '커피챗 판매'
    else if(props.type === 'diamond')
      readableType = '커피챗 구매'
    readableInfo = readableInfo.concat('님과의 커피챗')
  } else if (item.serviceType === 'COLUMN') {
    if(props.type === 'sale')
      readableType = '칼럼 판매'
    else if(props.type === 'diamond')
      readableType = '칼럼 구매'
  }

  if(props.type === 'point' || props.type === 'diamond') {
    amount = item.increaseAmount ?? -item.decreaseAmount
  }

  return {
    id: item.serviceId,
    createdAt: item.createdAt,
    amount: amount,
    serviceType: readableType,
    relatedInfo: readableInfo,
    settledAt: item.settledAt,
  }
}
</script>

<style scoped></style>
