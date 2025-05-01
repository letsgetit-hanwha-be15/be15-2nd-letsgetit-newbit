<template>
  <nav class="admin-sidebar">
    <div class="sidebar-section">
      <div class="sidebar-title">신고 관리</div>
      <router-link class="sidebar-item" to="/admin" exact-active-class="active"
        >신고 목록 조회</router-link
      >
      <router-link
        class="sidebar-item"
        to="/admin/false-reports"
        exact-active-class="active"
        >허위 신고 목록 조회</router-link
      >
    </div>
    <div class="sidebar-section">
      <div class="sidebar-title">상품 관리</div>
      <router-link
        class="sidebar-item"
        to="/admin/products"
        exact-active-class="active"
        >상품 목록 조회</router-link
      >
    </div>
    <div class="sidebar-section">
      <div class="sidebar-title">칼럼 관리</div>
      <router-link
        class="sidebar-item"
        to="/admin/columns"
        exact-active-class="active"
        >칼럼 승인 관리</router-link
      >
    </div>
    <div class="sidebar-section">
      <div class="sidebar-title">회원 관리</div>
      <router-link
        class="sidebar-item"
        to="/admin/users"
        exact-active-class="active"
        >회원 관리 페이지</router-link
      >
    </div>
  </nav>
</template>

<style scoped>
.admin-sidebar {
  background: #fff;
  border: 2px solid #b3d8fd;
  border-radius: 20px;
  padding: 32px 24px;
  width: 217px;
  min-height: 600px;
  color: #222;
  box-sizing: border-box;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  margin: 0;
}
.sidebar-section {
  margin-bottom: 32px;
}
.sidebar-title {
  color: #888;
  margin-bottom: 8px;
}
.sidebar-item {
  display: block;
  margin: 16px 0 0 16px;
  cursor: pointer;
  transition: color 0.2s;
  color: inherit;
  text-decoration: none;
}
.sidebar-item:hover {
  color: #1976d2;
}
.sidebar-item.active {
  color: #1976d2;
}
</style>
