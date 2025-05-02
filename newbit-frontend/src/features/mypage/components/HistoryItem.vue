<template>
  <div class="flex justify-between items-center py-6 text-sm w-full">
    <!-- 날짜 영역 -->
    <div class="flex flex-col items-end w-[80px] pr-2 shrink-0">
      <span v-if="showYear" class="text-xs text-gray-400">{{ year }}</span>
      <span class="text-black font-medium">{{ day }}</span>
    </div>

    <!-- 내용 영역 -->
    <div class="flex-1 px-4 overflow-hidden">
      <div class="text-black font-semibold truncate">{{ item.serviceType }}</div>
      <div v-if="item.relatedInfo" class="text-gray-400 text-sm truncate">
        {{ item.relatedInfo }}
      </div>
    </div>

    <!-- 금액 영역 -->
    <div
        class="min-w-[64px] text-right font-semibold shrink-0"
        :class="item.amount > 0 ? 'text-blue-500' : 'text-gray-400'"
    >
      {{ formattedAmount }}
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import dayjs from 'dayjs'

const props = defineProps({
  item: Object,
  prevItem: Object // 전달받은 이전 항목
})

const date = dayjs(props.item.createdAt)
const year = date.format('YYYY')
const day = date.format('MM.DD')

const showYear = computed(() => {
  if (!props.prevItem) return true
  const currentYear = year
  const prevYear = dayjs(props.prevItem.createdAt).format('YYYY')
  return currentYear !== prevYear
})

const formattedAmount = computed(() => {
  const sign = props.item.amount > 0 ? '+' : '-'
  return `${sign}${props.item.amount}`
})
</script>

<style scoped></style>