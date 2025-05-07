<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useToast } from 'vue-toastification'
import { updatePost } from '@/api/post'
import { getPostDetail } from '@/api/post'
import Editor from '@toast-ui/editor'
import '@toast-ui/editor/dist/toastui-editor.css'

const toast = useToast()
const route = useRoute()
const router = useRouter()

const postId = route.params.postId
const title = ref('')
const file = ref(null)
const editorRef = ref(null)
let toastEditor = null

const handleFileChange = (e) => {
  file.value = e.target.files[0]
}

const submitEdit = async () => {
  const content = toastEditor?.getMarkdown()

  if (!title.value.trim() || !content.trim()) {
    toast.error('제목과 내용을 입력해주세요.')
    return
  }

  const formData = new FormData()
  formData.append('title', title.value)
  formData.append('content', content)
  if (file.value) formData.append('file', file.value)

  try {
    await updatePost(postId, formData)
    toast.success('게시글이 수정되었습니다!')
    router.push(`/posts/${postId}`)
  } catch (e) {
    toast.error('게시글 수정에 실패했습니다.')
  }
}

const fetchPostData = async () => {
  try {
    const data = await getPostDetail(postId) // 실제 서버에서 게시글 정보 조회
    title.value = data.title
    if (toastEditor) toastEditor.setMarkdown(data.content)
  } catch (e) {
    toast.error('게시글 데이터를 불러오지 못했습니다.')
  }
}

onMounted(() => {
  toastEditor = new Editor({
    el: editorRef.value,
    height: '400px',
    initialEditType: 'markdown',
    previewStyle: 'vertical',
    usageStatistics: false
  })
  fetchPostData()
})

onBeforeUnmount(() => {
  toastEditor?.destroy()
})
</script>

<template>
  <section class="max-w-3xl mx-auto px-6 py-4 space-y-6">
    <button
        @click="$router.back()"
        class="flex items-center bg-gray-100 hover:bg-gray-200 text-gray-700 px-3 py-1 rounded text-sm"
    >
      <span class="mr-1">←</span> 목록으로
    </button>

    <input
        v-model="title"
        type="text"
        placeholder="제목을 입력하세요"
        class="w-full border px-4 py-3 rounded-lg text-base focus:outline-none focus:ring-1 focus:ring-blue-500"
    />

    <div class="flex gap-2">
      <input
          type="text"
          :value="file?.name || '첨부파일'"
          class="flex-1 border px-4 py-3 rounded-lg text-sm bg-white text-gray-800 cursor-default"
          readonly
      />
      <label
          class="bg-blue-500 text-white px-4 py-2 rounded-lg text-sm cursor-pointer flex items-center"
      >
        찾아보기
        <input type="file" class="hidden" @change="handleFileChange" />
      </label>
    </div>

    <!-- Toast UI 에디터 영역 -->
    <div ref="editorRef" />

    <div class="flex justify-end gap-2 mt-4">
      <button
          @click="$router.back()"
          class="bg-[var(--newbitred)] text-white px-4 py-2 rounded"
      >
        취소
      </button>
      <button
          @click="submitEdit"
          class="bg-blue-500 text-white px-4 py-2 rounded"
      >
        수정
      </button>
    </div>
  </section>
</template>
