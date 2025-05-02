<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const categories = [
  { id: null, name: '전체' },
  { id: 1, name: '자유 게시판' },
  { id: 2, name: '문서 템플릿' },
  { id: 3, name: '개발 트렌드/뉴스' },
  { id: 4, name: '취업/이직' },
  { id: 5, name: '급여 관리/재테크' }
]

// 클릭 시 RESTful 주소로 이동
const handleClick = (id) => {
  if (id === null) {
    router.push('/posts') // 전체 게시글
  } else {
    router.push(`/posts/category/${id}`) // 해당 카테고리 게시글
  }
}
</script>

<template>
  <aside class="w-[200px] p-4 border-r text-sm">
    <h2 class="mb-2 text-gray-600 font-semibold">카테고리</h2>
    <ul class="space-y-2">
      <li
          v-for="cat in categories"
          :key="cat.id"
          :class="[
          'cursor-pointer px-3 py-2 rounded-md transition',
          cat.id === selectedId
            ? 'bg-blue-100 text-blue-600 font-semibold'
            : 'hover:bg-gray-100 text-gray-800'
        ]"
          @click="handleClick(cat.id)"
      >
        {{ cat.name }}
      </li>
    </ul>
  </aside>
</template>
