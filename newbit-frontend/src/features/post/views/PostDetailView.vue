<script setup>
import { onMounted, ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useToast } from 'vue-toastification'
import { getPostDetail } from '@/api/post'
import { deletePost } from '@/api/post'
import ReportModal from '@/features/post/components/ReportModal.vue'
import DeleteConfirmModal from '@/features/post/components/DeleteConfirmModal.vue'

const toast = useToast()
const reportType = ref('') // 'post' or 'comment'
const reportedId = ref(null)
const router = useRouter()
const isDeleteModalOpen = ref(false)
const isCommentDeleteModalOpen = ref(false)
const commentToDeleteId = ref(null)

const openCommentDeleteModal = (commentId) => {
  commentToDeleteId.value = commentId
  isCommentDeleteModalOpen.value = true
}

const closeCommentDeleteModal = () => {
  isCommentDeleteModalOpen.value = false
}

const confirmCommentDelete = () => {
  comments.value = comments.value.filter(comment => comment.id !== commentToDeleteId.value)
  isCommentDeleteModalOpen.value = false
  toast.success('댓글이 삭제되었습니다.')
}

const openDeleteModal = () => {
  isDeleteModalOpen.value = true
}

const closeDeleteModal = () => {
  isDeleteModalOpen.value = false
}

const confirmDelete = async () => {
  try {
    await deletePost(post.value.id)
    toast.success('삭제되었습니다.')
    router.push('/posts')
  } catch (e) {
    toast.error('삭제 실패')
  } finally {
    isDeleteModalOpen.value = false
  }
}

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

const goToEdit = () => {
  router.push(`/posts/${post.value.id}/edit`)
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
  console.log(`${typeLabel} 신고:`, {
    type: reportType.value,
    targetId: reportedId.value,
    reason: reportData.reason,
    content: reportData.content
  })

  toast.success(`${typeLabel} 신고가 접수되었습니다.`)
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
  try {
    const res = await getPostDetail(postId)
    post.value = {
      id: res.id,
      title: res.title,
      author: res.writerName,
      createdAt: res.createdAt.replace('T', ' ').slice(0, 16),
      content: res.content,
      likeCount: res.likeCount,
      liked: false,
      attachment: {
        name: res.imageUrls?.[0]?.split('/').pop() || '첨부 이미지',
        size: '알 수 없음'
      }
    }

    comments.value = res.comments.map((c) => ({
      id: c.id,
      nickname: c.writerName,
      date: new Date().toISOString().slice(0, 16).replace('T', ' '),
      content: c.content
    }))
  } catch (e) {
    console.error('게시글 상세 조회 실패:', e)
    toast.error('게시글을 불러오지 못했습니다.')
  }
}

onMounted(fetchPostDetail)
</script>

<template>
  <section class="p-8 max-w-3xl mx-auto pb-40">
    <div v-if="post">
      <div class="flex justify-end gap-2 mb-2">
        <button
            @click="goToEdit"
            class="bg-blue-500 text-white px-3 py-1 rounded text-sm"
        >
          수정
        </button>
        <button
            @click="openDeleteModal"
            class="bg-[var(--newbitred)] text-white px-3 py-1 rounded text-sm"
        >
          삭제
        </button>
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
          <button @click="openPostReportModal" class="bg-[var(--newbitred)] text-white px-3 py-1 rounded text-sm">신고</button>
        </div>
      </div>

      <div class="border p-4 rounded whitespace-pre-line leading-relaxed mb-4">
        {{ post.content }}
      </div>

      <div class="mt-4 text-sm">
        <strong>첨부파일</strong> (1개 {{ post.attachment.size }})<br />
        <span class="text-blue-500 underline cursor-pointer">{{ post.attachment.name }}</span>
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
            <!-- 댓글 버튼 (삭제 & 신고) -->
            <div class="flex justify-between mt-2">
              <!-- 삭제 버튼 -->
              <button
                  class="bg-[var(--newbitred)] text-white text-xs px-3 py-1 rounded"
                  @click="openCommentDeleteModal(c.id)"
              >
                삭제
              </button>

              <!-- 신고 버튼 -->
              <button
                  class="bg-[var(--newbitred)] text-white text-xs px-3 py-1 rounded"
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
          :title="reportType === 'post' ? '게시글 신고' : '댓글 신고'"
          @close="closeReportModal"
          @submit="handleReportSubmit"
      />
      <DeleteConfirmModal
          v-if="isDeleteModalOpen"
          title="게시글 삭제"
          message="게시글을 삭제하시겠습니까?"
          @close="closeDeleteModal"
          @confirm="confirmDelete"
      />
      <DeleteConfirmModal
          v-if="isCommentDeleteModalOpen"
          title="댓글 삭제"
          message="댓글을 삭제하시겠습니까?"
          @close="closeCommentDeleteModal"
          @confirm="confirmCommentDelete"
      /> </div>
  </section>
</template>
