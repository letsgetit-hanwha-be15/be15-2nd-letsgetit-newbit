<script setup>
import {
  ref,
  computed,
  onMounted,
  onUnmounted,
  nextTick,
  watch,
  onBeforeUnmount,
} from "vue";
import {
  fetchRoomInfo,
  fetchMessagesByRoomPaging,
  sendMessage,
  markAsRead,
} from "@/api/coffeeletter";
import websocketService from "@/features/coffeeletter/services/websocket";
import { useIntersectionObserver } from "@vueuse/core";

// TODO; 사용자 정보 auth 적용 후 수정 ( 테스트용 하드 코딩)
const DEFAULT_ROOM_ID = "67fca09d6632d00f31d416bc";
const currentUserId = 3;
const isMentor = true;

const props = defineProps({
  roomId: {
    type: String,
    required: false,
    default: DEFAULT_ROOM_ID,
  },
});

const messages = ref([]);
const newMessage = ref("");
const chatMessagesContainer = ref(null);
const loadMoreTrigger = ref(null);
const roomInfo = ref({
  id: "",
  partnerName: "",
  partnerProfileImageUrl: "",
  status: "",
  mentorId: null,
  menteeId: null,
});
const loading = ref(false);
const initialLoading = ref(true);
const loadingOlderMessages = ref(false);
const error = ref(null);
const historyScrollPosition = ref(null);

const page = ref(0);
const size = ref(30);
const hasMore = ref(true);
const isFirstLoad = ref(true);
const scrollToLoadMoreVisible = ref(true);

const allowSendMessage = ref(true);

const { stop: stopObserver } = useIntersectionObserver(
  loadMoreTrigger,
  ([{ isIntersecting }]) => {
    if (isIntersecting && hasMore.value && !loadingOlderMessages.value) {
      loadOlderMessages();
    }
  },
  { threshold: 0.5 }
);

const getCurrentRoomId = () => {
  return props.roomId || DEFAULT_ROOM_ID;
};

const fetchRoomData = async () => {
  if (!getCurrentRoomId()) return;

  loading.value = true;
  initialLoading.value = true;
  error.value = null;

  try {
    const response = await fetchRoomInfo(getCurrentRoomId());
    const room = response.data;

    roomInfo.value = {
      id: room.id,
      partnerName: isMentor
        ? room.menteeName || room.menteeNickname
        : room.mentorName || room.mentorNickname,
      partnerProfileImageUrl: isMentor
        ? room.menteeProfileImageUrl
        : room.mentorProfileImageUrl,
      status: room.status,
      mentorId: room.mentorId,
      menteeId: room.menteeId,
    };

    page.value = 0;
    hasMore.value = true;
    isFirstLoad.value = true;
    messages.value = [];
    await loadMessages(true);
  } catch (err) {
    console.error("채팅방 정보 조회 실패:", err);
    error.value = "채팅방 정보를 불러오는데 실패했습니다.";
  } finally {
    loading.value = false;
    initialLoading.value = false;
  }
};

const loadMessages = async (reset = false) => {
  if (!getCurrentRoomId() || loadingOlderMessages.value) return;

  try {
    loadingOlderMessages.value = true;

    if (!reset && messages.value.length > 0) {
      historyScrollPosition.value = chatMessagesContainer.value.scrollHeight;
    }

    const response = await fetchMessagesByRoomPaging(
      getCurrentRoomId(),
      page.value,
      size.value,
      "DESC"
    );

    const data = response.data;
    const content = data.content || [];

    if (reset) {
      messages.value = [...content.reverse()];
      scrollToBottom();
    } else {
      messages.value = [...content.reverse(), ...messages.value];

      nextTick(() => {
        if (chatMessagesContainer.value && historyScrollPosition.value) {
          const newScrollPos =
            chatMessagesContainer.value.scrollHeight -
            historyScrollPosition.value;
          chatMessagesContainer.value.scrollTop = newScrollPos;
          historyScrollPosition.value = null;
        }
      });
    }

    hasMore.value = !data.last;
    scrollToLoadMoreVisible.value = hasMore.value;

    if (isFirstLoad.value) {
      scrollToBottom();
      isFirstLoad.value = false;
    }

    await markMessagesAsRead();
  } catch (err) {
    console.error("메시지 목록 조회 실패:", err);
    error.value = "메시지 목록을 불러오는데 실패했습니다.";
  } finally {
    loadingOlderMessages.value = false;
  }
};

