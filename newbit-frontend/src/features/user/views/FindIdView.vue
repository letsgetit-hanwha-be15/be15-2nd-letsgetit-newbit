<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { FindId } from '@/api/user'
import FindIdForm from '@/features/user/components/FindIdForm.vue'

const router = useRouter()
const findError = ref(false) // 🔴 에러 상태

const handleFindId = async (formData) => {
  try {
    const response = await FindId(formData)

    if (response.data.success) {
      findError.value = false // 🔵 에러 없음
      alert(`가입된 이메일은 ${response.data.data.email} 입니다.`)
      router.push('/login')
    } else {
      findError.value = true // 🔴 에러 상태 전달
      const errorCode = response.data.error?.code || response.data.code
      const errorMessage = response.data.error?.message || response.data.message

      if (errorCode === '10002') {
        alert('이름 혹은 핸드폰번호를 잘못 입력하셨습니다.')
      } else {
        alert(errorMessage || '아이디를 찾을 수 없습니다.')
      }
    }
  } catch (error) {
    console.error('❗Axios Error:', error)
    findError.value = true // 🔴 에러 상태 전달

    const errorData = error?.response?.data
    const errorCode = errorData?.error?.code || errorData?.code
    const errorMessage = errorData?.error?.message || errorData?.message

    if (errorCode === '10002') {
      alert('이름 혹은 핸드폰번호를 잘못 입력하셨습니다.')
    } else if (errorMessage) {
      alert(errorMessage)
    } else {
      alert('서버 오류가 발생했습니다.')
    }
  }
}

const goLogin = () => router.push('/login')
const goFindPassword = () => router.push('/find/password')
const goSignup = () => router.push('/signup')
</script>

<template>
  <div class="min-h-screen flex items-center justify-center">
    <FindIdForm
        :findError="findError"
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
