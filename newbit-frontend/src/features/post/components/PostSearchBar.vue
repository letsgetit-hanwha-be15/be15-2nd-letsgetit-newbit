<script setup>
import { useRouter } from 'vue-router'
import { ref } from 'vue'

const emit = defineEmits(['search'])
const searchKeyword = ref('')

const handleSearch = () => {
  emit('search', searchKeyword.value)
}

</script>

<template>
  <div class="flex items-center justify-between mb-4">
    <div class="flex-1 flex gap-2">
      <input
          v-model="searchKeyword"
          type="text"
          placeholder="작성자나 제목으로 검색해보세요"
          class="flex-1 border border-gray-300 rounded px-4 py-2 text-sm"
      />
      <button
          @click="handleSearch"
          class="bg-blue-500 text-white px-4 py-2 rounded text-sm"
      >
        검색
      </button>
    </div>

  </div>
</template>
