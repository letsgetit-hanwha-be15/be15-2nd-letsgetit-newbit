<script setup>
import SignUpForm from '@/features/user/components/SignUpForm.vue'
import { useRouter } from 'vue-router'
import { SignUpUser } from "@/api/user.js"

const router = useRouter()

// 회원가입 후 로그인 화면으로 이동
const handleRegister = async (formData) => {
  try {
    await SignUpUser(formData)
    alert('회원가입이 완료되었습니다.')
    await router.push('/login')
  } catch (error) {
    const code = error?.response?.data?.code
    switch (code) {
      case '10007':
        alert('이미 존재하는 핸드폰 번호입니다.')
        break
      case '10008':
        alert('이미 존재하는 닉네임입니다.')
        break
      case '10009':
        alert('비밀번호 형식이 올바르지 않습니다. 최소 8자, 영문자, 숫자, 특수문자를 포함해야 합니다.')
        break
      case '10010':
        alert('현재 비밀번호가 올바르지 않습니다.')
        break
      default:
        alert('올바른 정보를 입력해주세요.')
    }
    console.error('회원 가입 실패:', error)
  }
}
</script>

<template>
  <div class="flex flex-col min-h-screen bg-white">
    <main class="flex-1 flex items-center justify-center">
      <SignUpForm @submit="handleRegister" />
    </main>
  </div>
</template>

<style scoped>
/* 필요 시 추가 */
</style>
