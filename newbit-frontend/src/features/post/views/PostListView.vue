<script setup>
import { ref, onMounted } from 'vue'
import PostListItem from '../components/PostListItem.vue'
import { fetchPosts } from '../api'

const posts = ref([])

onMounted(async () => {
  const res = await fetchPosts(0, 10)

  // 공지글과 일반글을 구분하고, 번호도 계산
  posts.value = res.content.map((post, index) => ({
    ...post,
    isNotice: post.isNotice || false,
    serialNumber: res.totalElements - index // 예: 100 - index
  }))
})
</script>

<template>
  <table class="post-table">
    <thead>
    <tr>
      <th>번호</th>
      <th>제목</th>
      <th>글쓴이</th>
      <th>등록일</th>
      <th>좋아요</th>
    </tr>
    </thead>
    <tbody>
    <PostListItem
        v-for="(post, index) in posts"
        :key="post.id"
        :post="post"
        :row-index="index"
    />
    </tbody>
  </table>
</template>

<style scoped>
.post-table {
  width: 100%;
  border-collapse: collapse;
}
.post-table th,
.post-table td {
  padding: 12px;
  border-bottom: 1px solid #ddd;
  text-align: center;
}
</style>
