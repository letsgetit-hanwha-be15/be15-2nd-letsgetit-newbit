<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import Editor from '@toast-ui/editor'
import '@toast-ui/editor/dist/toastui-editor.css'

const router = useRouter()

const title = ref('')
const file = ref(null)
const editorRef = ref(null)
let toastEditor = null

const handleFileChange = (e) => {
  file.value = e.target.files[0]
}

const submitPost = async () => {
  const content = toastEditor?.getMarkdown()

  if (!title.value.trim() || !content.trim()) {
    alert('제목과 내용을 입력해주세요.')
    return
  }

  const formData = new FormData()
  formData.append('title', title.value)
  formData.append('content', content)
  if (file.value) formData.append('file', file.value)

  try {
    await fetch('/api/posts', {
      method: 'POST',
      body: formData
    })
    alert('게시글이 등록되었습니다.')
    router.push('/posts')
  } catch (e) {
    alert('게시글 등록 실패')
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
})

onBeforeUnmount(() => {
  toastEditor?.destroy()
})
</script>

<template>  <section class="max-w-3xl mx-auto px-6 py-4 space-y-6">
  <!-- 🔙 목록으로 -->
  <button
      @click="$router.back()"
      class="flex items-center bg-gray-100 hover:bg-gray-200 text-gray-700 px-3 py-1 rounded text-sm"
  >
    <span class="mr-1">←</span> 목록으로
  </button>

  <!-- 제목 입력 -->
  <input
      v-model="title"
      type="text"
      placeholder="제목을 입력하세요"
      class="w-full border px-4 py-3 rounded-lg text-base focus:outline-none focus:ring-1 focus:ring-blue-500"
  />

  <!-- 첨부파일 입력 -->
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

    <!-- Toast UI 에디터 -->
    <div ref="editorRef" />

    <!-- 버튼 -->
    <div class="flex justify-end gap-2 mt-4">
      <button
          @click="$router.back()"
          class="bg-[var(--newbitred)] text-white px-4 py-2 rounded"
      >
        취소
      </button>
      <button
          @click="submitPost"
          class="bg-blue-500 text-white px-4 py-2 rounded"
      >
        등록
      </button>
    </div>
  </section>
</template>

