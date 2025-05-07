<script setup>
import { ref, onMounted } from 'vue'
import { fetchPostList } from '@/api/post' // 실제 경로에 맞게 조정

const templates = ref([])

onMounted(async () => {
  try {
    const res = await fetchPostList(0, 5) // 첫 페이지 5개만
    templates.value = res.data.content // content 배열로 가정
  } catch (err) {
    console.error('문서 템플릿 목록 조회 실패:', err)
  }
})
</script>

<template>
  <div class="w-full h-full p-5 rounded-lg border border-gray-200 bg-white">
    <h2 class="text-heading3 text-newbittext font-bold mb-4">
      뉴빗 인기 게시글
    </h2>

    <div class="space-y-8">
      <div
          v-for="(template, index) in templates"
          :key="template.postId"
          class="flex items-center justify-between px-4 py-2 border border-blue-200 rounded-md"
      >
        <span class="text-base font-semibold text-blue-800 w-[30px]">{{ index + 1 }}</span>
        <span class="text-base text-newbittext w-full text-left pl-2 truncate">
          {{ template.title }}
        </span>
      </div>
    </div>
  </div>
</template>
