<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useToast } from 'vue-toastification'
import { getColumnDetail, updateColumnRequest } from '@/api/column'
import ColumnRequestForm from '@/features/column/components/ColumnRequestForm.vue'
import { useAuthStore } from '@/features/stores/auth.js'

const authStore = useAuthStore()
const userId = authStore.userId


const route = useRoute()
const router = useRouter()
const toast = useToast()

const columnId = Number(route.params.id)
const seriesList = ref([
  { id: '', name: '선택 없음' },
  { id: '1', name: '시리즈 제목1' },
  { id: '2', name: '시리즈 제목2' },
  { id: '3', name: '강한 사람이 되는 방법' },
  { id: '4', name: '버텨야 할 때와 그만두어야 할 때' }
])

const initialValue = ref(null)

onMounted(async () => {
  try {
    const userId = localStorage.getItem('userId')
    const res = await getColumnDetail(columnId, userId)
    initialValue.value = res.data
  } catch (err) {
    toast.error('칼럼 정보를 불러오지 못했습니다.')
  }
})

const handleSubmit = async (formData) => {
  try {
    const payload = {
      title: formData.title,
      price: formData.price,
      seriesId: formData.seriesId ? Number(formData.seriesId) : null,
      content: formData.content,
      thumbnailUrl: formData.thumbnail
    }

    console.log('수정 요청 payload:', payload)
    await updateColumnRequest(columnId, payload)
    toast.success('칼럼 수정 요청이 완료되었습니다!')
    router.push('/mypage/mentor/column-requests')
  } catch (err) {
    console.error('수정 요청 실패:', err)
    toast.error('수정 요청 중 오류가 발생했습니다.')
  }
}

const handleCancel = () => {
  router.push('/columns')
}
</script>

<template>
  <div class="max-w-[1100px] mx-auto py-10 px-4">
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

    <ColumnRequestForm
        v-if="initialValue"
        :seriesList="seriesList"
        :initialValue="initialValue"
        :submitLabel="'수정 요청'"
        :cancelLabel="'취소'"
        @submit="handleSubmit"
        @cancel="handleCancel"
    />
  </div>
</template>