const loadOlderMessages = async () => {
  if (!hasMore.value || loadingOlderMessages.value) return;
  page.value += 1;
  await loadMessages(false);
};

const sendNewMessage = async () => {
  if (!allowSendMessage.value) return;
  if (!newMessage.value.trim() || !getCurrentRoomId()) return;

  const messageData = {
    roomId: getCurrentRoomId(),
    senderId: currentUserId,
    senderName: isMentor ? "김멘토스" : "박멘티코어",
    content: newMessage.value,
    type: "CHAT",
    timestamp: new Date().toISOString(),
    readByMentor: isMentor,
    readByMentee: !isMentor,
  };

  try {
    const response = await sendMessage(messageData);
    const sentMessage = response.data;

    if (!websocketService.isConnected()) {
      messages.value.push(sentMessage);
      scrollToBottom();
    }

    newMessage.value = "";
  } catch (err) {
    console.error("메시지 전송 실패:", err);
    error.value = "메시지 전송에 실패했습니다. 다시 시도해주세요.";
  }
};

const markMessagesAsRead = async () => {
  if (!getCurrentRoomId()) return;

  try {
    await markAsRead(getCurrentRoomId(), currentUserId);
  } catch (err) {
    console.error("메시지 읽음 처리 실패:", err);
  }
};

const scrollToBottom = () => {
  nextTick(() => {
    if (chatMessagesContainer.value) {
      chatMessagesContainer.value.scrollTop =
        chatMessagesContainer.value.scrollHeight;
    }
  });
};

const isMyMessage = (message) => {
  return message.senderId === currentUserId;
};

const formatTime = (dateStr) => {
  if (!dateStr) return "";

  const date = new Date(dateStr);
  return `${date.getHours().toString().padStart(2, "0")}:${date
    .getMinutes()
    .toString()
    .padStart(2, "0")}`;
};

const formatDate = (dateStr) => {
  if (!dateStr) return "";

  const date = new Date(dateStr);
  const today = new Date();
  const yesterday = new Date(today);
  yesterday.setDate(yesterday.getDate() - 1);

  if (date.toDateString() === today.toDateString()) {
    return "오늘";
  }

  if (date.toDateString() === yesterday.toDateString()) {
    return "어제";
  }

  return `${date.getFullYear()}.${(date.getMonth() + 1)
    .toString()
    .padStart(2, "0")}.${date.getDate().toString().padStart(2, "0")}`;
};

const groupedMessages = computed(() => {
  const groups = [];
  let currentGroup = null;
  let lastDate = null;

  messages.value.forEach((message, index) => {
    const messageDate = new Date(message.timestamp).toDateString();

    if (lastDate !== messageDate) {
      if (lastDate !== null) {
        groups.push({ type: "date", date: message.timestamp });
      }
      lastDate = messageDate;
    }

    const prevMessage = index > 0 ? messages.value[index - 1] : null;
    const timeDiff = prevMessage
      ? new Date(message.timestamp) - new Date(prevMessage.timestamp)
      : Infinity;
    const isSameSender = prevMessage
      ? prevMessage.senderId === message.senderId
      : false;

    if (!isSameSender || timeDiff > 300000 || !currentGroup) {
      currentGroup = { type: "messages", messages: [message] };
      groups.push(currentGroup);
    } else {
      currentGroup.messages.push(message);
    }
  });

  return groups;
});

const statusText = computed(() => {
  if (!roomInfo.value.status) return "";

  switch (roomInfo.value.status) {
    case "ACTIVE":
      return "진행 중";
    case "INACTIVE":
      return "종료됨";
    case "CANCELED":
      return "취소됨";
    default:
      return roomInfo.value.status;
  }
});

const setupWebSocket = () => {
  if (!getCurrentRoomId()) return;

  websocketService.connect({
    roomId: getCurrentRoomId(),
    onNewMessage: (message) => {
      if (message.type === "CHAT" || message.type === "SYSTEM") {
        const isDuplicate = messages.value.some(
          (m) =>
            m.id === message.id ||
            (m.senderId === message.senderId &&
              m.content === message.content &&
              m.timestamp === message.timestamp)
        );

        if (!isDuplicate) {
          messages.value.push(message);
          markMessagesAsRead();
          scrollToBottom();
        }
      } else if (message.type === "READ_RECEIPT") {
      }
    },
    onConnected: () => {},
    onError: (error) => {
      console.error("WebSocket 연결 오류:", error);
      error.value =
        "채팅 서버 연결에 문제가 발생했습니다. 페이지를 새로고침 해주세요.";
    },
  });
};

