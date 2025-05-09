<script setup>
import {onMounted, ref, computed, watch} from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useToast } from 'vue-toastification'
import {getPostDetail } from '@/api/post'
import { deletePost } from '@/api/post'
import { postComment } from '@/api/post'
import { deleteComment } from '@/api/post'
import { reportPost, reportComment } from '@/api/post'
import { marked } from 'marked'
import { useAuthStore } from '@/features/stores/auth.js'
import ReportModal from '@/features/post/components/ReportModal.vue'
import DeleteConfirmModal from '@/features/post/components/DeleteConfirmModal.vue'

const convertReasonToId = (reason) => {
  const reasonMap = {
    '욕설, 비하': 1,
    '혐오 표현, 차별': 2,
    '음란물, 선정적 표현': 3,
    '사기, 사칭': 4,
    '광고, 판매, 스팸': 5,
    '불법 정보, 범죄 조장': 6,
    '개인정보 노출, 사생활 침해': 7,
    '저작권 침해': 8,
    '도배 행위': 9,
    '타인 명예 훼손': 10,
    '기타': 11
  }

  return reasonMap[reason] || 11
}

const deleteModalTitle = ref('게시글 삭제')
const deleteModalMessage = ref('게시글을 삭제하시겠습니까?')

const post = ref(null)

const authStore = useAuthStore()
const currentUserId = authStore.userId

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

const confirmCommentDelete = async () => {
  try {
    await deleteComment(post.value.id, commentToDeleteId.value)
    toast.success('댓글이 삭제되었습니다.')
    await fetchPostDetail() // 댓글 목록 다시 불러오기
  } catch (e) {
    toast.error('댓글 삭제에 실패했습니다.')
    console.error('댓글 삭제 오류:', e)
  } finally {
    isCommentDeleteModalOpen.value = false
  }
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
    const msg = post.value.isNotice ? '공지사항이 삭제되었습니다.' : '게시글이 삭제되었습니다.'
    toast.success(msg)
    router.push('/posts')
  } catch (e) {
    toast.error('삭제에 실패했습니다.')
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

  const basePayload = {
    userId: authStore.userId,
    reportTypeId: convertReasonToId(reportData.reason),
    content: reportData.content
  }

  try {
    if (reportType.value === 'post') {
      reportPost({
        ...basePayload,
        postId: reportedId.value
      })
    } else {
      reportComment({
        ...basePayload,
        commentId: reportedId.value
      })
    }

    toast.success(`${typeLabel} 신고가 접수되었습니다.`)
  } catch (e) {
    toast.error(`${typeLabel} 신고에 실패했습니다.`)
    console.error('🚨 신고 오류:', e)
  } finally {
    closeReportModal()
  }
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

const submitComment = async () => {
  if (!newComment.value.trim()) return

  try {
    await postComment(post.value.id, newComment.value.trim())
    toast.success('댓글이 등록되었습니다.')
    newComment.value = ''
    await fetchPostDetail() // 댓글 목록 다시 불러오기
  } catch (e) {
    toast.error('댓글 등록에 실패했습니다.')
    console.error('댓글 등록 오류:', e)
  }
}

marked.setOptions({
  breaks: true
})

const fetchPostDetail = async () => {
  try {
    const res = await getPostDetail(postId)
    console.log('📦 post detail:', res)

    console.log("📦 백엔드 응답 전체:", res)
    console.log('✅ userId (작성자):', res.userId)
    console.log('✅ currentUserId:', currentUserId)
    console.log('✅ 비교 결과:', res.userId === currentUserId)


    // ✅ 모달 제목 여기서 직접 설정
    const isNotice = res.notice === true || res.notice === 'true' || res.notice === 1;

    deleteModalTitle.value = isNotice ? '공지사항 삭제' : '게시글 삭제';
    deleteModalMessage.value = isNotice
        ? '공지사항을 삭제하시겠습니까?'
        : '게시글을 삭제하시겠습니까?';

    post.value = {
      id: res.id,
      title: res.title,
      author: res.writerName,
      authorId: res.userId,
      createdAt: res.createdAt.replace('T', ' ').slice(0, 16),
      content: marked(res.content),
      likeCount: res.likeCount,
      liked: false,
      isNotice, // 여기는 프론트 내부에서 쓰는 용도니까 그대로 사용해도 OK
      attachment: {
        name: res.imageUrls?.[0]?.split('/').pop() || 'Newbit.jpg',
        size: '2KB'
      }
    }

    comments.value = res.comments.map((c) => ({
      id: c.id,
      nickname: c.writerName,
      userId: c.userId,
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
      <!-- 수정/삭제 버튼: 게시글 작성자만 표시 -->
      <div class="flex justify-end gap-2 mb-2" v-if="post.authorId === currentUserId">
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

      <div class="border p-4 rounded leading-relaxed mb-4" v-html="post.content"></div>

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
              <!-- 신고 버튼 -->
              <button
                  class="bg-[var(--newbitred)] text-white text-xs px-3 py-1 rounded"
                  @click="openCommentReportModal(c.id)"
              >
                신고
              </button>

              <!-- 삭제 버튼: 본인만 표시 -->
              <button
                  v-if="c.userId === currentUserId"
                  class="bg-[var(--newbitred)] text-white text-xs px-3 py-1 rounded"
                  @click="openCommentDeleteModal(c.id)"
              >
                삭제
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
          v-if="isReportModalOpen && reportedId && reportType"
          :title="reportType === 'post' ? '게시글 신고' : '댓글 신고'"
          @close="closeReportModal"
          @submit="handleReportSubmit"
      />
      <DeleteConfirmModal
          v-if="isDeleteModalOpen"
          :title="deleteModalTitle"
          :message="deleteModalMessage"
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
