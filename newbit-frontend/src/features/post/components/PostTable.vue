<script setup>
defineProps({
  posts: {
    type: Array,
    required: true
  }
})
</script>

<template>
  <table class="w-full text-sm border-collapse mt-2">
    <thead class="bg-gray-50 border-b text-gray-500 text-center">
    <tr>
      <th class="py-3">번호</th>
      <th class="text-left px-4">제목</th>
      <th>글쓴이</th>
      <th>등록일</th>
      <th>좋아요</th>
    </tr>
    </thead>
    <tbody>
    <tr
        v-for="(post, index) in posts"
        :key="post.id"
        :class="[
          'text-center border-b hover:bg-gray-50 transition',
          post.isNotice ? 'bg-[#f7faff]' : ''
        ]"
    >
      <td class="py-4">
        <span v-if="post.isNotice" class="text-xs px-2 py-1 bg-blue-100 text-blue-600 rounded">공지사항</span>
        <span v-else class="text-blue-500 text-xs">{{ post.serialNumber }}</span>
      </td>
      <td class="text-left px-4 py-4 font-medium">
        <router-link :to="`/posts/${post.id}`" class="hover:underline">
          {{ post.title }}
        </router-link>
      </td>
      <td class="py-4">{{ post.writerNickname }}</td>
      <td class="py-4">{{ post.createdAt }}</td>
      <td class="py-4">{{ post.likeCount }}</td>
    </tr>
    </tbody>
  </table>
</template>


