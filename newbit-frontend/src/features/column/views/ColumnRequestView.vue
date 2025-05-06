<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useToast } from 'vue-toastification'
import ColumnRequestForm from '@/features/column/components/ColumnRequestForm.vue'
import { createColumnRequest } from '@/api/column'

const router = useRouter()
const toast = useToast()

const seriesList = [
  { id: '', name: '선택 없음' },
  { id: '1', name: '시리즈 제목1' },
  { id: '2', name: '시리즈 제목2' },
  { id: '3', name: '강한 사람이 되는 방법' },
  { id: '4', name: '버텨야 할 때와 그만두어야 할 때' }
]

const handleSubmit = async (formData) => {
  try {
    const payload = {
      title: formData.title,
      price: formData.price,
      seriesId: formData.seriesId ? Number(formData.seriesId) : null, // 문자열이면 숫자로 변환
      content: formData.content || null,
      thumbnailUrl: formData.thumbnail || null
    }

    console.log('🔍 payload:', payload)

    await createColumnRequest(payload)

    toast.success('칼럼 등록 요청이 완료되었습니다!')
    router.push('/mypage/mentor/column-requests')  // 또는 원하는 목록 페이지
  } catch (error) {
    console.error('칼럼 등록 요청 실패:', error)
    toast.error('등록 요청 중 오류가 발생했습니다.')
  }
}

const handleCancel = () => {
  router.push('/columns')
}
</script>

<template>
  <div class="max-w-[1100px] mx-auto py-10 px-4">
    <!-- 목록으로 돌아가기 버튼 -->
    <router-link
        to="/columns"
        class="inline-flex items-center gap-2 text-[var(--newbittext)] text-13px-regular bg-[var(--newbitlightmode)] border border-[var(--newbitdivider)] px-4 py-2 rounded-lg shadow-sm hover:bg-[var(--newbitlightmode-hover)] transition mb-6"
    >
      <span class="text-xl">←</span>
      <span>목록으로</span>
    </router-link>

    <!-- 페이지 제목 -->
    <h1 class="text-heading2 mb-6">칼럼 등록하기</h1>

    <!-- 칼럼 작성 폼 컴포넌트 -->
    <ColumnRequestForm
        :seriesList="seriesList"
        @submit="handleSubmit"
        @cancel="handleCancel" />
  </div>
</template>

<style scoped>
</style>