watch(
  () => getCurrentRoomId(),
  async (newRoomId, oldRoomId) => {
    if (newRoomId) {
      if (oldRoomId && oldRoomId !== newRoomId) {
        websocketService.unsubscribe(`/topic/chat/room/${oldRoomId}`);
        websocketService.unsubscribe(`/topic/chat/${oldRoomId}`);
      }

      await fetchRoomData();
      setupWebSocket();
    } else {
      messages.value = [];
      roomInfo.value = {
        id: "",
        partnerName: "",
        partnerProfileImageUrl: "",
        status: "",
        mentorId: null,
        menteeId: null,
      };

      if (oldRoomId) {
        websocketService.unsubscribe(`/topic/chat/room/${oldRoomId}`);
        websocketService.unsubscribe(`/topic/chat/${oldRoomId}`);
      }
      page.value = 0;
      hasMore.value = true;
    }
  },
  { immediate: true }
);

const handleScroll = () => {
  if (!chatMessagesContainer.value) return;

  const { scrollTop } = chatMessagesContainer.value;

  scrollToLoadMoreVisible.value = scrollTop > 50 && hasMore.value;
};

onMounted(() => {
  if (getCurrentRoomId()) {
    fetchRoomData();
    setupWebSocket();

    if (chatMessagesContainer.value) {
      chatMessagesContainer.value.addEventListener("scroll", handleScroll);
    }
  }
});

onBeforeUnmount(() => {
  stopObserver();
  if (chatMessagesContainer.value) {
    chatMessagesContainer.value.removeEventListener("scroll", handleScroll);
  }
  if (getCurrentRoomId()) {
    websocketService.unsubscribe(`/topic/chat/room/${getCurrentRoomId()}`);
    websocketService.unsubscribe(`/topic/chat/${getCurrentRoomId()}`);
  }
});
</script>

<template>
  <div v-if="!roomId" class="empty-chat">
    <div class="empty-chat-content">
      <img
        src="@/assets/image/chat-icon.png"
        alt="Chat"
        class="empty-chat-icon"
      />
      <p>채팅방을 선택해주세요</p>
    </div>
  </div>
  <div v-else class="chat-container">
    <transition name="fade" mode="out-in">
      <div :key="roomId" class="chat-content-wrapper">
        <div class="chat-header">
          <div class="chat-header-left">
            <img
              :src="
                roomInfo.partnerProfileImageUrl ||
                '/src/assets/image/profile.png'
              "
              :alt="roomInfo.partnerName"
              class="profile-img"
            />
            <div class="chat-header-info">
              <span class="chat-nickname">{{ roomInfo.partnerName }}</span>
              <span
                class="chat-status"
                :class="{ 'status-inactive': roomInfo.status !== 'ACTIVE' }"
                >{{ statusText }}</span
              >
            </div>
          </div>
        </div>

        <div v-if="initialLoading" class="loading-container">
          <div class="loading-spinner"></div>
        </div>
        <div v-else class="chat-messages" ref="chatMessagesContainer">
          <!-- 무한 스크롤 트리거 -->
          <div
            v-if="hasMore"
            ref="loadMoreTrigger"
            class="infinite-scroll-trigger"
          >
            <div
              v-if="loadingOlderMessages"
              class="loading-spinner-small"
            ></div>
          </div>

          <!-- 이전 메시지 로드 버튼 -->
          <div
            v-if="hasMore && scrollToLoadMoreVisible"
            class="load-more-container"
          >
            <button
              @click="loadOlderMessages"
              class="load-more-button"
              :disabled="loadingOlderMessages"
            >
              <div v-if="loadingOlderMessages" class="loading-dots">
                <span></span><span></span><span></span>
              </div>
              <span v-else>이전 메시지 불러오기</span>
            </button>
          </div>

          <div v-if="messages.length === 0" class="no-messages">
            <p>대화를 시작해보세요!</p>
          </div>

          <div v-else class="messages-container">
            <template v-for="(group, index) in groupedMessages" :key="index">
              <!-- 날짜 구분선 -->
              <div v-if="group.type === 'date'" class="date-divider">
                <span>{{ formatDate(group.date) }}</span>
              </div>

              <!-- 메시지 그룹 -->
              <div
                v-else-if="group.type === 'messages'"
                class="message-group"
                :class="{
                  'my-message-group': isMyMessage(group.messages[0]),
                  'other-message-group': !isMyMessage(group.messages[0]),
                }"
              >
                <div
                  class="message-sender"
                  v-if="!isMyMessage(group.messages[0])"
                >
                  <img
                    :src="
                      roomInfo.partnerProfileImageUrl ||
                      '/src/assets/image/profile.png'
                    "
                    :alt="roomInfo.partnerName"
                    class="message-avatar"
                  />
                </div>
                <div class="message-content-group">
                  <div
                    class="message-sender-name"
                    v-if="!isMyMessage(group.messages[0])"
                  >
                    {{ roomInfo.partnerName }}
                  </div>
                  <div
                    v-for="(message, msgIndex) in group.messages"
                    :key="message.id"
                    class="message"
                    :class="{
                      'my-message': isMyMessage(message),
                      'other-message': !isMyMessage(message),
                      'first-message': msgIndex === 0,
                      'last-message': msgIndex === group.messages.length - 1,
                    }"
                  >
                    <div class="message-bubble">{{ message.content }}</div>
                    <div
                      class="message-time"
                      v-if="msgIndex === group.messages.length - 1"
                    >
                      {{ formatTime(message.timestamp) }}
                    </div>
                  </div>
                </div>
              </div>
            </template>
          </div>
        </div>

        <div class="chat-input-container">
          <div class="chat-input-wrapper">
            <input
              type="text"
              v-model="newMessage"
              placeholder="메시지를 입력하세요"
              class="chat-input"
              @keyup.enter="sendNewMessage"
              :disabled="
                allowSendMessage ? false : roomInfo.status !== 'ACTIVE'
              "
            />
            <button
              @click="sendNewMessage"
              class="send-button"
              :disabled="
                allowSendMessage
                  ? !newMessage.trim()
                  : !newMessage.trim() || roomInfo.status !== 'ACTIVE'
              "
              aria-label="메시지 보내기"
            >
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
          <div
            v-if="!allowSendMessage && roomInfo.status !== 'ACTIVE'"
            class="chat-disabled-message"
          >
            이 대화는 {{ statusText }} 상태입니다.
          </div>
          <div v-else-if="!allowSendMessage" class="chat-disabled-message">
            임의로 채팅 입력이 비활성화되어 있습니다.
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<style scoped>
.chat-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #f8fafc;
  height: 100%;
  overflow: hidden;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.chat-content-wrapper {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid #e5e7eb;
  background: #fff;
  height: 72px;
  box-sizing: border-box;
  box-shadow: 0 2px 8px 0 rgba(0, 0, 0, 0.03);
  border-radius: 12px 12px 0 0;
}

