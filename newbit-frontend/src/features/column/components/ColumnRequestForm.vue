<script setup>
import { ref, onMounted } from 'vue'
import Editor from '@toast-ui/editor'
import '@toast-ui/editor/dist/toastui-editor.css'

const props = defineProps({
  seriesList: Array,
  cancelLabel: String,
  submitLabel: String,
  initialValue: Object
})

const emit = defineEmits(['submit', 'cancel'])

const title = ref('')
const price = ref(null)
const series = ref('')
const content = ref('')
const thumbnailFile = ref(null)
const thumbnailPreview = ref(null)
const editorRef = ref(null)

let editorInstance = null

onMounted(() => {
  // 초기값 세팅
  if (props.initialValue) {
    title.value = props.initialValue.title || ''
    price.value = props.initialValue.price || null
    series.value = props.initialValue.seriesId || ''
    content.value = props.initialValue.content || ''
    thumbnailPreview.value = props.initialValue.thumbnailUrl || ''
  }

  // Toast UI Editor 초기화
  if (editorRef.value) {
    editorInstance = new Editor({
      el: editorRef.value,
      height: '400px',
      initialEditType: 'wysiwyg',
      previewStyle: 'vertical',
      initialValue: content.value || '내용을 입력해 주세요.',
      events: {
        change: () => {
          content.value = editorInstance.getMarkdown()
        }
      }
    })
  }
})

const onThumbnailChange = (e) => {
  const file = e.target.files[0]
  if (!file) return

  const reader = new FileReader()
  reader.onload = () => {
    thumbnailFile.value = reader.result // base64 문자열 저장
    thumbnailPreview.value = reader.result
  }
  reader.readAsDataURL(file)
}

const handleSubmit = () => {
  emit('submit', {
    title: title.value,
    price: price.value,
    seriesId: series.value,
    content: content.value,
    thumbnail: thumbnailFile.value  // base64 문자열
  })
}

const handleCancel = () => {
  emit('cancel')
}
</script>

<template>
  <div class="flex gap-6">
    <!-- 썸네일 -->
    <div class="w-[220px] flex flex-col items-center">
      <div class="w-full h-[160px] bg-gray-100 rounded flex items-center justify-center mb-2 overflow-hidden">
        <img :src="thumbnailPreview || '/src/assets/image/product-skeleton.png'" class="w-full h-full object-cover" />
      </div>
      <input type="file" @change="onThumbnailChange" />
    </div>

    <!-- 우측 입력 폼 -->
    <div class="flex-1 space-y-4">
      <input
          v-model="title"
          type="text"
          placeholder="제목을 입력하세요"
          class="w-full border px-4 py-2 rounded"
      />

      <div class="flex items-center gap-2">
        <img src="@/assets/image/diamond-icon.png" class="w-5 h-5" />
        <input
            v-model="price"
            type="number"
            placeholder="가격"
            class="w-24 border px-3 py-2 rounded"
        />
        <el-select
            v-model="series"
            placeholder="시리즈 선택"
            clearable
            filterable
            class="flex-1"
        >
          <el-option
              v-for="s in props.seriesList"
              :key="s.id"
              :label="s.name"
              :value="s.id"
          />
        </el-select>
      </div>

      <div ref="editorRef"></div>

      <div class="flex justify-end gap-2 mt-4">
        <button @click="handleCancel" class="bg-[var(--newbitred)] text-white px-4 py-2 rounded">
          {{ cancelLabel || '취소' }}
        </button>
        <button @click="handleSubmit" class="bg-blue-500 text-white px-4 py-2 rounded">
          {{ submitLabel || '등록' }}
        </button>
      </div>
    </div>
  </div>
</template>
