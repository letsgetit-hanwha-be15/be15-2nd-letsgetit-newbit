<template>
  <div class="dropdown-wrapper" @click.stop="toggleDropdown">
    <img src="@/assets/image/profile.png" alt="Profile" class="profile-button" />
    <div v-if="showDropdown" class="dropdown">
      <!-- TODO : authStore, 로그인 기능 추가 시 role에 따라서 조건 표시, 닉네임 가져오기 -->
      <div class="nickname">
        <span class="name">레츠기릿</span>
        <span class="role text-13px-bold">멘토</span>
      </div>

      <button class="edit-profile" @click="goTo('/mypage/profile/edit')">프로필 수정</button>

      <hr class="divider" />

      <div class="asset-row">
        <div class="asset" @click="goTo('/mypage/history/point')">
          <div class="asset-wrapper">
            <img src="@/assets/image/profile.png" class="icon" />
            <!-- TODO : Pinia store에서 가져오는 방식으로 수정 -->
            <span>1230</span>
          </div>
          <img src="@/assets/image/arrow-icon.png" class="arrow-icon" />
        </div>
        <div class="asset" @click="goTo('/mypage/history/diamond')">
          <div class="asset-wrapper">
            <img src="@/assets/image/diamond-icon.png" class="icon" />
            <!-- TODO : Pinia store에서 가져오는 방식으로 수정 -->
            <span>200</span>
          </div>
          <img src="@/assets/image/arrow-icon.png" class="arrow-icon" />
        </div>
      </div>

      <hr class="divider" />

      <ul class="menu-list">
        <li @click="goTo('/mypage/contents/posts')">내 콘텐츠</li>
        <li @click="goTo('/mypage/history/coffeechat')">커피챗</li>
        <li @click="goTo('/mypage/history/point')">활동 내역</li>
        <!-- TODO : authStore, 로그인 기능 추가 시 role에 따라서 조건 표시 -->
        <li @click="goTo('/mypage/mentor/series')">멘토 활동 관리</li>
        <li @click="goTo('/mypage/account')">설정</li>
        <!-- TODO : 로그아웃 로직 추가 -->
        <li class="logout" @click="logout">로그아웃</li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const showDropdown = ref(false)

const toggleDropdown = () => {
  showDropdown.value = !showDropdown.value
}

const closeDropdown = () => {
  showDropdown.value = false
}

const goTo = (path) => {
  router.push(path)
  closeDropdown()
}

const logout = () => {
  closeDropdown()
  router.push('/')
}

function handleClickOutside(event) {
  if (!event.target.closest('.dropdown-wrapper')) {
    closeDropdown()
  }
}

onMounted(() => {
  window.addEventListener('click', handleClickOutside)
})

onBeforeUnmount(() => {
  window.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.profile-button {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: none;
  background: none;
  cursor: pointer;
}

.dropdown {
  position: absolute;
  top: 60px;
  right: 40px;
  width: 260px;
  background: var(--newbitbackground);
  border-radius: 4px;
  border: 1px solid var(--newbitdivider);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
  z-index: 1000;
  font-family: "Noto Sans KR", sans-serif;
}

.nickname {
  display: flex;
  align-items: baseline;
  gap: 6px;
  font-size: 16px;
  font-weight: 700;
  margin-bottom: 16px;
}

.nickname .role {
  color: var(--newbitnormal);
}

.edit-profile {
  width: 100%;
  padding: 10px;
  border: 1px solid var(--newbitdivider);
  border-radius: 10px;
  background: var(--newbitbackground);
  cursor: pointer;
  font-weight: 600;
  font-size: 14px;
  margin-bottom: 16px;
}

.asset-row {
  display: flex;
  margin-bottom: 16px;
  cursor: pointer;
}


.asset {
  display: flex;
  width: 100px;
  padding: 0 12px;
  align-items: center;
  justify-content: space-between;
  font-weight: 600;
  font-size: 15px;
}

.asset-wrapper {
  display: flex;
  align-items: center;
  gap: 6px;
}

.asset span {
  position: relative;
  top: 1px; /* 숫자를 아래로 1px 내림 */
}

.icon {
  width: 18px;
  height: 18px;
}

.arrow-icon {
  width: 8px;
  height: 8px;
}

.divider {
  border: none;
  border-top: 1px solid var(--newbitdivider);
  margin: 16px 0;
}

.menu-list {
  list-style: none;
  padding: 0;
}

.menu-list li {
  margin-bottom: 10px;
  padding: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 400;
}

.menu-list li:hover {
  background-color: var(--newbitlightgray);
}

.logout {
  color: red;
  font-weight: 600;
  cursor: pointer;
  text-align: left;
  font-size: 14px;
}
</style>