.chat-header-left {
  display: flex;
  align-items: center;
}

.profile-img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 12px;
  object-fit: cover;
  border: 1px solid #e5e7eb;
}

.chat-header-info {
  display: flex;
  flex-direction: column;
}

.chat-nickname {
  font-size: 16px;
  font-weight: 600;
  color: #222;
}

.chat-status {
  font-size: 12px;
  color: #60a5fa;
  margin-top: 2px;
  padding: 2px 8px;
  background-color: rgba(96, 165, 250, 0.1);
  border-radius: 10px;
  display: inline-block;
}

.status-inactive {
  color: #9ca3af;
  background-color: rgba(156, 163, 175, 0.1);
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  background: transparent;
  padding: 24px;
  gap: 0;
  scrollbar-width: thin;
  scrollbar-color: #d1d5db transparent;
}

.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-track {
  background: transparent;
}

.chat-messages::-webkit-scrollbar-thumb {
  background-color: #d1d5db;
  border-radius: 6px;
}

.messages-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
  overflow-y: visible;
}

.message-group {
  display: flex;
  margin-bottom: 24px;
}

.my-message-group {
  flex-direction: row-reverse;
}

.other-message-group {
  flex-direction: row;
}

.message-sender {
  margin-right: 8px;
  align-self: flex-start;
}

.message-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}

.message-content-group {
  display: flex;
  flex-direction: column;
  max-width: 70%;
  gap: 8px;
}

.message-sender-name {
  font-size: 12px;
  color: #6b7280;
  margin-bottom: 4px;
}

.message {
  display: flex;
  flex-direction: column;
  margin-bottom: 8px;
}

.my-message {
  align-self: flex-end;
}

.other-message {
  align-self: flex-start;
}

.message-bubble {
  padding: 12px 16px;
  border-radius: 18px;
  font-size: 14px;
  line-height: 1.5;
  word-break: break-word;
  background: #fff;
  color: #222;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.05);
}

