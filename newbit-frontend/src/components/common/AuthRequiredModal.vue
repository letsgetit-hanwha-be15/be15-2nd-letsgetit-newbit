<template>
  <div class="modal-backdrop">
    <div class="modal-content">
      <div class="modal-header">
        <span class="modal-title">로그인이 필요한 서비스입니다</span>
      </div>
      <div class="modal-body">
        <p>
          이 서비스는 로그인이 필요합니다.<br />로그인 페이지로
          이동하시겠습니까?
        </p>
      </div>
      <div class="modal-footer">
        <button class="modal-btn" @click="$emit('close')">취소</button>
        <button class="modal-btn main" @click="$emit('login')">로그인</button>
      </div>
    </div>
  </div>
</template>

<script setup></script>

<style scoped>
.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.35);
  z-index: 2000;
  display: flex;
  align-items: center;
  justify-content: center;
}
.modal-content {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.18);
  padding: 32px 28px 24px 28px;
  min-width: 320px;
  max-width: 90vw;
  text-align: center;
}
.modal-header {
  margin-bottom: 16px;
}
.modal-title {
  font-size: 20px;
  font-weight: 700;
  color: #222;
}
.modal-body {
  font-size: 16px;
  color: #444;
  margin-bottom: 24px;
}
.modal-footer {
  display: flex;
  gap: 12px;
  justify-content: center;
}
.modal-btn {
  padding: 10px 24px;
  border-radius: 6px;
  border: none;
  font-size: 16px;
  font-weight: 600;
  background: #eee;
  color: #333;
  cursor: pointer;
  transition: background 0.2s;
}
.modal-btn.main {
  background: var(--newbitnormal, #3b82f6);
  color: #fff;
}
.modal-btn:hover {
  background: #e0e7ef;
}
</style>
