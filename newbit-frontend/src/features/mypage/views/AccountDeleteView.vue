<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { deleteUser } from '@/api/user.js' // deleteUser API 함수 사용

const password = ref('')
const showModal = ref(false)
const errorMessage = ref('')
const successMessage = ref('')
const router = useRouter()

const handleWithdraw = () => {
  errorMessage.value = ''
  successMessage.value = ''
  showModal.value = true
}

const submitWithdrawRequest = async () => {
  try {
    const response = await deleteUser({ password: password.value })

    if (response.data?.success) {
      successMessage.value = '회원 탈퇴가 완료되었습니다.'
      errorMessage.value = ''
      showModal.value = false

      // 로그아웃 처리 (토큰 삭제)
      localStorage.removeItem('accessToken')

      // 메인 페이지로 이동
      await router.push('/')
    } else {
      errorMessage.value = response.data?.message || '탈퇴에 실패했습니다.'
      showModal.value = false
    }
  } catch (error) {

    if (error.response?.data?.message) {
      errorMessage.value = error.response.data.message
    } else {
      errorMessage.value = '요청 중 오류가 발생했습니다.'
    }
    showModal.value = false
  }
}
</script>

<template>
  <div class="w-full max-w-4xl mx-auto">
    <h2 class="text-heading3 mb-4">회원 탈퇴</h2>
    <div class="flex items-center justify-center">
      <div class="w-full px-36 py-24 border rounded-lg shadow-sm">
        <form @submit.prevent="handleWithdraw" class="space-y-6 flex flex-col items-center justify-center">
          <div class="w-full flex flex-col items-center justify-center">
            <label for="password" class="block text-sm text-16px-regular mb-4 w-3/5">비밀번호</label>
            <input
                id="password"
                type="password"
                v-model="password"
                placeholder="비밀번호 입력"
                class="w-3/5 px-4 py-3 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-400"
                required
            />
          </div>

          <button
              type="submit"
              class="w-3/5 py-3 bg-[var(--newbitnormal)] text-white rounded-xl text-center hover:bg-[var(--newbitnormal-hover)] transition"
          >
            회원 탈퇴
          </button>

          <!-- 에러 메시지 -->
          <p v-if="errorMessage" class="text-red-500 text-sm w-3/5 text-left mt-2">
            {{ errorMessage }}
          </p>

          <!-- 성공 메시지 -->
          <p v-if="successMessage" class="text-blue-500 text-sm w-3/5 text-left mt-2">
            {{ successMessage }}
          </p>
        </form>
      </div>
    </div>

    <!-- 임시 확인 모달 -->
    <div v-if="showModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 w-full max-w-md text-center shadow-lg">
        <p class="text-lg font-medium mb-6">정말로 회원을 탈퇴하시겠습니까?</p>
        <div class="flex justify-center space-x-4">
          <button
              @click="submitWithdrawRequest"
              class="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600"
          >
            확인
          </button>
          <button
              @click="showModal = false"
              class="bg-gray-300 text-gray-800 px-4 py-2 rounded hover:bg-gray-400"
          >
            취소
          </button>
        </div>
      </div>
    </div>
  </div>
</template>