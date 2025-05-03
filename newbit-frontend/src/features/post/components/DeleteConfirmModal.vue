<template>
  <div class="fixed inset-0 bg-black/30 flex items-center justify-center z-50" @click.self="emit('close')">
    <div class="bg-white rounded-xl p-6 w-[320px] text-center shadow border">
      <h2 class="text-lg font-bold mb-4">{{ title }}</h2>
      <p class="text-sm mb-6">{{ message }}</p>
      <div class="flex justify-center gap-4">
        <button @click="emit('close')" class="bg-red-400 hover:bg-red-500 text-white px-4 py-2 rounded">아니요</button>
        <button @click="emit('confirm')" class="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded">네</button>
      </div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  title: {
    type: String,
    default: '삭제 확인'
  },
  message: {
    type: String,
    default: '정말 삭제하시겠습니까?'
  }
})

const emit = defineEmits(['close', 'confirm'])
</script>
