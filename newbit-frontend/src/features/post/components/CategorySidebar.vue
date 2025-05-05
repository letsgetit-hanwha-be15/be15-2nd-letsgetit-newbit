<script setup>
import { useRouter } from 'vue-router'
import { ref } from 'vue'

const router = useRouter()
const selectedId = ref(null)

const categories = [
  { id: 1, name: '자유 게시판' },
  { id: 2, name: '문서 템플릿' },
  { id: 3, name: '개발 트렌드/뉴스' },
  { id: 4, name: '취업/이직' },
  { id: 5, name: '급여관리/재테크' },
  { id: 6, name: '개발 장비' },
  { id: 7, name: '직장 생활/커뮤니케이션' },
  { id: 8, name: '업무 생산성' },
  { id: 9, name: 'AI 활용법' },
  { id: 10, name: '코드 리뷰' },
  { id: 11, name: '팀 프로젝트' },
  { id: 12, name: '기술 면접/코딩 테스트' },
  { id: 13, name: '개발자 밋업/컨퍼런스' }
]

const handleClick = (id) => {
  selectedId.value = id
  router.push(`/posts/category/${id}`)
}
</script>

<template>
  <aside
      class="w-[200px] h-[650px] p-4 border border-blue-200 rounded-lg text-sm flex-shrink-0 mt-16">
    <h2 class="text-gray-600 font-semibold text-base">카테고리</h2>
    <ul class="space-y-2">
      <li
          v-for="cat in categories"
          :key="cat.id"
          @click="handleClick(cat.id)"
          :class="[
          'cursor-pointer px-3 py-2 rounded-md transition',
          cat.id === selectedId
            ? 'bg-blue-100 text-blue-600 font-semibold'
            : 'hover:bg-gray-100 text-gray-800'
        ]"
      >
        {{ cat.name }}
      </li>
    </ul>
  </aside>
</template>
