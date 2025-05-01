<template>
  <div class="admin-layout">
    <AdminSideBar />
    <div class="admin-content">
      <router-view />
    </div>
  </div>
</template>

<script setup>
import AdminSideBar from "./AdminSideBar.vue";
</script>

<style scoped>
.admin-layout {
  display: flex;
  width: 100%;
  min-height: 100%;
  gap: 0px;
  background: none;
}
.admin-content {
  flex: 1;
  min-width: 0;
  background: #fff;
  border-radius: 15px;
  padding: 12px 12px;
  overflow-x: auto;
}
</style>
