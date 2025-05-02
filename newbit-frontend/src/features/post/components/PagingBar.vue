<template>
  <div class="flex justify-center items-center gap-2 mt-6 text-sm text-gray-600">
    <button :disabled="currentPage === 1" @click="changePage(currentPage - 1)" class="px-2">← Previous</button>
    <span
        v-for="page in visiblePages"
        :key="page"
        :class="[
        'px-2 py-1 rounded cursor-pointer',
        page === currentPage ? 'bg-blue-500 text-white' : 'hover:underline'
      ]"
        @click="changePage(page)"
    >
      {{ page }}
    </span>
    <button :disabled="currentPage === totalPages" @click="changePage(currentPage + 1)" class="px-2">Next →</button>
  </div>
</template>

<script setup>
import { computed } from 'vue'
const props = defineProps({
  currentPage: Number,
  totalPages: Number,
  totalItems: Number
})
const emit = defineEmits(['page-changed'])

const changePage = (p) => {
  if (p >= 1 && p <= props.totalPages) emit('page-changed', p)
}

const visiblePages = computed(() => {
  const pages = []
  const range = 2
  const start = Math.max(1, props.currentPage - range)
  const end = Math.min(props.totalPages, props.currentPage + range)
  for (let i = start; i <= end; i++) pages.push(i)
  return pages
})
</script>
