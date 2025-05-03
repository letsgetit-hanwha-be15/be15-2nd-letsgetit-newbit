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
  name: '',
  nickname: '',
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
      form.name &&
      form.nickname
  )
})

function onSubmit() {
  emit('submit', {
    ...form,
    techstacks: form.techstacks
  })
}
</script>

<template>
  <form class="signup-container" @submit.prevent="onSubmit">
    <div class="logo">
      <img src="@/assets/image/logo.png" alt="Newbit 로고" />
      <h2>회원가입</h2>
    </div>

    <div class="form-group">
      <label>아이디(이메일)</label>
      <input type="email" v-model.trim="form.email" placeholder="Email" required />
    </div>

    <div class="form-group">
      <label>비밀번호 (영문, 숫자, 특수문자 포함 8자리 이상)</label>
      <input type="password" v-model="form.password" placeholder="Password" required />
    </div>

    <div class="form-group">
      <label>이름</label>
      <input type="text" v-model="form.name" placeholder="Name" required />
    </div>

    <div class="form-group">
      <label>닉네임</label>
      <input type="text" v-model="form.nickname" placeholder="Nickname" required />
    </div>

    <div class="form-group">
      <label>직종</label>
      <select v-model="form.job" class="select-placeholder select-job">
        <option disabled value="">Job</option>
        <option value="frontend">프론트엔드</option>
        <option value="backend">백엔드</option>
        <option value="designer">디자이너</option>
        <option value="pm">PM</option>
      </select>
    </div>

    <div class="form-group">
      <label>기술 스택 (중복 선택 가능)</label>
      <select @change="onSelectTechstack" class="select-placeholder select-techstack">
        <option disabled selected value="">Techstack</option>
        <option v-for="stack in allTechstacks" :key="stack" :value="stack">
          {{ stack.charAt(0).toUpperCase() + stack.slice(1) }}
        </option>
      </select>

      <div class="techstack-tags-wrapper">
        <div class="techstack-tags">
          <span
              v-for="(item, index) in form.techstacks"
              :key="item"
              class="tag"
          >
            {{ item.charAt(0).toUpperCase() + item.slice(1) }}
            <span class="remove" @click="removeTechstack(index)">×</span>
          </span>
        </div>
      </div>
    </div>

    <button :disabled="!isValid" type="submit">회원 가입</button>
  </form>
</template>

<style scoped>
.signup-container {
  width: 480px;
  margin: 60px auto;
  padding: 40px 32px;
  background-color: white;
  border-radius: 12px;
  box-sizing: border-box;
  min-height: fit-content;
}
.logo {
  text-align: center;
  margin-bottom: 28px;
}
.logo img {
  width: 195px;
  height: 72px;
  margin: 0 auto 8px;
  display: block;
}
.logo h2 {
  font-size: 20px;
  font-weight: bold;
  margin-top: 8px;
}
.form-group {
  margin-bottom: 20px;
}
.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 20px;
  font-weight: 500;
  color: #333;
}
.form-group input {
  width: 417px;
  height: 60px;
  padding: 0 16px;
  font-size: 16px;
  border: 1px solid #ccc;
  border-radius: 6px;
  box-sizing: border-box;
  color: #333;
  background-color: white;
}
.form-group select {
  width: 417px;
  height: 60px;
  font-size: 16px;
  border: 1px solid #ccc;
  border-radius: 6px;
  box-sizing: border-box;
  background-color: white;
  color: rgba(3, 3, 4, 0.54); /* placeholder 느낌 */
}

.select-job {
  padding: 0 12px;
}
.select-techstack {
  padding: 0 12px;
}
.techstack-tags-wrapper {
  width: 417px;
  margin-top: 10px;
  padding: 10px;
  min-height: 60px;
  border: 1px solid #ccc;
  border-radius: 6px;
  box-sizing: border-box;
  background-color: white;
}
.techstack-tags {
  display: flex;
  flex-wrap: wrap;
}
.tag {
  background-color: #f1f3f5;
  color: #222;
  border-radius: 4px;
  padding: 6px 10px;
  margin: 4px 4px 0 0;
  font-size: 14px;
  display: flex;
  align-items: center;
}
.tag .remove {
  margin-left: 8px;
  cursor: pointer;
  font-weight: bold;
}
button {
  width: 417px;
  height: 48px;
  padding: 14px;
  background-color: #038FFD;
  color: white;
  font-size: 16px;
  font-weight: bold;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  margin-top: 16px;
}
button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
:global(body) {
  background-color: white;
}
</style>
