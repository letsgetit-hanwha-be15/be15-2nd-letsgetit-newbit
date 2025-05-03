<script setup>
import { ref } from 'vue'

const name = ref('')
const phoneNumber = ref('')

const emit = defineEmits(['submit', 'goFindId', 'goFindPassword', 'goSignup'])

const onSubmit = () => {
  if (!name.value || !phoneNumber.value) {
    alert('이름과 전화번호를 입력해주세요.')
    return
  }

  emit('submit', {
    name: name.value,
    phoneNumber: phoneNumber.value
  })
}
</script>

<template>
  <div class="login-wrapper">
    <div class="login-container">
      <!-- 로고 + 제목 -->
      <div class="logo-section">
        <img src="@/assets/image/logo.png" alt="Newbit Logo" class="logo-img" />
        <p class="login-title">아이디(이메일) 찾기</p>
      </div>

      <!-- 이름 -->
      <div class="form-group">
        <label for="name">이름</label>
        <input id="name" v-model="name" type="text" placeholder="Name" />
      </div>

      <!-- 전화번호 -->
      <div class="form-group">
        <label for="phone">전화번호</label>
        <input id="phone" v-model="phoneNumber" type="tel" placeholder="PhoneNumber" />
      </div>

      <!-- 버튼 -->
      <button @click.prevent="onSubmit" class="login-btn">아이디 찾기</button>

      <!-- 링크 -->
      <div class="footer-links">
        <a @click.prevent="emit('goFindId')" href="#">아이디(이메일)찾기</a>
        <a @click.prevent="emit('goFindPassword')" href="#">비밀번호 찾기</a>
        <a @click.prevent="emit('goSignup')" href="#">회원가입</a>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-wrapper {
  display: flex;
  min-height: 100vh;
  background-color: #fff;
  padding-top: 100px;
}
.login-container {
  width: 480px;
  padding: 40px 32px;
  background-color: white;
  border-radius: 12px;
  box-sizing: border-box;
}
.logo-section {
  text-align: center;
  margin-bottom: 28px;
}
.logo-img {
  width: 195px;
  height: 72px;
  margin: 0 auto 8px;
  display: block;
}
.login-title {
  font-size: 20px;
  color: #000000;
  font-weight: 600;
}
.form-group {
  margin-bottom: 20px;
}
.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 20px;
  font-weight: 500;
  color: #000000;
}
.form-group input {
  width: 417px;
  height: 60px;
  padding: 0 16px;
  border: 1px solid #ccc;
  border-radius: 6px;
  font-size: 16px;
  box-sizing: border-box;
  color: rgba(3, 3, 4, 0.54);
}
.login-btn {
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
  margin-bottom: 18px;
}
.login-btn:hover {
  background-color: #0277d8;
}
.footer-links {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
}
.footer-links a {
  color: rgba(3, 3, 4, 0.54);
  text-decoration: underline;
}
.footer-links a:hover {
  text-decoration: underline;
}
</style>
