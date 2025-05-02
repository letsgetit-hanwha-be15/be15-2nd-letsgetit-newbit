<script setup>
const { post, rowIndex } = defineProps({
  post: Object,
  rowIndex: Number
})
</script>

<template>
  <tr :class="{ notice: post.isNotice }">
    <td>
      <span v-if="post.isNotice" class="badge">공지사항</span>
      <span v-else>{{ post.serialNumber }}</span>
    </td>
    <td class="title">
      <router-link :to="`/posts/${post.id}`">
        {{ post.title }}
      </router-link>
    </td>
    <td>{{ post.writerNickname }}</td>
    <td>{{ post.createdAt }}</td>
    <td>{{ post.likeCount }}</td>
  </tr>
</template>

<style scoped>
.notice {
  background-color: #fafbfd;
}
.badge {
  background-color: #1976d2;
  color: white;
  padding: 3px 6px;
  font-size: 0.75rem;
  border-radius: 4px;
}
.title {
  text-align: left;
}
</style>
