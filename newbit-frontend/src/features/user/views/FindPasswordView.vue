<script setup>
import { useRouter } from 'vue-router'
import FindPasswordForm from '@/features/user/components/FindPasswordForm.vue'

const router = useRouter()

const handleFindPassword = async (formData) => {
  console.log('비밀번호 찾기 요청:', formData)

  // 실제 API 호출이 들어갈 수 있음
  // const response = await api.findPassword(formData)
  // if (response.success) { ... }
}

const goFindId = () => {
  router.push('/find/id')
}

const goFindPassword = () => {
  // 현재 페이지라 이동 없음
}

const goSignup = () => {
  router.push('/signup')
}
</script>

<template>
  <div class="min-h-screen flex items-center justify-center">
    <FindPasswordForm
        @submit="handleFindPassword"
        @goFindId="goFindId"
        @goFindPassword="goFindPassword"
        @goSignup="goSignup"
    />
  </div>
</template>

<style scoped>
/* 필요 시 스타일 추가 */
</style>
