<template>
  <div class="dropdown-wrapper" @click.stop="toggleDropdown">
    <img src="@/assets/image/profile.png" alt="Profile" class="profile-button" />
    <div v-if="showDropdown" class="dropdown">
      <div class="nickname">
        <span class="name">레츠기릿</span>
        <span class="role">멘토</span>
      </div>

      <button class="edit-profile" @click="goTo('/mypage/profile/edit')">프로필 수정</button>

      <hr class="divider" />

      <div class="asset-row">
        <div class="asset">
          <!--          <img src="/assets/image/coin.svg" class="icon" />1230-->
        </div>
        <div class="asset">
          <img src="@/assets/image/diamond-icon.png" class="icon" />200
        </div>
      </div>

      <hr class="divider" />

      <ul class="menu-list">
        <li @click="goTo('/mypage/contents?type=posts')">내 콘텐츠</li>
        <li @click="goTo('/mypage/history?type=coffeechat')">커피챗</li>
        <li @click="goTo('/mypage/history?type=point')">활동 내역</li>
        <li @click="goTo('/mypage/mentor/series')">멘토 활동 관리</li>
        <li @click="goTo('/mypage/account')">설정</li>
      </ul>

      <div class="logout" @click="logout">로그아웃</div>
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
  right: 0;
  width: 260px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
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
  font-size: 13px;
  font-weight: 500;
  color: var(--newbitgray);
}

.edit-profile {
  width: 100%;
  padding: 10px;
  border: 1px solid var(--newbitdivider);
  border-radius: 10px;
  background: white;
  cursor: pointer;
  font-weight: 600;
  font-size: 14px;
  margin-bottom: 16px;
}

.asset-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}

.asset {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
  font-size: 15px;
}

.icon {
  width: 18px;
  height: 18px;
}

.divider {
  border: none;
  border-top: 1px solid var(--newbitdivider);
  margin: 16px 0;
}

.menu-list {
  list-style: none;
  padding: 0;
  margin: 0 0 16px;
}

.menu-list li {
  margin-bottom: 10px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
}

.logout {
  color: red;
  font-weight: 600;
  cursor: pointer;
  text-align: left;
  font-size: 14px;
}
</style>
