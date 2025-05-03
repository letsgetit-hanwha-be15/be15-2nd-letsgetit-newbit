<script setup>
import { ref } from 'vue'

const emit = defineEmits(['submit', 'goFindId', 'goFindPassword', 'goSignup'])

const email = ref('')
const password = ref('')

const onSubmit = () => {
  if (!email.value || !password.value) {
    alert('아이디와 비밀번호를 입력해주세요.')
    return
  }

  emit('submit', {
    email: email.value,
    password: password.value
  })
}
</script>

<template>
  <div class="login-wrapper">
    <div class="login-container">
      <div class="logo-section">
        <img src="@/assets/image/logo.png" alt="Newbit Logo" class="logo-img" />
        <p class="login-title">로그인</p>
      </div>

      <div class="form-group">
        <label for="email">아이디(이메일)</label>
        <input id="email" v-model="email" type="email" placeholder="Email" />
      </div>

      <div class="form-group">
        <label for="password">비밀번호</label>
        <input id="password" v-model="password" type="password" placeholder="Password" />
      </div>

      <button @click.prevent="onSubmit" class="login-btn">로그인</button>

      <div class="footer-links">
        <a @click.prevent="emit('goFindId')" href="#">아이디(이메일)찾기</a>
        <a @click.prevent="emit('goFindPassword')" href="#">비밀번호 찾기</a>
        <a @click.prevent="emit('goSignup')" href="#">회원가입</a>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 스타일은 네가 마지막에 준 그대로 유지함 */
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
