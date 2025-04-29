<template>
  <header class="header">
    <div class="left">
      <router-link to="/" class="logo">
        <img src="@/assets/image/logo.png" alt="Newbit Logo" />
      </router-link>
      <nav class="nav text-13px-regular">
        <router-link to="/posts" class="nav-link" active-class="active">게시판</router-link>
        <router-link to="/columns" class="nav-link" active-class="active">칼럼</router-link>
        <router-link to="/coffeechats" class="nav-link" active-class="active">커피챗</router-link>
        <router-link to="/psychology-tests" class="nav-link" active-class="active">심리 테스트</router-link>
        <div class="divider" v-if="isAdmin"></div>
        <router-link v-if="isAdmin" to="/admin">Admin</router-link>
      </nav>
    </div>

    <div class="right">
      <router-link class="shop-button" to="/shop">
        <img src="@/assets/image/diamond-icon.png" alt="Diamond" />
        <span class="text-13px-regular">상점</span>
      </router-link>

      <button class="icon-button">
        <img class="chat-icon" src="@/assets/image/chat-icon.png" alt="Chat" />
      </button>

      <button class="icon-button">
        <img class="notification-icon" src="@/assets/image/notification-icon.png" alt="Notifications" />
      </button>

      <router-link to="/mypage">
        <button class="profile-button">
          <img src="@/assets/image/profile.png" alt="Profile" />
        </button>
      </router-link>


    </div>
  </header>
</template>

<script setup>
// 별도 스크립트 필요 없음
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  height: 70px;
  padding: 0 120px;
  background-color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.left {
  display: flex;
  align-items: center;
}

.logo img {
  height: 40px;
}

.nav {
  display: flex;
  align-items: center;
  margin-left: 30px;
}

.nav a {
  margin-right: 20px;
  color: black;
  text-decoration: none;
}

.nav a:hover {
  color: var(--newbitnormal-hover); /* 파란색 hover */
}

.router-link-exact-active.nav-link {
  color: var(--newbitnormal-active);
  font-weight: 500;
}

.divider {
  width: 1px;
  height: 16px;
  background-color: var(--newbitdivider);
  margin: 0 20px;
}

.right {
  display: flex;
  align-items: center;
  justify-content: center;
}

.shop-button {
  display: inline-flex;
  align-items: center;
  gap: 4px; /* 아이콘과 텍스트 사이 간격 */
  padding: 10px 12px;
  margin-right: 20px;
  border: 1px solid var(--newbitdivider); /* 네가 정의한 색상 변수 */
  border-radius: 8px;
  background-color: white;
  text-decoration: none; /* a태그 밑줄 없애기 */
  color: black; /* 글자색 검정 */
  font-size: 14px;
  font-weight: 500;

}

.shop-button img {
  width: 18px;
  height: 18px;
  margin-right: 5px;
  vertical-align: middle;
}

.shop-button span {
  position: relative;
  top: 1px; /* 아주 살짝 내리기 */
}


.icon-button {
  background: none;
  border: none;
  margin-right: 12px;
  padding: 0 5px;
}

.notification-icon {
  width: 24px;
  height: 24px;
}

.chat-icon {
  width: 20px;
  height: 20px;
}

.profile-button {
  width: 40px;      /* 원하는 사이즈 */
  height: 40px;
  object-fit: cover; /* 사진 비율 깨지지 않게 */
  border-radius: 50%; /* 완전 동그랗게! */
  border: none;     /* 테두리 제거 */
  background: none; /* 배경색 제거 */
}

.profile-button img {
  width: 36px;
  height: 36px;
  border-radius: 50%;
}
</style>