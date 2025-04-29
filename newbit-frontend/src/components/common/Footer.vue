<template>
  <footer class="footer">
    <div class="footer-inner">
      <div class="footer-top text-13px-regular">
        <span>저자 지원 | 고객센터</span>
        <div class="social-icons">
        </div>
      </div>

      <div class="footer-info text-13px-regular">
        (주) 뉴빗 | 대표이사: 스즈메 | 서울특별시 동작구 플레이데이터 3층 (08997) | 02-3424-1234 | 사업자 등록번호: 123-12-12345 |
        <br />
        통신판매업신고: 2025-서울동작-2938 | 이용약관 | 개인정보 처리방침 ⓒ NEWBIT Co., Ltd.
      </div>
    </div>
  </footer>
</template>

<script setup>
</script>

<style scoped>
.footer {
  background-color: #2e343b;
  color: #ccc;
  padding: 48px 32px;
}

.footer-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.footer-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.social-icons img {
  width: 20px;
  height: 20px;
  margin-left: 10px;
}

.footer-info {
  line-height: 1.6;
}
</style>