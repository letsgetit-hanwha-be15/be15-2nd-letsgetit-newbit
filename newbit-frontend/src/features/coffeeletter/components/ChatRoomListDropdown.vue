<template>
  <div v-if="open" class="chatroom-list-modal" ref="modalRef">
    <div class="modal-content">
      <div class="header text-16px-bold">커피레터</div>
      <div class="chatroom-list">
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
              <span class="nickname text-13px-bold">{{ room.nickname }}</span>
              <span class="date text-10px-regular">{{ room.date }}</span>
            </div>
            <div class="last-message text-13px-regular">
              {{ room.lastMessage }}
            </div>
          </div>
        </div>
      </div>
      <div class="view-all-container">
        <button class="view-all-button" @click="viewAllChatrooms">
          <span>모든 채팅방 보기</span>
          <svg
            width="16"
            height="16"
            viewBox="0 0 16 16"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path
              d="M6 12L10 8L6 4"
              stroke="currentColor"
              stroke-width="1.5"
              stroke-linecap="round"
              stroke-linejoin="round"
            />
          </svg>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, inject, watch, computed } from "vue";
import { useRouter } from "vue-router";

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

// activeDropdown이 변경될 때마다 현재 드롭다운 상태 확인
watch(activeDropdown, (newValue) => {
  if (newValue !== props.dropdownId && props.open) {
    close();
  }
});

const chatRooms = ref([
  {
    id: 1,
    nickname: "추격자",
    date: "2025-04-24 18:39",
    lastMessage: "안녕하세요! 오늘 날씨가 참 아릅답습니다..",
  },
  {
    id: 2,
    nickname: "스즈메",
    date: "2025-04-23 09:39",
    lastMessage: "라라라라더라랄러아아...",
  },
  {
    id: 3,
    nickname: "양세바리",
    date: "2025-04-20 08:00",
    lastMessage: "들어올 땐 맘대로였겠지만 나갈 땐 꼭 나가야 해...",
  },
  {
    id: 4,
    nickname: "토끼공듀",
    date: "2025-04-19 10:00",
    lastMessage: "ㅎㅎㅎㅎ 망겜이 다 그렇죠 뭐 ...",
  },
  {
    id: 5,
    nickname: "바다라라",
    date: "2025-04-18 16:25",
    lastMessage: "모든 것이 꿈이었길 바래...",
  },
  {
    id: 6,
    nickname: "개발왕",
    date: "2025-04-17 11:11",
    lastMessage: "이 코드는 리팩토링이 필요해요. 다음 주에 같이 봐요!",
  },
]);

// 최대 5개만 표시
const displayedRooms = computed(() => {
  return chatRooms.value.slice(0, 5);
});

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
});

onBeforeUnmount(() => {
  window.removeEventListener("click", handleClickOutside);
});

const selectRoom = (room) => {
  // TODO: 채팅방 진입 로직
  close();
};

const viewAllChatrooms = () => {
  router.push("/coffeeletters/chats");
  close();
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
  color: #666;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 180px;
  line-height: 1.4;
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
</style>
