<script setup>
import { ref, computed, watch } from 'vue'
import { useToast } from 'vue-toastification'


import { ElMessage } from 'element-plus'
import { CloseBold, Plus } from '@element-plus/icons-vue'

const props = defineProps({ modelValue: Boolean })
const emit = defineEmits(['update:modelValue', 'create'])
const toast = useToast()

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const fallbackImg = new URL('@/assets/image/product-skeleton.png', import.meta.url).href

const form = ref({
  title: '',
  description: '',
  thumbnailFile: null,
  thumbnailUrl: '',
  columns: []
})

const availableColumns = [
  { id: 1, title: '칼럼 제목1' },
  { id: 2, title: '칼럼 제목2' },
  { id: 3, title: '칼럼 제목3' }
]

const selectedColumnIds = ref([])

watch(selectedColumnIds, (ids) => {
  form.value.columns = availableColumns.filter(col => ids.includes(col.id))
})

const removeColumn = (id) => {
  form.value.columns = form.value.columns.filter(col => col.id !== id)
  selectedColumnIds.value = selectedColumnIds.value.filter(cid => cid !== id)
}

const handleThumbnailChange = (uploadFile) => {
  form.value.thumbnailFile = uploadFile.raw
  form.value.thumbnailUrl = URL.createObjectURL(uploadFile.raw)
}

const handleClose = (done) => {
  emit('update:modelValue', false)
  done()
}

const handleCreate = () => {
  if (!form.value.title || !form.value.description) {
    ElMessage.error('제목과 설명을 입력해주세요.')
    return
  }

  const payload = {
    title: form.value.title,
    description: form.value.description,
    thumbnailFile: form.value.thumbnailFile,
    columnIds: selectedColumnIds.value.length === 0 ? null : selectedColumnIds.value
  }

  emit('create', payload)
  toast.success('시리즈 발행에 성공했습니다!')
  emit('update:modelValue', false)
}
</script>

<template>
  <el-dialog
      v-model="visible"
      title="📝 새로운 시리즈 발행하기"
      width="560px"
      :close-on-click-modal="false"
      :before-close="handleClose"
  >
    <div class="space-y-5 px-2 py-1">
      <!-- 제목 -->
      <div>
        <div class="label">제목</div>
        <el-input v-model="form.title" placeholder="시리즈 제목을 입력하세요" />
      </div>

      <!-- 설명 -->
      <div>
        <div class="label">설명</div>
        <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="시리즈에 대한 설명을 입력하세요"
        />
      </div>

      <!-- 썸네일 -->
      <div>
        <div class="label">썸네일</div>
        <el-upload
            class="thumbnail-uploader"
            :auto-upload="false"
            :show-file-list="false"
            accept="image/*"
            :on-change="handleThumbnailChange"
        >
          <img v-if="form.thumbnailUrl" :src="form.thumbnailUrl" class="thumbnail-preview" />
          <div v-else class="upload-placeholder">
            <el-icon><Plus /></el-icon>
            <div class="text-xs text-gray-500 mt-1">썸네일 업로드</div>
          </div>
        </el-upload>
      </div>

      <!-- 칼럼 선택 -->
      <div>
        <div class="label">칼럼 선택</div>
        <el-select
            v-model="selectedColumnIds"
            multiple
            filterable
            clearable
            collapse-tags
            collapse-tags-tooltip
            placeholder="포함할 칼럼을 선택하세요"
            class="w-full"
        >
          <el-option
              v-for="col in availableColumns"
              :key="col.id"
              :label="col.title"
              :value="col.id"
          />
        </el-select>
      </div>

      <!-- 선택된 칼럼 -->
      <div v-if="form.columns.length" class="flex flex-col gap-2">
        <div
            v-for="col in form.columns"
            :key="col.id"
            class="flex justify-between items-center border px-4 py-2 rounded bg-gray-50"
        >
          <span>{{ col.title }}</span>
          <el-button
              :icon="CloseBold"
              size="small"
              circle
              type="danger"
              @click="removeColumn(col.id)"
          />
        </div>
      </div>
    </div>

    <!-- 하단 버튼 -->
    <template #footer>
      <div class="flex justify-end gap-2">
        <el-button @click="() => emit('update:modelValue', false)">취소</el-button>
        <el-button type="primary" @click="handleCreate">생성</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<style scoped>
.label {
  margin-bottom: 6px;
  color: var(--newbittext);
  font-family: var(--newbit-text-13px-bold-font-family);
  font-size: var(--newbit-text-13px-bold-font-size);
  font-weight: var(--newbit-text-13px-bold-font-weight);
}

/* 썸네일 업로더 박스 */
.thumbnail-uploader {
  width: 100%;
  max-width: 320px;
  aspect-ratio: 16 / 9;
  border: 1px dashed var(--newbitdivider);
  border-radius: 8px;
  background-color: var(--newbitlightmode);
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
}

/* 썸네일 이미지 미리보기 */
.thumbnail-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
  border-radius: 8px;
}

/* 썸네일 업로드 전 placeholder 스타일 */
.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--newbitgray);
  font-size: var(--newbit-text-10px-regular-font-size);
  font-family: var(--newbit-text-10px-regular-font-family);
}
</style>