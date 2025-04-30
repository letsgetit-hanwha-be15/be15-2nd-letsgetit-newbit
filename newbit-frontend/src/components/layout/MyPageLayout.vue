<script setup>
import Header from '@/components/common/Header.vue'
import Footer from '@/components/common/Footer.vue'
import MyPageSideBar from '@/features/mypage/components/MyPageSideBar.vue'
</script>

<template>
  <div class="layout-wrapper">
    <Header />

    <div class="layout-body">
      <main class="content">
        <MyPageSideBar />
        <router-view />
      </main>
    </div>

    <Footer />
  </div>
</template>

<style scoped>
.layout-wrapper {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.layout-body {
  flex: 1;
  display: flex;
}

.content {
  display: flex;
  width: 1280px;
  max-width: 1280px;
  margin: 0 auto;
  padding: 20px;
  box-sizing: border-box;
  border-left: 0.5px solid var(--newbitdivider);
  border-right: 0.5px solid var(--newbitdivider);
  min-height: calc(100vh - 120px); /* header + footer 높이 */
}
</style>