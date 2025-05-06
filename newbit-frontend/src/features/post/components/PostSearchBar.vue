<script setup>
import { useRouter } from 'vue-router'
import { ref } from 'vue'

const emit = defineEmits(['search'])
const searchKeyword = ref('')
const router = useRouter()

const handleSearch = () => {
  emit('search', searchKeyword.value)
}

const goToCreate = () => {
  router.push('/posts/create')
}
</script>

<template>
  <div class="flex items-center gap-2 mb-4">
    <input
        v-model="searchKeyword"
        type="text"
        placeholder="작성자나 제목으로 검색해보세요"
        class="flex-1 border border-[var(--newbitdivider)] rounded px-4 py-2 text-13px-regular"
    />
    <button
        @click="handleSearch"
        class="bg-[var(--newbitnormal)] text-white px-4 py-2 rounded text-13px-bold"
    >
      검색
    </button>
    <button
        @click="goToCreate"
        class="bg-[var(--newbitnormal)] text-white px-4 py-2 rounded text-13px-bold ml-2 whitespace-nowrap"
    >
      글 작성
    </button>
  </div>
</template>
