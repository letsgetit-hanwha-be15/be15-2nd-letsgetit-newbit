<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useToast } from 'vue-toastification'
import Editor from '@toast-ui/editor'
import '@toast-ui/editor/dist/toastui-editor.css'

// 라우팅 & 알림
const route = useRoute()
const router = useRouter()
const toast = useToast()

// 폼 상태
const title = ref('')
const price = ref(null)
const series = ref('')
const content = ref('')
const thumbnailFile = ref(null)
const thumbnailPreview = ref(null)

// 에디터
const editorRef = ref(null)
let editorInstance = null

// 시리즈 목록 (예시)
const seriesList = [
  { id: '', name: '선택 없음' },
  { id: '1', name: '시리즈 제목1' },
  { id: '2', name: '시리즈 제목2' },
  { id: '3', name: '강한 사람이 되는 방법' },
  { id: '4', name: '버텨야 할 때와 그만두어야 할 때' },
]

// 기본 이미지
const fallbackThumbnail = new URL('@/assets/image/product-skeleton.png', import.meta.url).href

// 썸네일 변경 시 미리보기
const onThumbnailChange = (e) => {
  const file = e.target.files[0]
  if (file) {
    thumbnailFile.value = file
    thumbnailPreview.value = URL.createObjectURL(file)
  }
}

// 에디터 초기화
onMounted(() => {
  editorInstance = new Editor({
    el: editorRef.value,
    height: '400px',
    initialEditType: 'wysiwyg',
    previewStyle: 'vertical',
    initialValue: '내용을 입력해 주세요.',
    events: {
      change: () => {
        content.value = editorInstance.getMarkdown()
      }
    }
  })
})

// 제출
const handleSubmit = () => {
  console.log({
    title: title.value,
    price: price.value,
    seriesId: series.value,
    content: content.value,
    thumbnail: thumbnailFile.value,
  })

  toast.success('수정 요청에 성공했습니다!')
  router.push('/columns')
}

// 취소
const handleCancel = () => {
  router.push('/columns')
}
</script>

<template>
  <div class="max-w-[1100px] mx-auto py-10 px-4">
    <!-- 목록으로 버튼 -->
    <router-link
        to="/columns"
        class="inline-flex items-center gap-2 text-[var(--newbittext)] text-13px-regular
             bg-[var(--newbitlightmode)] border border-[var(--newbitdivider)]
             px-4 py-2 rounded-lg shadow-sm hover:bg-[var(--newbitlightmode-hover)] transition mb-6"
    >
      <span class="text-xl">←</span>
      <span>목록으로</span>
    </router-link>

    <h1 class="text-heading2 mb-6">칼럼 수정 요청하기</h1>

    <div class="flex gap-6">
      <!-- 썸네일 -->
      <div class="w-[220px] flex flex-col items-center">
        <div class="w-full h-[160px] bg-gray-100 rounded flex items-center justify-center mb-2 overflow-hidden">
          <img :src="thumbnailPreview || fallbackThumbnail" class="w-full h-full object-cover" />
        </div>
        <input type="file" @change="onThumbnailChange" />
      </div>

      <!-- 입력 폼 -->
      <div class="flex-1 space-y-4">
        <!-- 제목 -->
        <input
            v-model="title"
            type="text"
            placeholder="제목을 입력하세요"
            class="w-full border px-4 py-2 rounded"
        />

        <!-- 가격 + 시리즈 드롭다운 -->
        <div class="flex items-center gap-2">
          <img src="@/assets/image/diamond-icon.png" class="w-5 h-5" />
          <input
              v-model="price"
              type="number"
              placeholder="가격"
              class="w-24 border px-3 py-2 rounded"
          />

          <!-- 드롭다운 (Element Plus) -->
          <el-select
              v-model="series"
              placeholder="시리즈 선택"
              clearable
              filterable
              class="flex-1"
          >
            <el-option
                v-for="s in seriesList"
                :key="s.id"
                :label="s.name"
                :value="s.id"
            />
          </el-select>
        </div>

        <!-- 에디터 -->
        <div ref="editorRef"></div>

        <!-- 버튼 -->
        <div class="flex justify-end gap-2 mt-4">
          <button
              @click="handleCancel"
              class="bg-[var(--newbitred)] text-white px-4 py-2 rounded"
          >
            취소
          </button>
          <button
              @click="handleSubmit"
              class="bg-blue-500 text-white px-4 py-2 rounded"
          >
            수정
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
</style>
