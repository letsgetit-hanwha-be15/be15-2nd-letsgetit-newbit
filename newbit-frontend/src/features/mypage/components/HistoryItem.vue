<template>
  <div class="flex justify-between items-center py-6 text-sm w-full">
    <!-- 날짜 영역 -->
    <div class="flex flex-col items-end w-[80px] pr-2 shrink-0">
      <span v-if="showYear" class="text-xs text-[var(--newbitgray)]">{{ year }}</span>
      <span class="font-medium">{{ day }}</span>
    </div>

    <!-- 내용 영역 -->
    <div class="flex-1 px-4 overflow-hidden">
      <div class="font-semibold truncate">{{ item.serviceType }}</div>
      <div v-if="item.relatedInfo" class="text-[var(--newbitgray)] text-sm truncate">
        <router-link
            v-if="item.serviceType === '커피챗 구매' || item.serviceType === '커피챗 판매'"
            :to="`/mypage/coffeechats/${item.id}`"
            class="hover:underline"
        >
          {{ item.relatedInfo }}
        </router-link>
        <router-link
            v-else-if="item.serviceType === '칼럼 구매' || item.serviceType === '칼럼 판매'"
            :to="`/columns/${item.id}`"
            class="hover:underline"
        >
          {{ item.relatedInfo }}
        </router-link>
        <span v-else>
      {{ item.relatedInfo }}
    </span>
      </div>
    </div>

    <!-- 금액 영역 -->
    <div class="flex flex-row justify-center gap-2">
      <div v-if="type === 'sale'">
        <div v-if="item.settledAt">
          {{ dayjs(item.settledAt).format('HH:mm:ss') }}
        </div>
        <div v-else class="text-[var(--newbitgray)]">
          정산 미완료
        </div>
      </div>
      <div
          class="min-w-[64px] text-right font-semibold shrink-0"
          :class="item.amount > 0 ? 'text-[var(--newbitnormal)]' : 'text-[var(--newbitgray)]'"
      >
        {{ formattedAmount }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import dayjs from 'dayjs'

const props = defineProps({
  item: Object,
  prevItem: Object, // 전달받은 이전 항목
  type: String
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
  if(props.type === 'sale') {
    const amount = Number(props.item.amount ?? 0)
    return `${amount.toLocaleString()}`
  }
  const sign = props.item.amount > 0 ? '+' : ''
  return `${sign}${props.item.amount}`
})
</script>

<style scoped></style>