<script setup>
import { computed, ref, watch } from 'vue'

const props = defineProps({
  currentPage: Number,
  totalPage: Number
})

const emit = defineEmits(['page-change'])

const groupSize = 5
const currentGroup = ref(Math.floor((props.currentPage - 1) / groupSize))

// 페이지 그룹 감시
watch(() => props.currentPage, (newPage) => {
  currentGroup.value = Math.floor((newPage - 1) / groupSize)
})

// 표시할 페이지들 계산
const pages = computed(() => {
  const start = currentGroup.value * groupSize + 1
  const end = Math.min(start + groupSize - 1, props.totalPage)
  const pageList = []
  for (let i = start; i <= end; i++) {
    pageList.push(i)
  }
  return pageList
})

const goToPage = (page) => {
  if (page === props.currentPage) return
  emit('page-change', page)
}

const goToPrevGroup = () => {
  const prevStart = Math.max((currentGroup.value - 1) * groupSize + 1, 1)
  emit('page-change', prevStart)
}

const goToNextGroup = () => {
  const nextStart = (currentGroup.value + 1) * groupSize + 1
  if (nextStart <= props.totalPage) emit('page-change', nextStart)
}
</script>

<template>
  <div class="flex items-center justify-center gap-2 mt-10">
    <button
        class="flex items-center gap-1 px-2 py-1 text-gray-400 text-13px-regular disabled:cursor-not-allowed"
        :disabled="pages[0] === 1"
        @click="goToPrevGroup"
    >
      ← Previous
    </button>

    <button
        v-for="page in pages"
        :key="page"
        @click="goToPage(page)"
        :class="[
          'px-2 py-2 rounded',
          page === currentPage ? 'bg-[var(--newbitnormal)] text-white' : 'text-black hover:bg-gray-100',
          'cursor-pointer',
          'text-13px-regular'
        ]"
    >
      {{ page }}
    </button>

    <button
        class="flex items-center gap-1 px-2 py-1 text-black  text-13px-regular disabled:text-gray-400 disabled:cursor-not-allowed"
        :disabled="pages.at(-1) === totalPage"
        @click="goToNextGroup"
    >
      Next →
    </button>
  </div>
</template>
