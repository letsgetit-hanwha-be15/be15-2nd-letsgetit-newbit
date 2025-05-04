<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from "vue";
import axios from "axios";

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

// TODO: 사용자 정보 auth 적용 후 수정
const currentUserId = ref(1);
const isMentor = ref(true);

const chatRooms = ref([]);
const searchKeyword = ref("");
const loading = ref(false);

const filteredRooms = computed(() => {
  if (!searchKeyword.value.trim()) return chatRooms.value;

  return chatRooms.value.filter((room) => {
    const partnerName = isCurrentUserMentor(room)
      ? room.menteeName
      : room.mentorName;
    return partnerName
      .toLowerCase()
      .includes(searchKeyword.value.toLowerCase());
  });
});

const selectRoom = (roomId) => {
  console.log("채팅방 리스트에서 선택됨:", roomId);
  emit("select-room", roomId);
};

// 채팅방 목록 조회
const fetchChatRooms = async () => {
  loading.value = true;
  try {
    // TODO: 실제 API 구현 시 아래 코드 사용
    // const response = await axios.get(`/coffeeletter/rooms/user/${currentUserId.value}`);
    // chatRooms.value = response.data;

    // 테스트용 더미 데이터
    chatRooms.value = [
      {
        id: "1",
        mentorId: 1,
        mentorName: "멘토A",
        menteeId: 2,
        menteeName: "멘티B",
        lastMessageContent: "안녕하세요! 커피챗 질문이 있어요.",
        lastMessageTime: "2025-05-01T14:30:00",
        status: "ACTIVE",
        unreadCountMentor: 0,
        unreadCountMentee: 2,
      },
      {
        id: "2",
        mentorId: 1,
        mentorName: "멘토A",
        menteeId: 3,
        menteeName: "멘티C",
        lastMessageContent: "답변 감사합니다! 정말 도움이 많이 됐어요.",
        lastMessageTime: "2025-04-30T09:15:00",
        status: "ACTIVE",
        unreadCountMentor: 1,
        unreadCountMentee: 0,
      },
      {
        id: "3",
        mentorId: 4,
        mentorName: "멘토D",
        menteeId: 1,
        menteeName: "멘티A",
        lastMessageContent: "다음 주에 일정 조율 가능할까요?",
        lastMessageTime: "2025-04-29T18:20:00",
        status: "ACTIVE",
        unreadCountMentor: 0,
        unreadCountMentee: 0,
      },
      {
        id: "4",
        mentorId: 1,
        mentorName: "멘토A",
        menteeId: 5,
        menteeName: "멘티E",
        lastMessageContent: "프로젝트 리뷰 부탁드려요!",
        lastMessageTime: "2025-04-28T10:45:00",
        status: "ACTIVE",
        unreadCountMentor: 3,
        unreadCountMentee: 0,
      },
      {
        id: "5",
        mentorId: 6,
        mentorName: "멘토F",
        menteeId: 1,
        menteeName: "멘티A",
        lastMessageContent: "온라인 미팅 링크 보내드렸습니다!",
        lastMessageTime: "2025-04-27T16:30:00",
        status: "ACTIVE",
        unreadCountMentor: 0,
        unreadCountMentee: 1,
      },
      {
        id: "6",
        mentorId: 1,
        mentorName: "멘토A",
        menteeId: 7,
        menteeName: "멘티G",
        lastMessageContent: "포트폴리오 피드백 감사합니다!",
        lastMessageTime: "2025-04-26T09:45:00",
        status: "ACTIVE",
        unreadCountMentor: 0,
        unreadCountMentee: 0,
      },
    ];
  } catch (error) {
    console.error("채팅방 목록 조회 실패:", error);
    chatRooms.value = [];
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

watch(
  () => props.selectedRoomId,
  (newValue, oldValue) => {
    console.log(
      "ChatRoomList에서 selectedRoomId 변경 감지:",
      oldValue,
      "->",
      newValue
    );
  }
);

onMounted(() => {
  fetchChatRooms();
  console.log(
    "ChatRoomList 마운트됨, 초기 selectedRoomId:",
    props.selectedRoomId
  );
});

// TODO: 채팅방 목록 웹소켓 설정 추가한 후 삭제 예정
let refreshInterval;
onMounted(() => {
  refreshInterval = setInterval(() => {
    fetchChatRooms();
  }, 5 * 60 * 1000);
});

onUnmounted(() => {
  if (refreshInterval) {
    clearInterval(refreshInterval);
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
        v-model="searchKeyword"
        placeholder="채팅방 검색"
        class="search-input"
      />
    </div>
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
    </div>
    <div v-else-if="chatRooms.length === 0" class="no-rooms">
      <p>진행 중인 커피레터가 없습니다.</p>
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
          <img src="@/assets/image/profile.png" alt="프로필" />
          <div v-if="getUnreadCount(room) > 0" class="unread-badge">
            {{ getUnreadCount(room) }}
          </div>
        </div>
        <div class="room-info">
          <div class="room-top">
            <span class="nickname">{{ getPartnerName(room) }}</span>
            <span class="time">{{ formatDate(room.lastMessageTime) }}</span>
          </div>
          <div class="room-bottom">
            <p class="last-message">
              {{ room.lastMessageContent || "새로운 채팅방이 개설되었습니다." }}
            </p>
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

.last-message {
  font-size: 13px;
  color: #666;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin: 0;
}

.no-rooms {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
  color: #999;
  font-size: 14px;
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
