<script setup>
import Header from '@/components/common/Header.vue'
import MyPageSideBar from '@/features/mypage/components/MyPageSideBar.vue'
</script>

<template>
  <div class="flex flex-col min-h-screen">
    <Header />

    <div class="flex flex-1">
      <main class="flex w-[1280px] mx-auto min-h-[calc(100vh-120px)] border-x border-gray-200 p-8 gap-10">
        <MyPageSideBar />
        <router-view />
      </main>
    </div>
  </div>
</template>