<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { SignUpUser, FetchJobList, FetchTechstackList } from '@/api/user.js'
import SignUpForm from '@/features/user/components/SignUpForm.vue'

const router = useRouter()
const jobs = ref([])
const techstacks = ref([])

onMounted(async () => {
  try {
    const jobRes = await FetchJobList()
    jobs.value = jobRes.data.data // ✅ .data 안의 data만

    const techstackRes = await FetchTechstackList()
    techstacks.value = techstackRes.data.data // ✅ .data 안의 data만
  } catch (error) {
    console.error('목록 조회 실패:', error)
  }
})

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
        alert('비밀번호 형식이 올바르지 않습니다.')
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
      <SignUpForm
          :jobs="jobs"
          :techstacks="techstacks"
          @submit="handleRegister"
      />
    </main>
  </div>
</template>
