<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from "vue";
import { useRouter } from "vue-router";
import { fetchChatRoomsByUser } from "@/api/coffeeletter";
import webSocketService from "@/features/coffeeletter/services/websocket";
import ChatRoomListDropdown from "./ChatRoomListDropdown.vue";

const props = defineProps({
  onSelectRoom: {
    type: Function,
    required: false,
  },
  selectedRoomId: {
    type: String,
    default: null,
  },
});

const emit = defineEmits(["select-room"]);
const router = useRouter();

// TODO: 사용자 정보 auth 적용 후 수정
const currentUserId = ref(3);
const isMentor = ref(true);

const rooms = ref([]);
const searchQuery = ref("");
const loading = ref(false);

const filteredRooms = computed(() => {
  if (!searchQuery.value.trim()) return rooms.value;

  return rooms.value.filter((room) => {
    const partnerName = isCurrentUserMentor(room)
      ? room.menteeName
      : room.mentorName;
    return (partnerName ?? "")
      .toLowerCase()
      .includes(searchQuery.value.toLowerCase());
  });
});

const selectRoom = (roomId) => {
  emit("select-room", roomId);
};

const fetchRooms = async () => {
  loading.value = true;
  try {
    const response = await fetchChatRoomsByUser(currentUserId.value);
    rooms.value = response.data;

    rooms.value.sort((a, b) => {
      const timeA = a.lastMessageTime
        ? new Date(a.lastMessageTime).getTime()
        : 0;
      const timeB = b.lastMessageTime
        ? new Date(b.lastMessageTime).getTime()
        : 0;
      return timeB - timeA;
    });
  } catch (error) {
    console.error("채팅방 목록 조회 실패:", error);
    rooms.value = [];
  } finally {
    loading.value = false;
  }
};

const getPartnerName = (room) => {
  return isCurrentUserMentor(room) ? room.menteeName : room.mentorName;
};

const isCurrentUserMentor = (room) => {
  return room.mentorId === currentUserId.value;
};

const getUnreadCount = (room) => {
  return isCurrentUserMentor(room)
    ? room.unreadCountMentor
    : room.unreadCountMentee;
};

