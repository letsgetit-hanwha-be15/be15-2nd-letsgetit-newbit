<template>
  <div>
    <Header />

    <main class="content">
      <router-view />
      <!-- 여기에 각 페이지(HomePage, LoginPage 등)가 끼워진다 -->
    </main>

    <Footer />
  </div>
</template>

<script setup>
import Header from '@/components/common/Header.vue'
import Footer from '@/components/common/Footer.vue'
import '@/assets/styles/styleguide.css'
</script>

<style scoped>
main {
  min-height: calc(100vh - 120px); /* 대략 헤더+푸터 뺀 영역 높이 설정 */
  padding: 20px;
}
.content {
  max-width: 1280px;  /* 너비 제한 */
  height: 1000px;
  margin: 0 auto;     /* 가운데 정렬 */
  min-height: calc(100vh - 120px); /* (헤더+푸터) 뺀 영역 높이 */
  padding: 20px;
  box-sizing: border-box; /* padding 포함해서 사이즈 계산 */
  border-left: 0.5px solid var(--newbitdivider);
  border-right: 0.5px solid var(--newbitdivider);
}
</style>l