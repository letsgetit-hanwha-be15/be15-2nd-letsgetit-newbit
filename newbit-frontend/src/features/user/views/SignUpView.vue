<script setup>
import SignUpForm from '@/features/user/components/SignUpForm.vue'
import Footer from '@/components/common/Footer.vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 회원가입 후 로그인 화면으로 이동
const handleRegister = async (formData) => {
  try {
    console.log('회원가입 데이터:', formData)

    // 👉 나중에 여기에 회원가입 API 연동 예정
    // await axios.post('/api/signup', formData)

    // 성공 시 로그인 페이지로 이동
    router.push('/login')
  } catch (error) {
    console.error('회원가입 실패:', error)
    // 👉 실패 시 에러 메시지 띄우는 로직 추가 가능
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
