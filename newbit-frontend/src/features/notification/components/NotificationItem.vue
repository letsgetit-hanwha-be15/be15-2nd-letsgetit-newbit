<template>
  <div class="p-4 border-b last:border-b-0 hover:bg-gray-50 cursor-pointer">
    <div class="flex justify-between items-start">
      <!-- 좌측 상단: typeName -->
      <div class="text-xs text-gray-500 font-semibold">{{ notification.typeName }}</div>
      <!-- 우측 상단: 시간 -->
      <div class="text-xs text-gray-400">{{ formattedDate }}</div>
    </div>
    <!-- 내용 -->
    <div class="text-sm text-13px-regular mt-2 text-left p-3" v-html="formattedContent"></div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  notification: {
    type: Object,
    required: true,
  },
})

const formattedDate = computed(() => {
  const date = new Date(props.notification.createdAt)
  return `${(date.getMonth() + 1).toString().padStart(2, '0')}.${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
})

const formattedContent = computed(() => props.notification.content)
</script>