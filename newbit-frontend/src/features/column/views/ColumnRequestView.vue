<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Editor from '@toast-ui/editor'

import '@toast-ui/editor/dist/toastui-editor.css'

const router = useRouter()

const title = ref('')
const price = ref(null)
const series = ref('')
const content = ref('')
const thumbnailFile = ref(null)
const thumbnailPreview = ref(null)
let editorInstance = null

const editorContainer = ref(null)

const seriesList = [
  { id: '', name: '선택 없음' },
  { id: '1', name: '시리즈 제목1' },
  { id: '2', name: '시리즈 제목2' },
  { id: '3', name: '강한 사람이 되는 방법' },
  { id: '4', name: '버텨야 할 때와 그만두어야 할 때' },
]

// 썸네일 선택
const onThumbnailChange = (e) => {
  const file = e.target.files[0]
  if (file) {
    thumbnailFile.value = file
    thumbnailPreview.value = URL.createObjectURL(file)
  }
}

// 에디터 마운트
onMounted(() => {
  editorInstance = new Editor({
    el: editorContainer.value,
    height: '400px',
    initialEditType: 'wysiwyg',
    previewStyle: 'vertical',
    initialValue: '내용을 입력해 주세요.',
    events: {
      change: () => {
        content.value = editorInstance.getMarkdown()
      },
    },
  })
})

const submit = () => {
  console.log({
    title: title.value,
    price: price.value,
    seriesId: series.value,
    content: content.value,
    thumbnail: thumbnailFile.value,
  })
}
</script>

<template>
  <div class="max-w-[1100px] mx-auto py-10 px-4">
    <router-link
        to="/columns"
        class="inline-flex items-center gap-2 text-[var(--newbittext)] text-13px-regular bg-[var(--newbitlightmode)] border border-[var(--newbitdivider)] px-4 py-2 rounded-lg shadow-sm hover:bg-[var(--newbitlightmode-hover)] transition mb-6"
    >
      <span class="text-xl">←</span>
      <span>목록으로</span>
    </router-link>

    <h1 class="text-heading2 mb-6">칼럼 등록하기</h1>

    <div class="flex gap-6">
      <!-- 썸네일 -->
      <div class="w-[220px] flex flex-col items-center">
        <div class="w-full h-[160px] bg-gray-100 rounded flex items-center justify-center mb-2 overflow-hidden">
          <img :src="thumbnailPreview || '/src/assets/image/product-skeleton.png'" class="w-full h-full object-cover" />
        </div>
        <input type="file" @change="onThumbnailChange" />
      </div>

      <!-- 우측 -->
      <div class="flex-1 space-y-4">
        <input v-model="title" type="text" placeholder="제목을 입력하세요"
               class="w-full border px-4 py-2 rounded text-16px-regular" />
        <div class="flex items-center gap-2">
          <img src="@/assets/image/diamond-icon.png" class="w-5 h-5" />
          <input v-model="price" type="number" placeholder="가격"
                 class="w-24 border px-3 py-1 rounded" />
        </div>
        <select v-model="series" class="w-full border rounded px-4 py-2">
          <option disabled value="">시리즈 선택</option>
          <option v-for="s in seriesList" :key="s.id" :value="s.id">{{ s.name }}</option>
        </select>

        <!-- 에디터 렌더 대상 -->
        <div ref="editorContainer"></div>

        <div class="flex justify-end gap-2 mt-4">
          <button class="bg-[var(--newbitred)] text-white px-4 py-2 rounded" @click="router.push('/columns')">취소</button>
          <button class="bg-blue-500 text-white px-4 py-2 rounded" @click="submit">등록</button>
        </div>
      </div>
    </div>
  </div>
</template>
