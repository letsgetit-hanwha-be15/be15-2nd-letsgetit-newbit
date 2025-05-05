<script setup>
import { useRouter } from 'vue-router'
import { loginUser } from '@/api/user.js'
import { useAuthStore } from '@/features/stores/auth.js'
import LoginForm from '@/features/user/components/LoginForm.vue'

const router = useRouter()
const authStore = useAuthStore()

const handleLogin = async (formData) => {
  try {
    const resp = await loginUser(formData)

    console.log('🟢 로그인 응답:', resp.data.data) // ✅ 이거 꼭 추가
    const accessToken = resp.data.data.accessToken
    const refreshToken = resp.data.data.refreshToken

    console.log('🟢 refreshToken:', refreshToken) // ✅ null인지 확인

    localStorage.setItem('accessToken', accessToken)
    localStorage.setItem('refreshToken', refreshToken)

    authStore.setAuth(accessToken)

    await router.push('/')
  } catch (e) {
    console.error('로그인 실패', e)
  }
}

// 보조 이동 함수
const goFindId = () => router.push('/find/id')
const goFindPassword = () => router.push('/find/password')
const goSignup = () => router.push('/signup')
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
