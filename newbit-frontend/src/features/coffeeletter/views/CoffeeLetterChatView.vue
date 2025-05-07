<script setup>
import { ref, computed, onMounted, nextTick, watch } from "vue";
import { useRoute } from "vue-router";
import axios from "axios";
import {
  fetchRoomInfo as fetchRoomInfoApi,
  fetchMessagesByRoom,
  sendMessage as sendMessageApi,
  markAsRead as markAsReadApi,
} from "@/api/coffeeletter.js";
import { useAuthStore } from "@/features/stores/auth";
import { useChatStore } from "../stores/chatStore";

const DEFAULT_ROOM_ID = "67fca09d6632d00f31d416bc";

const route = useRoute();
const roomId = computed(() => route.params.id || DEFAULT_ROOM_ID);

const authStore = useAuthStore();
const chatStore = useChatStore();
const currentUserId = ref(null);
const isMentor = ref(false);

const parseUserInfo = () => {
  if (authStore.accessToken) {
    try {
      const payload = JSON.parse(atob(authStore.accessToken.split(".")[1]));
      currentUserId.value = parseInt(payload.userId);
      isMentor.value = payload.authority === "ROLE_MENTOR";
    } catch (e) {
      console.error("토큰 파싱 오류:", e);
    }
  }
};

parseUserInfo();

const messages = ref([]);
const newMessage = ref("");
const chatMessagesContainer = ref(null);
const roomInfo = ref({
  id: "",
  partnerName: "",
  status: "",
  mentorId: null,
  menteeId: null,
});

watch(
  () => authStore.accessToken,
  () => {
    parseUserInfo();
    fetchRoomInfo();
    fetchMessages();
  }
);

watch(
  () => currentUserId.value,
  () => {
    if (currentUserId.value) {
      fetchRoomInfo();
      fetchMessages();
    }
  }
);

const fetchRoomInfo = async () => {
  try {
    const response = await fetchRoomInfoApi(roomId.value);
    const room = response.data;

    roomInfo.value = {
      id: room.id,
      partnerName: isMentor.value ? room.menteeName : room.mentorName,
      status: room.status,
      mentorId: room.mentorId,
      menteeId: room.menteeId,
    };
  } catch (error) {
    console.error("채팅방 정보 조회 실패:", error);
  }
};

const fetchMessages = async () => {
  try {
    const response = await fetchMessagesByRoom(roomId.value);
    messages.value = response.data;
  } catch (error) {
    console.error("메시지 목록 조회 실패:", error);
  }
};

const sendMessage = async () => {
  if (!newMessage.value.trim() || !currentUserId.value) return;

  const messageData = {
    roomId: roomId.value,
    senderId: currentUserId.value,
    content: newMessage.value,
    type: "CHAT",
    timestamp: new Date().toISOString(),
    readByMentor: isMentor.value,
    readByMentee: !isMentor.value,
  };

  try {
    const response = await sendMessageApi(messageData);
    const newMessageObj = response.data;

    messages.value.push(newMessageObj);
    newMessage.value = "";

    await nextTick();
    scrollToBottom();
  } catch (error) {
    console.error("메시지 전송 실패:", error);
  }
};

const markAsRead = async () => {
  if (!currentUserId.value) return;

  try {
    await chatStore.markRoomAsRead(roomId.value);

    messages.value.forEach((msg) => {
      if (isMentor.value) {
        msg.readByMentor = true;
      } else {
        msg.readByMentee = true;
      }
    });
  } catch (error) {
    console.error("읽음 처리 실패:", error);
  }
};

const scrollToBottom = () => {
  if (chatMessagesContainer.value) {
    chatMessagesContainer.value.scrollTop =
      chatMessagesContainer.value.scrollHeight;
  }
};

const isMyMessage = (message) => {
  return message.senderId === currentUserId.value;
};

const formatTime = (dateStr) => {
  if (!dateStr) return "";

  const date = new Date(dateStr);
  return `${date.getHours().toString().padStart(2, "0")}:${date
    .getMinutes()
    .toString()
    .padStart(2, "0")}`;
};

onMounted(async () => {
  parseUserInfo(); // 사용자 정보 초기화

  if (currentUserId.value) {
    await fetchRoomInfo();
    await fetchMessages();
    markAsRead();
    nextTick(() => {
      scrollToBottom();
    });
  }
});

watch(messages, () => {
  nextTick(() => {
    scrollToBottom();
  });
});

