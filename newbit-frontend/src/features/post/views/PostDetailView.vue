<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const postId = route.params.postId

const post = ref(null)

// 좋아요 토글
const toggleLike = () => {
  if (!post.value) return
  post.value.liked = !post.value.liked
  post.value.likeCount += post.value.liked ? 1 : -1
}

const fetchPostDetail = async () => {
  post.value = {
    id: postId,
    title: '생산성을 높여주는 개발 툴 추천 리스트',
    author: '일산 김기홍',
    createdAt: '2025.05.01. 22:09',
    content: `안녕하세요. 개발하면서 자주 사용하는 유용한 툴들을 공유해봅니다!
여러분도 본인이 자주 쓰는 툴들 공유해 주세요 🙌

✅ 추천 툴 목록
1. **Postman**
- API 테스트할 때 없어서는 안 될 존재!
- RESTful API 개발하면서 응답 구조 확인할 때 최고입니다.

2. **Notion**
- 프로젝트 정리, 회의록, 일정 관리까지 한번에.
- 개발자뿐만 아니라 팀 협업용으로도 최고!`,
    likeCount: 10,
    liked: false, // 좋아요 여부
    attachment: {
      name: '개발 도구 리스트.xlsx',
      size: '451KB'
    }
  }
}

onMounted(fetchPostDetail)
</script>

<template>
  <section class="p-8 max-w-3xl mx-auto">
    <div v-if="post">

      <!-- 상단 오른쪽 수정/삭제 버튼 -->
      <div class="flex justify-end gap-2 mb-2">
        <button class="bg-blue-400 text-white px-3 py-1 rounded text-sm">수정</button>
        <button class="bg-red-500 text-white px-3 py-1 rounded text-sm">삭제</button>
      </div>

      <!-- 제목 -->
      <div class="flex justify-between items-start mb-1">
        <h1 class="text-2xl font-bold">{{ post.title }}</h1>
      </div>

      <!-- 작성자 + 좋아요/신고 영역 -->
      <div class="flex justify-between items-center mb-4">
        <!-- 작성자 정보 -->
        <div class="flex items-center gap-2 text-gray-600">
          <img src="@/assets/image/default-profile.png" alt="기본 프로필" />
          <div>
            <p class="text-sm font-bold"> {{ post.author }}</p>
            <p class="text-xs">{{ post.createdAt }}</p>
          </div>
        </div>

        <!-- 좋아요 / 신고 버튼 -->
        <div class="flex items-center gap-2">
          <button
              @click="toggleLike"
              class="flex items-center border px-3 py-1 rounded gap-2"
          >
            <img
                :src="post.liked
                ? '/src/assets/image/heart-active.png'
                : '/src/assets/image/heart-default.png'"
                alt="좋아요"
                class="w-4 h-4"
            />
            <span>{{ post.likeCount }}</span>
          </button>
          <button class="bg-red-500 text-white px-3 py-1 rounded text-sm">신고</button>
        </div>
      </div>

      <!-- 본문 -->
      <div class="border p-4 rounded whitespace-pre-line leading-relaxed mb-4">
        {{ post.content }}
      </div>

      <!-- 첨부파일 -->
      <div class="mt-4 text-sm">
        <strong>첨부파일</strong> (1개 {{ post.attachment.size }})<br>
        <span class="text-blue-600 underline cursor-pointer">{{ post.attachment.name }}</span>
      </div>
    </div>
  </section>
</template>



<style scoped>
</style>