.my-message .message-bubble {
  background: linear-gradient(135deg, #60a5fa 0%, #2563eb 100%);
  color: #fff;
  border-top-right-radius: 4px;
}

.other-message .message-bubble {
  background: #fff;
  border-top-left-radius: 4px;
  color: #222;
}

.first-message.my-message .message-bubble {
  border-top-right-radius: 18px;
}

.first-message.other-message .message-bubble {
  border-top-left-radius: 18px;
}

.last-message.my-message .message-bubble {
  border-bottom-right-radius: 18px;
}

.last-message.other-message .message-bubble {
  border-bottom-left-radius: 18px;
}

.message-time {
  font-size: 11px;
  color: #9ca3af;
  margin-top: 4px;
  align-self: flex-end;
}

.my-message .message-time {
  margin-right: 4px;
}

.other-message .message-time {
  margin-left: 4px;
}

.date-divider {
  display: flex;
  align-items: center;
  margin: 16px 0;
  color: #6b7280;
  font-size: 12px;
}

.date-divider::before,
.date-divider::after {
  content: "";
  flex: 1;
  height: 1px;
  background: #e5e7eb;
}

.date-divider::before {
  margin-right: 16px;
}

.date-divider::after {
  margin-left: 16px;
}

.date-divider span {
  padding: 0 10px;
  background: #f8fafc;
  border-radius: 10px;
}

.no-messages {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 180px;
  color: #9ca3af;
  font-size: 14px;
  font-style: italic;
}

.chat-input-container {
  display: flex;
  flex-direction: column;
  padding: 16px 24px;
  border-top: 1px solid #e5e7eb;
  background: #fff;
  min-height: 76px;
  box-sizing: border-box;
  box-shadow: 0 -2px 8px 0 rgba(0, 0, 0, 0.03);
  border-radius: 0 0 12px 12px;
}

.chat-input-wrapper {
  display: flex;
  align-items: center;
}

.chat-input {
  flex: 1;
  padding: 12px 18px;
  border: 1px solid #e5e7eb;
  border-radius: 24px;
  font-size: 14px;
  outline: none;
  background: #f8fafc;
  transition: border 0.2s, box-shadow 0.2s;
}

.chat-input:focus {
  border-color: #60a5fa;
  box-shadow: 0 2px 8px 0 rgba(96, 165, 250, 0.13);
}

.chat-input:disabled {
  background-color: #f3f4f6;
  cursor: not-allowed;
  color: #9ca3af;
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
  background: linear-gradient(135deg, #60a5fa 0%, #2563eb 100%);
  color: white;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 2px 6px 0 rgba(96, 165, 250, 0.13);
}

.send-button:hover:not(:disabled) {
  transform: scale(1.05);
  box-shadow: 0 4px 12px 0 rgba(96, 165, 250, 0.18);
}

.send-button:disabled {
  background: #e5e7eb;
  cursor: not-allowed;
  color: #9ca3af;
  box-shadow: none;
}

.empty-chat {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8fafc;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.empty-chat-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #9ca3af;
  padding: 40px;
  text-align: center;
}

.empty-chat-icon {
  width: 56px;
  height: 56px;
  opacity: 0.6;
  margin-bottom: 16px;
}

.loading-container {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.loading-spinner {
  width: 32px;
  height: 32px;
  border: 3px solid rgba(96, 165, 250, 0.2);
  border-top: 3px solid #60a5fa;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.loading-spinner-small {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(96, 165, 250, 0.2);
  border-top: 2px solid #60a5fa;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 8px auto;
}

.infinite-scroll-trigger {
  padding: 8px;
  text-align: center;
}

.load-more-container {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 16px;
  position: sticky;
  top: 0;
  z-index: 10;
}

.load-more-button {
  background-color: white;
  border: 1px solid #e5e7eb;
  border-radius: 20px;
  padding: 8px 16px;
  font-size: 13px;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 140px;
  min-height: 36px;
}

.load-more-button:hover:not(:disabled) {
  background-color: #f9fafb;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.08);
}

.load-more-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.loading-dots {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 4px;
}

.loading-dots span {
  width: 6px;
  height: 6px;
  background-color: #6b7280;
  border-radius: 50%;
  animation: bounce 1.4s infinite ease-in-out;
}

.loading-dots span:nth-child(1) {
  animation-delay: -0.32s;
}

.loading-dots span:nth-child(2) {
  animation-delay: -0.16s;
}

.chat-disabled-message {
  font-size: 12px;
  color: #9ca3af;
  text-align: center;
  margin-top: 8px;
  font-style: italic;
}

@keyframes bounce {
  0%,
  80%,
  100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.fade-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
