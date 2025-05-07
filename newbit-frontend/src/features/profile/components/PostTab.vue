<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getPostUserId } from '@/api/post'
import UserPostListItem from '@/features/profile/components/UserPostListItem.vue'
import PagingBar from '@/components/common/PagingBar.vue'

// 라우트에서 mentorId (= userId)
const route = useRoute()
const userId = Number(route.params.id)

const posts = ref([])
const currentPage = ref(1)
const totalPages = ref(0)
const pageSize = 8

// 항상 8줄 고정
const paddedPosts = computed(() => {
  const filled = [...posts.value]
  while (filled.length < pageSize) {
    filled.push(null)
  }
  return filled
})

async function fetchPosts(page = 1) {
  try {
    const res = await getPostUserId(userId, page - 1, pageSize)
    const data = res.data
    posts.value = data.content
    totalPages.value = data.totalPages
    currentPage.value = data.number + 1
  } catch (e) {
    console.error('게시글 조회 실패:', e)
  }
}

function handlePageChange(page) {
  currentPage.value = page
  fetchPosts(page)
}

onMounted(() => {
  fetchPosts()
})
</script>

<template>
  <div>
    <table class="w-full table-auto border-collapse">
      <thead>
      <tr class="border-b">
        <th class="py-2">번호</th>
        <th class="text-left pl-4 py-2">제목</th>
        <th class="py-2">작성자</th>
        <th class="py-2">작성일</th>
        <th class="py-2">좋아요</th>
      </tr>
      </thead>
      <tbody>
      <UserPostListItem
          v-for="(post, index) in paddedPosts"
          :key="index"
          :post="post"
          :rowIndex="index"
          :currentPage="currentPage"
          :pageSize="pageSize"
      />
      </tbody>
    </table>

    <PagingBar
        class="mt-8"
        :current-page="currentPage"
        :total-pages="totalPages"
        @page-change="handlePageChange"
    />
  </div>
</template>
