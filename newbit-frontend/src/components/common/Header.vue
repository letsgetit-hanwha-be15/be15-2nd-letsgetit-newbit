<script setup>
import { ref, provide, computed, watch } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/features/stores/auth.js";
import { logoutUser } from "@/api/user.js";
import ProfileDropdown from "@/components/common/ProfileDropdown.vue";
import ChatRoomListModal from "@/features/coffeeletter/components/ChatRoomListDropdown.vue";
import NotificationDropdown from "@/features/notification/components/NotificationDropdown.vue";
import { useChatStore } from "@/features/coffeeletter/stores/chatStore";

const showChatModal = ref(false);
const showNotification = ref(false);
const activeDropdown = ref(null);
const router = useRouter();
const authStore = useAuthStore();
const chatStore = useChatStore();
const isAdmin = authStore.userRole === "ADMIN";

provide("activeDropdown", activeDropdown);

// 읽지 않은 메시지 카운트 계산
const unreadMessagesCount = computed(() => {
  if (!authStore.isAuthenticated || !chatStore.rooms.length) return 0;

  // 최적화: rooms 배열 순회를 한 번만 수행
  return chatStore.rooms.reduce((total, room) => {
    const isUserMentor = room.mentorId === parseInt(authStore.userId);
    const unreadCount = isUserMentor
      ? room.unreadCountMentor || 0
      : room.unreadCountMentee || 0;
    return total + unreadCount;
  }, 0);
});

// 로그 추가: 읽지 않은 메시지 수가 변경될 때마다 로그 출력
watch(unreadMessagesCount, (newValue, oldValue) => {
  if (newValue !== oldValue) {
    console.log(
      `[Header] 읽지 않은 메시지 수 변경: ${oldValue} -> ${newValue}`
    );
  }
});

const handleDropdownOpened = (id) => {
  if (id === "profile" && showChatModal.value) {
    showChatModal.value = false;
    showNotification.value = false;
  }
  activeDropdown.value = id;
};

const toggleChatModal = () => {
  if (
    activeDropdown.value === "profile" ||
    activeDropdown.value === "notification"
  ) {
    activeDropdown.value = null;
  }
  showChatModal.value = !showChatModal.value;
  activeDropdown.value = showChatModal.value ? "chat" : null;

  // 채팅 모달 열 때 채팅방 목록 강제 갱신 및 읽지 않은 메시지 체크
  if (showChatModal.value && authStore.isAuthenticated) {
    chatStore.fetchRooms(true); // force refresh
  }
};

const toggleNotification = () => {
  if (activeDropdown.value === "profile" || activeDropdown.value === "chat") {
    activeDropdown.value = null;
  }
  showNotification.value = !showNotification.value;
  activeDropdown.value = showNotification.value ? "notification" : null;
};

const goMypage = () => router.push("/mypage");

const handleLogout = async () => {
  try {
    await logoutUser();
    authStore.clearAuth();
    router.push({ name: "Login" });
    // window.location.reload(); // ✅ 강제 새로고침으로 UI 반영
  } catch (e) {
    console.error("로그아웃 실패", e);
  }
};
</script>

<template>
  <header class="header">
    <div class="left">
      <router-link to="/" class="logo">
        <img src="@/assets/image/logo.png" alt="Newbit Logo" />
      </router-link>
      <nav class="nav text-13px-regular">
        <router-link to="/posts" class="nav-link" active-class="active"
          >게시판</router-link
        >
        <router-link to="/columns" class="nav-link" active-class="active"
          >칼럼</router-link
        >
        <router-link to="/mentors" class="nav-link" active-class="active"
          >커피챗</router-link
        >
        <router-link to="/perks" class="nav-link" active-class="active"
          >심리 테스트</router-link
        >
        <div class="divider" v-if="isAdmin"></div>
        <router-link
          v-if="isAdmin"
          to="/admin"
          class="nav-link"
          active-class="active"
          >Admin</router-link
        >
      </nav>
    </div>

    <div class="right">
      <router-link class="shop-button" to="/products">
        <img src="@/assets/image/diamond-icon.png" alt="Diamond" />
        <span class="text-13px-regular">상점</span>
      </router-link>

      <template v-if="authStore.isAuthenticated">
        <div class="chatroom-dropdown-wrapper">
          <button class="icon-button relative" @click="toggleChatModal">
            <img
              class="chat-icon"
              src="@/assets/image/chat-icon.png"
              alt="Chat"
            />
            <span
              v-if="unreadMessagesCount > 0"
              class="notification-dot"
            ></span>
          </button>
          <ChatRoomListModal
            v-if="showChatModal"
            :open="showChatModal"
            @close="showChatModal = false"
            :dropdown-id="'chat'"
          />
        </div>

        <button class="icon-button relative" @click="toggleNotification">
          <img
            class="notification-icon"
            src="@/assets/image/notification-icon.png"
            alt="Notifications"
          />
          <NotificationDropdown
            v-if="showNotification"
            :open="showNotification"
            @close="showNotification = false"
            :dropdown-id="'notification'"
          />
        </button>

        <ProfileDropdown
          class="mr-3"
          :dropdown-id="'profile'"
          @dropdown-opened="handleDropdownOpened"
          @logout="handleLogout"
        />
      </template>

      <template v-else>
        <router-link class="nav-button" to="/login">LOGIN</router-link>
      </template>
    </div>
  </header>
</template>

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
  position: relative;
  z-index: 10;
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
  color: var(--newbitnormal-hover);
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
  gap: 4px;
  padding: 10px 12px;
  margin-right: 20px;
  border: 1px solid var(--newbitdivider);
  border-radius: 8px;
  background-color: white;
  text-decoration: none;
  color: black;
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
  top: 1px;
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
  position: relative;
  top: 3px;
}

.profile-button {
  width: 40px;
  height: 40px;
  object-fit: cover;
  border-radius: 50%;
  border: none;
  background: none;
  cursor: pointer;
}

.profile-button img {
  width: 36px;
  height: 36px;
  border-radius: 50%;
}

/* ✅ 변경된 nav-button 스타일 */
.nav-button {
  background-color: var(--newbitnormal);
  color: white;
  border: none;
  padding: 8px 14px;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
}

.nav-button:hover {
  background-color: var(--newbitnormal-hover);
}

/* 뱃지 스타일 */
.relative {
  position: relative;
}

.notification-dot {
  position: absolute;
  top: -2px;
  right: -2px;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #ef4444;
  box-shadow: 0 0 0 2px white;
}
</style>
