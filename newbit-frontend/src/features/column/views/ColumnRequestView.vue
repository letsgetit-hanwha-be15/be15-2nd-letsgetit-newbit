<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useToast } from 'vue-toastification'
import ColumnRequestForm from '@/features/column/components/ColumnRequestForm.vue'

const router = useRouter()
const toast = useToast()

// 시리즈 목록 임시 더미데이터
const seriesList = [
  { id: '', name: '선택 없음' },
  { id: '1', name: '시리즈 제목1' },
  { id: '2', name: '시리즈 제목2' },
  { id: '3', name: '강한 사람이 되는 방법' },
  { id: '4', name: '버텨야 할 때와 그만두어야 할 때' }
]

// ColumnRequestForm에서 등록 버튼 누를 때 실행되는 콜백
const handleSubmit = (formData) => {
  console.log('폼 제출됨:', formData)

  // 성공 알림 표시
  toast.success('등록 요청에 성공했습니다!')

  // TODO: API 연동 후 성공 시 이동 처리
  // await columnApi.submit(formData)
  // router.push('/columns')
}

const handleCancel = () => {
  router.push('/columns')  // 칼러 목록 페이지로 이동
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