watch(roomId, async () => {
  await fetchRoomInfo();
  await fetchMessages();
  markAsRead();
  nextTick(() => {
    scrollToBottom();
  });
});
</script>

<template>
  <div class="chat-view-container">
    <div class="chat-header">
      <div class="chat-header-left">
        <router-link to="/coffeeletters/chats" class="back-button">
          <svg
            width="24"
            height="24"
            viewBox="0 0 24 24"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path
              d="M19 12H5"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            />
            <path
              d="M12 19L5 12L12 5"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            />
          </svg>
        </router-link>
        <div class="profile-wrapper">
          <img
            src="@/assets/image/profile.png"
            alt="프로필"
            class="profile-img"
          />
        </div>
        <div class="chat-info">
          <span class="nickname">{{ roomInfo.partnerName }}</span>
          <span class="status">{{ roomInfo.status }}</span>
        </div>
      </div>
    </div>

    <div class="chat-messages" ref="chatMessagesContainer">
      <div
        v-for="message in messages"
        :key="message.id"
        class="message-wrapper"
      >
        <div
          class="message"
          :class="{
            'my-message': isMyMessage(message),
            'other-message': !isMyMessage(message),
          }"
        >
          <div class="message-content">{{ message.content }}</div>
          <div class="message-time">{{ formatTime(message.timestamp) }}</div>
        </div>
      </div>
      <div v-if="messages.length === 0" class="no-messages">
        <p>대화를 시작해보세요!</p>
      </div>
    </div>

    <div class="chat-input-container">
      <input
        type="text"
        v-model="newMessage"
        placeholder="메시지를 입력하세요"
        class="chat-input"
        @keyup.enter="sendMessage"
      />
      <button @click="sendMessage" class="send-button">
        <svg
          width="24"
          height="24"
          viewBox="0 0 24 24"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
        >
          <path
            d="M22 2L11 13"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
          <path
            d="M22 2L15 22L11 13L2 9L22 2Z"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
        </svg>
      </button>
    </div>
  </div>
</template>

<style scoped>
.chat-view-container {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 70px); /* 헤더 높이 제외 */
  background-color: white;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid var(--newbitdivider, #ececec);
  background-color: white;
}

.chat-header-left {
  display: flex;
  align-items: center;
}

.back-button {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  margin-right: 16px;
  color: #666;
  border-radius: 50%;
  transition: background-color 0.2s;
}

.back-button:hover {
  background-color: #f5f5f5;
}

.profile-wrapper {
  position: relative;
  margin-right: 12px;
}

.profile-img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.chat-info {
  display: flex;
  flex-direction: column;
}

.nickname {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.status {
  font-size: 12px;
  color: var(--newbitnormal, #3b82f6);
}

.chat-messages {
  flex: 1;
  padding: 20px 24px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 16px;
  background-color: #f8f9fb;
}

.message-wrapper {
  display: flex;
  flex-direction: column;
}

.message {
  display: inline-flex;
  flex-direction: column;
  max-width: 70%;
  margin-bottom: 2px;
}

.my-message {
  align-self: flex-end;
}

.other-message {
  align-self: flex-start;
}

.message-content {
  padding: 12px 16px;
  border-radius: 18px;
  font-size: 14px;
  line-height: 1.5;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  word-break: break-word;
}

.my-message .message-content {
  background-color: var(--newbitnormal, #3b82f6);
  color: white;
  border-bottom-right-radius: 4px;
}

.other-message .message-content {
  background-color: white;
  border-bottom-left-radius: 4px;
}

.message-time {
  font-size: 11px;
  color: #999;
  margin-top: 4px;
}

.my-message .message-time {
  align-self: flex-end;
}

.other-message .message-time {
  align-self: flex-start;
}

.no-messages {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
  color: #999;
  font-size: 14px;
}

.chat-input-container {
  display: flex;
  align-items: center;
  padding: 16px 24px;
  border-top: 1px solid var(--newbitdivider, #ececec);
  background-color: white;
}

.chat-input {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid var(--newbitdivider, #ececec);
  border-radius: 24px;
  font-size: 14px;
  outline: none;
}

.chat-input:focus {
  border-color: var(--newbitnormal, #3b82f6);
}

.send-button {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  margin-left: 12px;
  border: none;
  border-radius: 50%;
  background-color: var(--newbitnormal, #3b82f6);
  color: white;
  cursor: pointer;
  transition: background-color 0.2s;
}

.send-button:hover {
  background-color: #2563eb;
}
</style>
