<script setup>
import { useRouter } from 'vue-router'
import { FindPassword } from '@/api/user' // ✅ API 함수 import
import FindPasswordForm from '@/features/user/components/FindPasswordForm.vue'

const router = useRouter()

const handleFindPassword = async (formData) => {
  try {
    const response = await FindPassword(formData)

    if (response.data.success) {
      alert('임시 비밀번호가 이메일로 전송되었습니다.')
      router.push('/login')
    } else {
      const errorCode = response.data.error?.code || response.data.code
      const errorMessage = response.data.error?.message || response.data.message

      if (errorCode === '10004') {
        alert('사용자를 찾을 수 없습니다.')
      } else {
        alert(errorMessage || '비밀번호 찾기에 실패했습니다.')
      }
    }
  } catch (error) {
    console.error('❗Axios Error:', error)

    const errorData = error?.response?.data
    const errorCode = errorData?.error?.code || errorData?.code
    const errorMessage = errorData?.error?.message || errorData?.message

    if (errorCode === '10003') {
      alert('가입된 이메일 정보를 찾을 수 없습니다.')
    } else if (errorMessage) {
      alert(errorMessage)
    } else {
      alert('서버 오류가 발생했습니다.')
    }
  }
}

const goFindId = () => {
  router.push('/find/id')
}

const goFindPassword = () => {
  // 현재 페이지
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
