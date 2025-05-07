<script setup>
import { ref } from 'vue'

const props = defineProps({
  findError: Boolean // ❗ 부모가 내려주는 에러 상태
})

const emit = defineEmits(['submit', 'goFindId', 'goFindPassword', 'goSignup'])

const userName = ref('')
const phoneNumber = ref('')

const onSubmit = () => {
  if (!userName.value || !phoneNumber.value) {
    alert('이름과 전화번호를 입력해주세요.')
    return
  }

  emit('submit', {
    userName: userName.value,
    phoneNumber: phoneNumber.value
  })
}
</script>

<template>
  <div class="flex min-h-screen bg-white pt-[100px]">
    <div class="w-[480px] mx-auto p-10 bg-white rounded-xl box-border">
      <!-- 로고 및 타이틀 -->
      <div class="text-center mb-7">
        <img src="@/assets/image/logo.png" alt="Newbit Logo" class="w-[195px] h-[72px] mx-auto mb-2" />
        <p class="text-[20px] text-newbittext font-semibold">아이디(이메일) 찾기</p>
      </div>

      <!-- ✅ form 시작 -->
      <form @submit.prevent="onSubmit">
        <!-- 이름 입력 -->
        <div class="mb-5">
          <label for="userName" class="block mb-2 text-20px-regular text-newbittext">이름</label>
          <input
              id="userName"
              v-model="userName"
              type="text"
              placeholder="Name"
              class="w-full h-[60px] px-4 border border-[#ccc] rounded-md bg-white text-16px-regular text-[rgba(3,3,4,0.54)]"
          />
        </div>

        <!-- 전화번호 입력 -->
        <div class="mb-5">
          <label for="phone" class="block mb-2 text-20px-regular text-newbittext">전화번호</label>
          <input
              id="phone"
              v-model="phoneNumber"
              type="tel"
              placeholder="PhoneNumber"
              class="w-full h-[60px] px-4 border border-[#ccc] rounded-md bg-white text-16px-regular text-[rgba(3,3,4,0.54)]"
          />
        </div>

        <!-- 에러 메시지 -->
        <p v-if="findError" class="text-red-500 text-sm mb-5">
          일치하는 회원 정보를 찾을 수 없습니다.
        </p>

        <!-- 아이디 찾기 버튼 -->
        <button
            type="submit"
            class="w-full h-[48px] bg-[var(--newbitnormal)] text-white text-button rounded-md mb-5 hover:bg-[var(--newbitnormal-hover)]"
        >
          아이디 찾기
        </button>
      </form>

      <!-- 하단 링크 -->
      <div class="flex justify-between text-[16px] text-[rgba(3,3,4,0.54)] underline">
        <a @click.prevent="emit('goFindId')" href="#">아이디(이메일)찾기</a>
        <a @click.prevent="emit('goFindPassword')" href="#">비밀번호 찾기</a>
        <a @click.prevent="emit('goSignup')" href="#">회원가입</a>
      </div>
    </div>
  </div>
</template>
