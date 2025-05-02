<template>
  <div class="w-full max-w-4xl mx-auto">

    <div class="flex gap-2 mb-4">
      <button
          v-for="label in ['전체', '적립', '사용']"
          :key="label"
          @click="selectedFilter = label"
          :class="[
          'px-4 py-2 rounded-full text-13px-regular',
          selectedFilter === label
            ? 'bg-blue-500 text-white'
            : 'bg-gray-100 text-gray-700'
        ]"
      >
        {{ label }}
      </button>
    </div>


    <!-- 리스트 -->
    <div class="flex flex-col divide-y divide-gray-100">
      <HistoryItem
          v-for="(item, index) in filteredHistories"
          :key="item.historyId"
          :item="normalizeItem(item)"
          :prevItem="index > 0 ? normalizeItem(filteredHistories[index - 1]) : null"
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
  return {
    id: item.historyId,
    createdAt: item.createdAt,
    amount: item.increaseAmount ?? -item.decreaseAmount,
    serviceType: item.serviceType,
    relatedInfo: item.serviceTitleOrUserNickname
  }
}
</script>

<style scoped></style>
