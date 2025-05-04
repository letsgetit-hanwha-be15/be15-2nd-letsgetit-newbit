<script setup>
import { reactive, computed } from 'vue'

const emit = defineEmits(['submit'])

const allTechstacks = [
  'javascript',
  'java',
  'vue',
  'react',
  'python'
]

const form = reactive({
  email: '',
  password: '',
  userName: '',
  nickname: '',
  phoneNumber: '',
  job: '',
  techstacks: []
})

function onSelectTechstack(event) {
  const value = event.target.value
  if (value && !form.techstacks.includes(value)) {
    form.techstacks.push(value)
  }
  event.target.value = ''
}

function removeTechstack(index) {
  form.techstacks.splice(index, 1)
}

const isValid = computed(() => {
  return (
      form.email.trim() &&
      form.password.length >= 8 &&
      form.userName &&
      form.nickname &&
      /^\d{10,11}$/.test(form.phoneNumber)
  )
})

function onSubmit() {
  emit('submit', {
    email: form.email,
    password: form.password,
    phoneNumber: form.phoneNumber,
    userName: form.userName,
    nickname: form.nickname,
    profileImgUrl: null,
    jobName: form.job || null, // '' → null
    techstackNames: form.techstacks.length ? form.techstacks : null // [] → null
  })
}
</script>

<template>
  <form @submit.prevent="onSubmit" class="w-[480px] mx-auto my-[60px] p-10 bg-white rounded-xl box-border">
    <div class="text-center mb-7">
      <img src="@/assets/image/logo.png" alt="Newbit 로고" class="w-[195px] h-[72px] mx-auto mb-2" />
      <h2 class="text-heading2 text-newbittext mt-2">회원가입</h2>
    </div>

    <div class="mb-5">
      <label class="block mb-2 text-20px-regular text-newbittext">아이디(이메일)</label>
      <input type="email" v-model.trim="form.email" placeholder="Email" required
             class="w-full h-[60px] px-4 border border-[#ccc] rounded-md bg-white text-16px-regular" />
    </div>

    <div class="mb-5">
      <label class="block mb-2 text-20px-regular text-newbittext whitespace-nowrap">
        비밀번호 (영문, 숫자, 특수문자 포함 8자리 이상)
      </label>
      <input type="password" v-model="form.password" placeholder="Password" required
             class="w-full h-[60px] px-4 border border-[#ccc] rounded-md bg-white text-16px-regular" />
    </div>

    <div class="mb-5">
      <label class="block mb-2 text-20px-regular text-newbittext">전화번호</label>
      <input type="tel" v-model="form.phoneNumber" placeholder="01012345678" required
             maxlength="11" pattern="\d{10,11}" title="숫자만 입력 (10~11자리)"
             class="w-full h-[60px] px-4 border border-[#ccc] rounded-md bg-white text-16px-regular" />
    </div>

    <div class="mb-5">
      <label class="block mb-2 text-20px-regular text-newbittext">이름</label>
      <input type="text" v-model="form.userName" placeholder="Name" required
             class="w-full h-[60px] px-4 border border-[#ccc] rounded-md bg-white text-16px-regular" />
    </div>

    <div class="mb-5">
      <label class="block mb-2 text-20px-regular text-newbittext">닉네임</label>
      <input type="text" v-model="form.nickname" placeholder="Nickname" required
             class="w-full h-[60px] px-4 border border-[#ccc] rounded-md bg-white text-16px-regular" />
    </div>

    <div class="mb-5">
      <label class="block mb-2 text-20px-regular text-newbittext">직종</label>
      <select v-model="form.job"
              class="w-full h-[60px] px-3 border border-[#ccc] rounded-md text-[rgba(3,3,4,0.54)] bg-white text-16px-regular">
        <option disabled value="">Job</option>
        <option value="frontend">프론트엔드</option>
        <option value="backend">백엔드</option>
        <option value="designer">디자이너</option>
        <option value="pm">PM</option>
      </select>
    </div>

    <div class="mb-5">
      <label class="block mb-2 text-20px-regular text-newbittext">기술 스택 (중복 선택 가능)</label>
      <select @change="onSelectTechstack"
              class="w-full h-[60px] px-3 border border-[#ccc] rounded-md text-[rgba(3,3,4,0.54)] bg-white text-16px-regular">
        <option disabled selected value="">Techstack</option>
        <option v-for="stack in allTechstacks" :key="stack" :value="stack">
          {{ stack.charAt(0).toUpperCase() + stack.slice(1) }}
        </option>
      </select>

      <div class="mt-2 border border-[#ccc] rounded-md bg-white px-3 py-2 min-h-[60px]">
        <div class="flex flex-wrap">
          <span v-for="(item, index) in form.techstacks" :key="item"
                class="bg-[#f1f3f5] text-[#222] rounded px-2 py-1 text-sm font-normal mr-2 mb-2 flex items-center">
            {{ item.charAt(0).toUpperCase() + item.slice(1) }}
            <span class="ml-2 cursor-pointer font-bold" @click="removeTechstack(index)">×</span>
          </span>
        </div>
      </div>
    </div>

    <button :disabled="!isValid"
            type="submit"
            class="w-full h-[48px] text-white bg-[var(--newbitnormal)] text-button rounded-md mt-4 disabled:opacity-60 disabled:cursor-not-allowed">
      회원 가입
    </button>
  </form>
</template>
