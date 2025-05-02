<template>
  <div class="w-full max-w-4xl mx-auto">

    <!-- 리스트 -->
    <div class="flex flex-col divide-y divide-gray-100">
      <HistoryItem
          v-for="(item, index) in histories"
          :key="item.historyId"
          :item="normalizeItem(item)"
          :prevItem="index > 0 ? normalizeItem(histories[index - 1]) : null"
      />
    </div>

    <!-- 페이지네이션 -->
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

const props = defineProps({
  histories: Array,
  type: String, // 'point' or 'diamond'
  pagination: Object, // currentPage, totalPage, totalItems
})

const emit = defineEmits(['page-change'])

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
