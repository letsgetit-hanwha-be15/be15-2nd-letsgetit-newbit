<script setup>
import { useRouter } from 'vue-router'
import { ref, onMounted } from 'vue'
import { fetchPostCategories } from '@/api/post'

const router = useRouter()
const selectedId = ref(null)
const categories = ref([{ id: null, name: '전체' }]) // 기본 '전체' 포함

const handleClick = (id) => {
  selectedId.value = id
  if (id === null) {
    router.push('/posts')
  } else {
    router.push(`/posts/category/${Number(id)}`)
  }
}

const loadCategories = async () => {
  try {
    const res = await fetchPostCategories()
    categories.value.push(...res.data.filter(c => c.name !== '전체'))
  } catch (e) {
    console.error('카테고리 불러오기 실패:', e)
  }
}

onMounted(() => {
  loadCategories()
})
</script>

<template>
  <aside
      class="w-[200px] h-[680px] p-4 border border-blue-200 rounded-lg text-sm flex-shrink-0 mt-6 bg-white shadow">

    <!-- 제목 -->
    <h2 class="text-heading3 text-gray-600 mb-4">카테고리</h2>

    <ul class="space-y-2">
      <li
          v-for="cat in categories"
          :key="cat.id ?? 'all'"
          @click="handleClick(cat.id)"
          :class="[
          'cursor-pointer px-3 py-2 rounded-md transition text-13px-regular',
          cat.id === selectedId
            ? 'bg-blue-100 text-blue-500 font-semibold'
            : 'hover:bg-gray-100 text-gray-800'
        ]"
      >
        {{ cat.name }}
      </li>
    </ul>
  </aside>
</template>
