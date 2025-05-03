<script setup>
import { useRouter } from 'vue-router'
import FindIdForm from '@/features/user/components/FindIdForm.vue'

const router = useRouter()

const handleFindId = async (formData) => {
  console.log('아이디 찾기 요청:', formData)

  // 여기에 실제 API 호출이 들어갈 수 있음
  // const response = await api.findId(formData)
  // if (response.success) { ... }
}

const goLogin = () => {
  router.push('/find/id')
}

const goFindPassword = () => {
  router.push('/find/password')
}

const goSignup = () => {
  router.push('/signup')
}
</script>

<template>
  <div class="min-h-screen flex items-center justify-center">
    <FindIdForm
        @submit="handleFindId"
        @goLogin="goLogin"
        @goFindPassword="goFindPassword"
        @goSignup="goSignup"
    />
  </div>
</template>

<style scoped>
/* 필요 시 스타일 추가 */
</style>
