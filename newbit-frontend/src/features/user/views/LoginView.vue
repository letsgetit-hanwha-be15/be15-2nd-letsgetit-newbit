<script setup>
import { useRouter } from 'vue-router'
import LoginForm from '@/features/user/components/LoginForm.vue'

const router = useRouter()

const handleLogin = async (formData) => {
  console.log('로그인 시도:', formData)

  // 실제 로그인 로직이 들어갈 위치 (예: API 호출)
  // const response = await loginApi(formData)
  // if (response.success) { ... }

  router.push('/') // 로그인 성공 시 루트로 이동
}

const goFindId = () => {
  router.push('/find-id')
}

const goFindPassword = () => {
  router.push('/find-password')
}

const goSignup = () => {
  router.push('/signup')
}
</script>

<template>
  <div class="min-h-screen flex items-center justify-center">
    <LoginForm
        @submit="handleLogin"
        @goFindId="goFindId"
        @goFindPassword="goFindPassword"
        @goSignup="goSignup"
    />
  </div>
</template>

<style scoped>
/* 필요 시 추가 스타일링 */
</style>
