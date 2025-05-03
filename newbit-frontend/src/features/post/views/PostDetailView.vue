<script setup>
import { onMounted, ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import ReportModal from '@/features/post/components/ReportModal.vue'

const reportType = ref('') // 'post' or 'comment'
const reportedId = ref(null)

const openPostReportModal = () => {
  reportType.value = 'post'
  reportedId.value = post.value.id
  isReportModalOpen.value = true
}

const openCommentReportModal = (commentId) => {
  reportType.value = 'comment'
  reportedId.value = commentId
  isReportModalOpen.value = true
}


const route = useRoute()
const postId = route.params.postId

const post = ref(null)
const comments = ref([])
const newComment = ref('')
const currentPage = ref(1)
const pageSize = 5

const isReportModalOpen = ref(false)

const openReportModal = () => {
  isReportModalOpen.value = true
}

const closeReportModal = () => {
  isReportModalOpen.value = false
}

const handleReportSubmit = (reportData) => {
  const typeLabel = reportType.value === 'post' ? '게시글' : '댓글'
  console.log(`🚨 ${typeLabel} 신고:`, {
    type: reportType.value,
    targetId: reportedId.value,
    reason: reportData.reason,
    content: reportData.content
  })

  alert(`${typeLabel} 신고가 접수되었습니다.`)
  closeReportModal()
}



const toggleLike = () => {
  if (!post.value) return
  post.value.liked = !post.value.liked
  post.value.likeCount += post.value.liked ? 1 : -1
}

const changePage = (page) => {
  currentPage.value = page
}

const paginatedComments = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  const end = start + pageSize
  return comments.value.slice(start, end)
})

const totalPages = computed(() => {
  return Math.ceil(comments.value.length / pageSize)
})

const submitComment = () => {
  if (!newComment.value.trim()) return
  comments.value.unshift({
    id: Date.now(),
    nickname: '익명 사용자',
    date: new Date().toISOString().slice(0, 16).replace('T', ' '),
    content: newComment.value.trim()
  })
  newComment.value = ''
  currentPage.value = 1
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
1. Postman
- API 테스트할 때 없어서는 안 될 존재!
2. Notion
- 프로젝트 정리, 회의록, 일정 관리까지 한번에.`,
    likeCount: 10,
    liked: false,
    attachment: {
      name: '개발 도구 리스트.xlsx',
      size: '451KB'
    }
  }

  comments.value = Array.from({ length: 18 }, (_, i) => ({
    id: i + 1,
    nickname: `사용자 ${i + 1}`,
    date: `2025.04.23. 23:${(10 + i).toString().padStart(2, '0')}`,
    content: `댓글 내용 ${i + 1}`
  }))
}

onMounted(fetchPostDetail)
</script>

<template>
  <section class="p-8 max-w-3xl mx-auto pb-40">
    <div v-if="post">
      <div class="flex justify-end gap-2 mb-2">
        <button class="bg-blue-400 text-white px-3 py-1 rounded text-sm">수정</button>
        <button class="bg-red-500 text-white px-3 py-1 rounded text-sm">삭제</button>
      </div>

      <div class="flex justify-between items-start mb-1">
        <h1 class="text-2xl font-bold">{{ post.title }}</h1>
      </div>

      <div class="flex justify-between items-center mb-4">
        <div class="flex items-center gap-2">
          <img src="@/assets/image/default-profile.png" class="w-8 h-8 rounded-full" />
          <div style="line-height: 1.1;">
            <p class="text-base font-bold text-black m-0">{{ post.author }}</p>
            <p class="text-sm text-neutral-500 m-0">{{ post.createdAt }}</p>
          </div>
        </div>
        <div class="flex items-center gap-2">
          <button @click="toggleLike" class="flex items-center border px-3 py-1 rounded gap-2">
            <img :src="post.liked ? '/src/assets/image/heart-active.png' : '/src/assets/image/heart-default.png'" class="w-4 h-4" />
            <span>{{ post.likeCount }}</span>
          </button>
          <button @click="openPostReportModal" class="bg-red-500 text-white px-3 py-1 rounded text-sm">신고</button>
        </div>
      </div>

      <div class="border p-4 rounded whitespace-pre-line leading-relaxed mb-4">
        {{ post.content }}
      </div>

      <div class="mt-4 text-sm">
        <strong>첨부파일</strong> (1개 {{ post.attachment.size }})<br />
        <span class="text-blue-600 underline cursor-pointer">{{ post.attachment.name }}</span>
      </div>

      <div class="mt-10">
        <h2 class="text-lg font-semibold mb-4">댓글 {{ comments.length }}</h2>

        <ul class="space-y-4">
          <li v-for="c in paginatedComments" :key="c.id" class="border-b pb-2">
            <div class="flex items-center gap-2 text-sm text-gray-600 mb-1">
              <img src="@/assets/image/default-profile.png" class="w-6 h-6 rounded-full" />
              <span class="font-semibold">{{ c.nickname }}</span>
              <span class="text-xs">{{ c.date }}</span>
            </div>
            <p class="text-sm">{{ c.content }}</p>
            <!-- ✅ 댓글 버튼 (삭제 & 신고) -->
            <div class="flex justify-between mt-2">
              <!-- 삭제 버튼 -->
              <button
                  class="bg-red-400 text-white text-xs px-3 py-1 rounded"
                  @click="comments.value = comments.value.filter(comment => comment.id !== c.id)"
              >
                삭제
              </button>

              <!-- 신고 버튼 -->
              <button
                  class="bg-red-500 text-white text-xs px-3 py-1 rounded"
                  @click="() => openCommentReportModal(c.id)"
              >
                신고
              </button>

            </div>
          </li>
        </ul>

        <div class="flex justify-center gap-2 mt-6 text-sm text-gray-600">
          <button @click="changePage(currentPage - 1)" :disabled="currentPage === 1" class="px-2">&lt;</button>
          <button
              v-for="n in totalPages"
              :key="n"
              @click="changePage(n)"
              :class="['px-3 py-1 rounded', currentPage === n ? 'bg-blue-500 text-white' : '']"
          >
            {{ n }}
          </button>
          <button @click="changePage(currentPage + 1)" :disabled="currentPage === totalPages" class="px-2">&gt;</button>
        </div>

        <div class="mt-6 flex gap-2">
          <input
              v-model="newComment"
              type="text"
              placeholder="댓글을 작성하세요."
              class="flex-1 border px-4 py-2 rounded text-sm"
          />
          <button @click="submitComment" class="bg-blue-500 text-white px-4 py-2 rounded text-sm">등록</button>
        </div>
      </div>

      <ReportModal
          v-if="isReportModalOpen"
          @close="closeReportModal"
          @submit="handleReportSubmit"
      /> </div>
  </section>
</template>
