<template>
  <div>
    <Header />

    <main class="content">
      <router-view />
      <!-- 여기에 각 페이지(HomePage, LoginPage 등)가 끼워진다 -->
    </main>

  </div>
</template>

<script setup>
import Header from '@/components/common/Header.vue'
import Footer from '@/components/common/Footer.vue'
import '@/assets/styles/styleguide.css'
</script>

<style scoped>
main {
  min-height: calc(100vh - 120px);
  padding: 20px;
}
.content {
  max-width: 1280px;
  margin: 0 auto;
  min-height: calc(100vh - 120px);
  padding: 20px;
  box-sizing: border-box;
  border-left: 0.5px solid var(--newbitdivider);
  border-right: 0.5px solid var(--newbitdivider);
}
</style>l