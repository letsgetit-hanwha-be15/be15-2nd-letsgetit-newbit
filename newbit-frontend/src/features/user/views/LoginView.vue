<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { loginUser } from '@/api/user.js'
import { useAuthStore } from '@/features/stores/auth.js'
import LoginForm from '@/features/user/components/LoginForm.vue'

const router = useRouter()
const authStore = useAuthStore()

// 🔴 로그인 실패 상태
const loginError = ref(false)

const handleLogin = async (formData) => {
  try {
    const resp = await loginUser(formData)

    console.log('🟢 로그인 응답:', resp.data.data)
    const accessToken = resp.data.data.accessToken
    const refreshToken = resp.data.data.refreshToken

    console.log('🟢 refreshToken:', refreshToken)

    localStorage.setItem('accessToken', accessToken)
    localStorage.setItem('refreshToken', refreshToken)

    authStore.setAuth(accessToken)

    loginError.value = false // 로그인 성공 시 에러 제거
    await router.push('/')
  } catch (e) {
    console.error('🔴 로그인 실패', e)
    loginError.value = true // 로그인 실패 시 에러 표시
  }
}

// 보조 이동 함수
const goFindId = () => router.push('/find/id')
const goFindPassword = () => router.push('/find/password')
const goSignup = () => router.push('/signup')
</script>

<template>
  <div class="min-h-screen flex items-center justify-center">
    <LoginForm :loginError="loginError"
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
