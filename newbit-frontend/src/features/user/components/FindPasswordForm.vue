<script setup>
import { ref } from 'vue'

const email = ref('')

const emit = defineEmits(['submit', 'goFindId', 'goFindPassword', 'goSignup'])

const onSubmit = () => {
  if (!email.value) {
    alert('이메일을 입력해주세요.')
    return
  }

  emit('submit', {
    email: email.value
  })
}
</script>

<template>
  <div class="flex min-h-screen bg-white pt-[100px]">
    <div class="w-[480px] mx-auto p-10 bg-white rounded-xl box-border">
      <!-- 로고 및 타이틀 -->
      <div class="text-center mb-7">
        <img src="@/assets/image/logo.png" alt="Newbit Logo" class="w-[195px] h-[72px] mx-auto mb-2" />
        <p class="text-[20px] text-newbittext font-semibold">비밀번호 찾기</p>
      </div>

      <!-- 이메일 입력 -->
      <div class="mb-5">
        <label for="email" class="block mb-2 text-20px-regular text-newbittext">아이디(이메일)</label>
        <input
            id="email"
            v-model="email"
            type="email"
            placeholder="Email"
            class="w-full h-[60px] px-4 border border-[#ccc] rounded-md bg-white text-16px-regular text-[rgba(3,3,4,0.54)]"
        />
      </div>

      <!-- 비밀번호 찾기 버튼 -->
      <button
          @click.prevent="onSubmit"
          class="w-full h-[48px] bg-[var(--newbitnormal)] text-white text-button rounded-md mb-5 hover:bg-[var(--newbitnormal-hover)]"
      >
        비밀번호 찾기
      </button>

      <!-- 하단 링크 -->
      <div class="flex justify-between text-[16px] text-[rgba(3,3,4,0.54)] underline">
        <a @click.prevent="emit('goFindId')" href="#">아이디(이메일)찾기</a>
        <a @click.prevent="emit('goFindPassword')" href="#">비밀번호 찾기</a>
        <a @click.prevent="emit('goSignup')" href="#">회원가입</a>
      </div>
    </div>
  </div>
</template>
