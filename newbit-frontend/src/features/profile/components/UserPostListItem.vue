<script setup>
const { post, rowIndex, currentPage, pageSize } = defineProps({
  post: Object,
  rowIndex: Number,
  currentPage: Number,
  pageSize: Number
})
</script>

<template>
  <!-- 실제 게시글이 있는 경우 -->
  <tr v-if="post" class="text-center hover:bg-gray-50 transition border-b">
    <td class="py-4 text-gray-800">
      <span>{{ (currentPage - 1) * pageSize + rowIndex + 1 }}</span>
    </td>
    <td class="text-left px-4 py-4 hover:underline cursor-pointer">
      <router-link :to="`/posts/${post.id}`">
        {{ post.title }}
      </router-link>
    </td>
    <td class="py-4 text-gray-700">{{ post.writerName }}</td>
    <td class="py-4 text-gray-500">{{ post.createdAt?.split('T')[0] }}</td>
    <td class="py-4 text-center">{{ post.likeCount }}</td>
  </tr>

  <!-- 빈 줄 채우기 -->
  <tr v-else class="border-b">
    <td colspan="5" class="py-4">&nbsp;</td>
  </tr>
</template>

<style scoped>
</style>
