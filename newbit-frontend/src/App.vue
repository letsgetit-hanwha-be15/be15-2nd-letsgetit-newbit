<script setup>
import Footer from "@/components/common/Footer.vue";
</script>

<template>
  <div class="app-layout">
    <RouterView />
    <Footer />
  </div>
</template>

<style>
.app-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}
</style>
