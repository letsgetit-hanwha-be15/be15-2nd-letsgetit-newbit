<template>
  <div v-if="open" class="chatroom-list-modal" ref="modalRef">
    <div class="modal-content">
      <div class="header text-16px-bold">커피레터</div>
      <div v-if="loading" class="loading-container">
        <div class="loading-spinner"></div>
      </div>
      <div v-else-if="chatRooms.length === 0" class="no-rooms">
        <p>진행 중인 커피레터가 없습니다.</p>
      </div>
      <div v-else class="chatroom-list">
        <div
          v-for="room in displayedRooms"
          :key="room.id"
          class="chatroom-item"
          @click="selectRoom(room)"
        >
          <img
            src="@/assets/image/profile.png"
            alt="프로필"
            class="profile-img"
          />
          <div class="chat-info">
            <div class="top-row">
              <span class="nickname text-13px-bold">{{
                getPartnerName(room)
              }}</span>
              <span class="date text-10px-regular">{{
                formatDate(room.lastMessageTime)
              }}</span>
            </div>
            <div class="last-message text-13px-regular">
              {{ room.lastMessageContent || "새로운 채팅방이 생성되었습니다." }}
              <span v-if="getUnreadCount(room) > 0" class="unread-badge">{{
                getUnreadCount(room)
              }}</span>
            </div>
          </div>
        </div>
      </div>
      <div class="view-all-container">
        <button class="view-all-button" @click="viewAllChatrooms">
          <span>모든 채팅방 목록 ➡️</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, inject, watch, computed } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/features/stores/auth";
import { useChatStore } from "../stores/chatStore";
import { storeToRefs } from "pinia";

const props = defineProps({
  open: Boolean,
  dropdownId: {
    type: String,
    required: true,
  },
});

const emit = defineEmits(["close"]);
const modalRef = ref(null);
const activeDropdown = inject("activeDropdown", ref(null));
const router = useRouter();
const authStore = useAuthStore();
const chatStore = useChatStore();
const { rooms: chatRooms, isLoadingRooms: loading } = storeToRefs(chatStore);

watch(activeDropdown, (newValue) => {
  if (newValue !== props.dropdownId && props.open) {
    close();
  }
});

watch(
  () => authStore.accessToken,
  () => {
    if (props.open) {
      // chatStore.fetchRooms();
    }
  }
);

const displayedRooms = computed(() => {
  const sortedRooms = [...chatRooms.value].sort((a, b) => {
    const timeA = a.lastMessageTime ? new Date(a.lastMessageTime).getTime() : 0;
    const timeB = b.lastMessageTime ? new Date(b.lastMessageTime).getTime() : 0;
    return timeB - timeA;
  });
  return sortedRooms.slice(0, 5);
});

const isCurrentUserMentor = (room) => {
  return room.mentorId === authStore.parsedUserId;
};

const getPartnerName = (room) => {
  return isCurrentUserMentor(room) ? room.menteeName : room.mentorName;
};

const getUnreadCount = (room) => {
  return isCurrentUserMentor(room)
    ? room.unreadCountMentor
    : room.unreadCountMentee;
};

const close = () => {
  emit("close");
  if (activeDropdown.value === props.dropdownId) {
    activeDropdown.value = null;
  }
};

function handleClickOutside(event) {
  if (
    !event.target.closest(".chatroom-dropdown-wrapper") &&
    !event.target.closest(".dropdown-wrapper")
  ) {
    close();
  }
}

onMounted(() => {
  window.addEventListener("click", handleClickOutside);
  // chatStore.fetchRooms();
});

onBeforeUnmount(() => {
  window.removeEventListener("click", handleClickOutside);
});

watch(
  () => props.open,
  (newValue) => {
    // if (newValue) {
    //   chatStore.fetchRooms();
    // }
  }
);

const selectRoom = (room) => {
  router.push({
    path: "/coffeeletters/chats",
    query: { roomId: room.id },
  });
  close();
};

const viewAllChatrooms = () => {
  router.push("/coffeeletters/chats");
  close();
};

const formatDate = (dateStr) => {
  if (!dateStr) return "";

  const date = new Date(dateStr);
  const now = new Date();
  const diffDays = Math.floor((now - date) / (1000 * 60 * 60 * 24));

  if (diffDays === 0) {
    // 오늘
    return `${date.getHours().toString().padStart(2, "0")}:${date
      .getMinutes()
      .toString()
      .padStart(2, "0")}`;
  } else if (diffDays === 1) {
    // 어제
    return "어제";
  } else if (diffDays < 7) {
    // 이번 주
    const days = ["일", "월", "화", "수", "목", "금", "토"];
    return `${days[date.getDay()]}요일`;
  } else {
    // 그 이전
    return `${date.getMonth() + 1}/${date.getDate()}`;
  }
};
</script>

<style scoped>
@import "@/assets/styles/text-utilities.css";

.chatroom-list-modal {
  position: absolute;
  top: 60px;
  right: 40px;
  width: 280px;
  background: var(--newbitbackground, #fff);
  border-radius: 12px;
  border: 1px solid var(--newbitdivider, #ececec);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  display: flex;
  flex-direction: column;
  pointer-events: auto;
  font-family: "Noto Sans KR", sans-serif;
  overflow: hidden;
  animation: dropdown-fade-in 0.18s cubic-bezier(0.4, 0, 0.2, 1);
}

.modal-content {
  position: static;
  width: 100%;
  background: none;
  border: none;
  box-shadow: none;
  padding: 0;
  font-family: inherit;
  overflow: visible;
  animation: none;
  display: flex;
  flex-direction: column;
}

@keyframes dropdown-fade-in {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.header {
  padding: 16px 20px 12px;
  border-bottom: 1px solid var(--newbitdivider, #f2f2f2);
  background: var(--newbitbackground, #fff);
}

.chatroom-list {
  display: flex;
  flex-direction: column;
  max-height: 320px;
  overflow-y: auto;
}

.chatroom-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  cursor: pointer;
  transition: background 0.2s ease;
  background: none;
  min-height: 56px;
  position: relative;
}

.chatroom-item:hover {
  background-color: var(--newbitlightgray, #f7fafd);
}

.chatroom-item:after {
  display: none;
}

.profile-img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #f2f2f2;
  object-fit: cover;
  flex-shrink: 0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.chat-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-width: 0;
  gap: 4px;
}

.top-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.nickname {
  color: #333;
  font-weight: 600;
}

.date {
  color: #999;
  margin-left: 8px;
  flex-shrink: 0;
  font-size: 10px;
}

.last-message {
  position: relative;
  color: #666;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 180px;
  line-height: 1.4;
  padding-right: 24px;
}

.unread-badge {
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  background-color: #ff4757;
  color: white;
  font-size: 10px;
  min-width: 16px;
  height: 16px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 4px;
}

.view-all-container {
  padding: 12px 16px;
  border-top: 1px solid var(--newbitdivider, #f2f2f2);
  display: flex;
  justify-content: center;
}

.view-all-button {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  background: none;
  border: none;
  color: var(--newbitnormal, #3b82f6);
  font-size: 14px;
  font-weight: 500;
  padding: 6px 12px;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.view-all-button:hover {
  background-color: rgba(59, 130, 246, 0.08);
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100px;
}

.loading-spinner {
  width: 24px;
  height: 24px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid var(--newbitnormal, #3b82f6);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.no-rooms {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100px;
  color: #999;
  font-size: 14px;
}
</style>