const formatDate = (dateStr) => {
  if (!dateStr) return "";

  const date = new Date(dateStr);
  const now = new Date();
  const diffMs = now - date;
  const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));
  const diffHours = Math.floor(diffMs / (1000 * 60 * 60));
  const diffMinutes = Math.floor(diffMs / (1000 * 60));

  if (diffMinutes < 1) {
    return "방금 전";
  } else if (diffMinutes < 60) {
    return `${diffMinutes}분 전`;
  } else if (diffHours < 24) {
    return `${diffHours}시간 전`;
  } else if (diffDays === 0) {
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

const getProfileImage = (room) => {
  const defaultImage = "/src/assets/image/profile.png";
  if (isCurrentUserMentor(room)) {
    return room.menteeProfileImageUrl || defaultImage;
  } else {
    return room.mentorProfileImageUrl || defaultImage;
  }
};

const setupWebSocket = () => {
  webSocketService.connect({
    userId: currentUserId.value,
    onRoomListUpdate: (updatedRoom) => {
      console.log("채팅방 목록 업데이트:", updatedRoom);
      const index = rooms.value.findIndex((room) => room.id === updatedRoom.id);
      if (index !== -1) {
        rooms.value[index] = updatedRoom;
      } else {
        rooms.value.unshift(updatedRoom);
      }
      rooms.value.sort((a, b) => {
        const timeA = a.lastMessageTime
          ? new Date(a.lastMessageTime).getTime()
          : 0;
        const timeB = b.lastMessageTime
          ? new Date(b.lastMessageTime).getTime()
          : 0;
        return timeB - timeA;
      });
    },
    onUserEvent: (event) => {
      console.log("사용자 이벤트 수신:", event);
      if (event.type === "READ_UPDATE" || event.type === "NEW_MESSAGE") {
        fetchRooms();
      }
    },
    onConnected: () => {
      console.log("채팅방 목록 WebSocket 연결됨");
    },
    onError: (error) => {
      console.error("WebSocket 오류:", error);
    },
  });
};

watch(
  () => props.selectedRoomId,
  (newValue, oldValue) => {
    console.log(
      "ChatRoomList에서 selectedRoomId 변경 감지:",
      oldValue,
      "->",
      newValue
    );

    if (newValue) {
      const selectedRoom = rooms.value.find((room) => room.id === newValue);
      if (selectedRoom) {
        if (isCurrentUserMentor(selectedRoom)) {
          selectedRoom.unreadCountMentor = 0;
        } else {
          selectedRoom.unreadCountMentee = 0;
        }
      }
    }
  }
);

onMounted(() => {
  fetchRooms();
  setupWebSocket();
  console.log(
    "ChatRoomList 마운트됨, 초기 selectedRoomId:",
    props.selectedRoomId
  );
});

onUnmounted(() => {
  if (webSocketService.isConnected()) {
    webSocketService.unsubscribe("/topic/rooms");
    webSocketService.unsubscribe(`/user/${currentUserId.value}/queue/events`);
  }
});
</script>

<template>
  <div class="chatroom-list-container">
    <div class="chatroom-header">
      <h2 class="title">커피레터 채팅</h2>
    </div>
    <div class="chatroom-search">
      <input
        type="text"
        v-model="searchQuery"
        placeholder="채팅방 검색"
        class="search-input"
      />
    </div>
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
    </div>
    <div v-else-if="rooms.length === 0" class="no-rooms">
      <p>진행 중인 커피레터가 없습니다.</p>
      <p class="no-rooms-sub">새로운 대화를 시작해보세요!</p>
    </div>
    <div v-else class="chatroom-list">
      <div
        v-for="room in filteredRooms"
        :key="room.id"
        class="chatroom-item"
        :class="{ active: selectedRoomId === room.id }"
        @click="selectRoom(room.id)"
      >
        <div class="profile-image">
          <img
            :src="getProfileImage(room)"
            :alt="getPartnerName(room)"
            class="profile-img"
          />
          <div v-if="getUnreadCount(room) > 0" class="unread-badge">
            {{ getUnreadCount(room) > 99 ? "99+" : getUnreadCount(room) }}
          </div>
        </div>
        <div class="room-info">
          <div class="room-top">
            <span class="nickname">{{ getPartnerName(room) }}</span>
            <span class="time">{{ formatDate(room.lastMessageTime) }}</span>
          </div>
          <div class="room-bottom">
            <p
              class="last-message"
              :class="{ unread: getUnreadCount(room) > 0 }"
            >
              {{ room.lastMessageContent || "새로운 채팅방이 개설되었습니다." }}
            </p>
            <div
              v-if="room.status !== 'ACTIVE'"
              class="status-badge"
              :class="{
                inactive: room.status === 'INACTIVE',
                cancelled: room.status === 'CANCELED',
              }"
            >
              {{ room.status === "INACTIVE" ? "종료됨" : "취소됨" }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.chatroom-list-container {
  width: 320px;
  height: 100%;
  border-right: 1px solid var(--newbitdivider, #ececec);
  background-color: white;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  flex-shrink: 0;
}

.chatroom-header {
  padding: 24px 20px 12px;
  border-bottom: 1px solid var(--newbitdivider, #ececec);
  height: 70px;
  box-sizing: border-box;
}

.title {
  font-size: 20px;
  font-weight: 700;
  color: #333;
  margin: 0;
}

.chatroom-search {
  padding: 12px 16px;
  border-bottom: 1px solid var(--newbitdivider, #ececec);
}

.search-input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid var(--newbitdivider, #ececec);
  border-radius: 8px;
  font-size: 14px;
  outline: none;
}

.search-input:focus {
  border-color: var(--newbitnormal, #3b82f6);
}

.chatroom-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px 0;
}

.chatroom-item {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  cursor: pointer;
  transition: background-color 0.2s, border-left 0.2s;
  border-bottom: 1px solid var(--newbitdivider, #f8f8f8);
  border-left: 0px solid var(--newbitnormal, #3b82f6);
}

.chatroom-item:hover {
  background-color: #f7fafd;
}

.chatroom-item.active {
  background-color: #e9f2fe;
  border-left: 3px solid var(--newbitnormal, #3b82f6);
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.profile-image {
  position: relative;
  margin-right: 16px;
}

.profile-image img {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid #f0f0f0;
}

.unread-badge {
  position: absolute;
  top: -5px;
  right: -5px;
  background-color: #ff4757;
  color: white;
  font-size: 11px;
  min-width: 18px;
  height: 18px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 5px;
  font-weight: 600;
  box-shadow: 0 2px 4px rgba(255, 71, 87, 0.3);
}

.room-info {
  flex: 1;
  min-width: 0;
}

.room-top {
  display: flex;
  justify-content: space-between;
  margin-bottom: 4px;
}

.nickname {
  font-weight: 600;
  font-size: 15px;
  color: #333;
}

.time {
  font-size: 12px;
  color: #999;
}

.room-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.last-message {
  font-size: 13px;
  color: #666;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin: 0;
  max-width: 75%;
}

.last-message.unread {
  font-weight: 600;
  color: #333;
}

.status-badge {
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 4px;
  color: white;
  margin-left: 8px;
}

.status-badge.inactive {
  background-color: #9ca3af;
}

.status-badge.cancelled {
  background-color: #ef4444;
}

.no-rooms {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 200px;
  color: #999;
  text-align: center;
}

.no-rooms p {
  margin: 0;
  padding: 4px 0;
}

.no-rooms-sub {
  font-size: 13px;
  margin-top: 8px;
  color: #999;
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
</style>
