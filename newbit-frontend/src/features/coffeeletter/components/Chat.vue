<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from "vue";
import axios from "axios";

const props = defineProps({
  roomId: {
    type: String,
    required: true,
  },
});

// TODO; 사용자 정보  auth 적용 후 수정정
const currentUserId = ref(1);
const isMentor = ref(true);

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
const loading = ref(false);

const fetchRoomInfo = async () => {
  if (!props.roomId) return;

  loading.value = true;
  try {
    // 실제 API 구현 시 아래 코드 사용
    // const response = await axios.get(`/coffeeletter/rooms/${props.roomId}`);
    // const room = response.data;

    // 테스트용 더미 데이터 - 각 채팅방별 다른 정보
    const dummyRooms = {
      1: {
        id: "1",
        mentorId: 1,
        mentorName: "멘토A",
        menteeId: 2,
        menteeName: "멘티B",
        status: "ACTIVE",
      },
      2: {
        id: "2",
        mentorId: 1,
        mentorName: "멘토A",
        menteeId: 3,
        menteeName: "멘티C",
        status: "ACTIVE",
      },
      3: {
        id: "3",
        mentorId: 4,
        mentorName: "멘토D",
        menteeId: 1,
        menteeName: "멘티A",
        status: "ACTIVE",
      },
      4: {
        id: "4",
        mentorId: 1,
        mentorName: "멘토A",
        menteeId: 5,
        menteeName: "멘티E",
        status: "ACTIVE",
      },
      5: {
        id: "5",
        mentorId: 6,
        mentorName: "멘토F",
        menteeId: 1,
        menteeName: "멘티A",
        status: "ACTIVE",
      },
      6: {
        id: "6",
        mentorId: 1,
        mentorName: "멘토A",
        menteeId: 7,
        menteeName: "멘티G",
        status: "ACTIVE",
      },
    };

    const room = dummyRooms[props.roomId] || {
      id: props.roomId,
      mentorId: 1,
      mentorName: "멘토A",
      menteeId: 2,
      menteeName: "멘티B",
      status: "ACTIVE",
    };

    roomInfo.value = {
      id: room.id,
      partnerName: isMentor.value ? room.menteeName : room.mentorName,
      status: room.status,
      mentorId: room.mentorId,
      menteeId: room.menteeId,
    };

    console.log(`채팅방 ${props.roomId} 정보 로드됨:`, roomInfo.value);
  } catch (error) {
    console.error("채팅방 정보 조회 실패:", error);
  } finally {
    loading.value = false;
  }
};

const fetchMessages = async () => {
  if (!props.roomId) return;

  loading.value = true;
  try {
    // 실제 API 구현 시 아래 코드 사용
    // const response = await axios.get(`/coffeeletter/messages/${props.roomId}`);
    // messages.value = response.data;

    // 테스트용 더미 데이터 - 각 채팅방별 다른 메시지
    const dummyMessages = {
      1: [
        {
          id: "101",
          roomId: "1",
          senderId: 2,
          senderName: "멘티B",
          content: "안녕하세요! 커피챗 질문이 있어요.",
          timestamp: "2025-05-01T14:30:00",
          readByMentor: true,
          readByMentee: true,
        },
        {
          id: "102",
          roomId: "1",
          senderId: 1,
          senderName: "멘토A",
          content: "안녕하세요! 어떤 질문이신가요?",
          timestamp: "2025-05-01T14:31:00",
          readByMentor: true,
          readByMentee: true,
        },
        {
          id: "103",
          roomId: "1",
          senderId: 2,
          senderName: "멘티B",
          content:
            "프론트엔드 개발자로 커리어를 시작하려고 하는데, 어떤 기술 스택을 먼저 배우면 좋을까요?",
          timestamp: "2025-05-01T14:33:00",
          readByMentor: true,
          readByMentee: true,
        },
        {
          id: "104",
          roomId: "1",
          senderId: 1,
          senderName: "멘토A",
          content:
            "기본적으로 HTML, CSS, JavaScript는 필수입니다. 이후 React나 Vue.js 같은 프레임워크를 학습하시면 좋을 것 같아요.",
          timestamp: "2025-05-01T14:35:00",
          readByMentor: true,
          readByMentee: false,
        },
      ],
      2: [
        {
          id: "201",
          roomId: "2",
          senderId: 3,
          senderName: "멘티C",
          content: "지난 번 조언 정말 도움이 많이 되었습니다!",
          timestamp: "2025-04-30T09:10:00",
          readByMentor: true,
          readByMentee: true,
        },
        {
          id: "202",
          roomId: "2",
          senderId: 1,
          senderName: "멘토A",
          content:
            "도움이 되었다니 다행이네요. 추가 질문이 있으시면 언제든지 물어보세요.",
          timestamp: "2025-04-30T09:12:00",
          readByMentor: true,
          readByMentee: true,
        },
        {
          id: "203",
          roomId: "2",
          senderId: 3,
          senderName: "멘티C",
          content: "답변 감사합니다! 정말 도움이 많이 됐어요.",
          timestamp: "2025-04-30T09:15:00",
          readByMentor: false,
          readByMentee: true,
        },
      ],
      3: [
        {
          id: "301",
          roomId: "3",
          senderId: 1,
          senderName: "멘티A",
          content: "다음 주에 커피챗 일정 조율 가능할까요?",
          timestamp: "2025-04-29T18:15:00",
          readByMentor: true,
          readByMentee: true,
        },
        {
          id: "302",
          roomId: "3",
          senderId: 4,
          senderName: "멘토D",
          content: "네, 가능합니다. 수요일 오후 어떠세요?",
          timestamp: "2025-04-29T18:18:00",
          readByMentor: true,
          readByMentee: true,
        },
        {
          id: "303",
          roomId: "3",
          senderId: 1,
          senderName: "멘티A",
          content: "다음 주에 일정 조율 가능할까요?",
          timestamp: "2025-04-29T18:20:00",
          readByMentor: true,
          readByMentee: true,
        },
      ],
      4: [
        {
          id: "401",
          roomId: "4",
          senderId: 5,
          senderName: "멘티E",
          content: "프로젝트 리뷰 부탁드려요!",
          timestamp: "2025-04-28T10:40:00",
          readByMentor: false,
          readByMentee: true,
        },
        {
          id: "402",
          roomId: "4",
          senderId: 1,
          senderName: "멘토A",
          content: "네, 깃허브 링크 공유해 주시면 리뷰해 드리겠습니다.",
          timestamp: "2025-04-28T10:42:00",
          readByMentor: true,
          readByMentee: true,
        },
        {
          id: "403",
          roomId: "4",
          senderId: 5,
          senderName: "멘티E",
          content: "https://github.com/menteeE/project 여기 있습니다!",
          timestamp: "2025-04-28T10:45:00",
          readByMentor: false,
          readByMentee: true,
        },
      ],
      5: [
        {
          id: "501",
          roomId: "5",
          senderId: 6,
          senderName: "멘토F",
          content: "다음 주 화상 미팅 준비되셨나요?",
          timestamp: "2025-04-27T16:20:00",
          readByMentor: true,
          readByMentee: true,
        },
        {
          id: "502",
          roomId: "5",
          senderId: 1,
          senderName: "멘티A",
          content: "네, 준비 완료했습니다!",
          timestamp: "2025-04-27T16:25:00",
          readByMentor: true,
          readByMentee: true,
        },
        {
          id: "503",
          roomId: "5",
          senderId: 6,
          senderName: "멘토F",
          content: "온라인 미팅 링크 보내드렸습니다!",
          timestamp: "2025-04-27T16:30:00",
          readByMentor: true,
          readByMentee: false,
        },
      ],
      6: [
        {
          id: "601",
          roomId: "6",
          senderId: 7,
          senderName: "멘티G",
          content: "포트폴리오 피드백 감사합니다!",
          timestamp: "2025-04-26T09:40:00",
          readByMentor: true,
          readByMentee: true,
        },
        {
          id: "602",
          roomId: "6",
          senderId: 1,
          senderName: "멘토A",
          content: "별말씀을요. 궁금한 점 있으면 언제든 물어보세요.",
          timestamp: "2025-04-26T09:45:00",
          readByMentor: true,
          readByMentee: true,
        },
      ],
    };

    messages.value = dummyMessages[props.roomId] || [];
    console.log(`채팅방 ${props.roomId}의 메시지 로드됨:`, messages.value);
  } catch (error) {
    console.error("메시지 목록 조회 실패:", error);
  } finally {
    loading.value = false;
    markAsRead();
  }
};

const sendMessage = async () => {
  if (!newMessage.value.trim() || !props.roomId) return;

  const messageData = {
    roomId: props.roomId,
    senderId: currentUserId.value,
    senderName: isMentor.value ? "멘토A" : "멘티A",
    content: newMessage.value,
    timestamp: new Date().toISOString(),
    readByMentor: isMentor.value,
    readByMentee: !isMentor.value,
  };

  try {
    // 실제 API 구현 시 아래 코드 사용
    // await axios.post("/coffeeletter/messages", messageData);

    // 테스트용: 메시지 목록에 직접 추가
    const newMessageObj = {
      id: `msg-${Date.now()}`,
      ...messageData,
    };

    messages.value.push(newMessageObj);
    newMessage.value = "";

    await nextTick();
    scrollToBottom();
  } catch (error) {
    console.error("메시지 전송 실패:", error);
  }
};

const markAsRead = async () => {
  if (!props.roomId) return;

  try {
    // 실제 API 구현 시 아래 코드 사용
    // await axios.post(`/coffeeletter/messages/${props.roomId}/mark-as-read/${currentUserId.value}`);

    // 테스트용: 상태 직접 변경
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

onMounted(async () => {
  if (props.roomId) {
    await fetchRoomInfo();
    await fetchMessages();
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

watch(
  () => props.roomId,
  async () => {
    if (props.roomId) {
      await fetchRoomInfo();
      await fetchMessages();
      nextTick(() => {
        scrollToBottom();
      });
    } else {
      messages.value = [];
      roomInfo.value = {
        id: "",
        partnerName: "",
        status: "",
        mentorId: null,
        menteeId: null,
      };
    }
  }
);

let refreshInterval;
onMounted(() => {
  if (props.roomId) {
    refreshInterval = setInterval(() => {
      if (props.roomId) {
        fetchMessages();
      }
    }, 30 * 1000);
  }
});

onUnmounted(() => {
  if (refreshInterval) {
    clearInterval(refreshInterval);
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
              src="@/assets/image/profile.png"
              alt="프로필"
              class="profile-img"
            />
            <div class="chat-header-info">
              <span class="chat-nickname">{{ roomInfo.partnerName }}</span>
              <span class="chat-status">{{ statusText }}</span>
            </div>
          </div>
        </div>

        <div v-if="loading && messages.length === 0" class="loading-container">
          <div class="loading-spinner"></div>
        </div>
        <div v-else class="chat-messages" ref="chatMessagesContainer">
          <transition-group name="message" tag="div" class="messages-container">
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
                <div class="message-time">
                  {{ formatTime(message.timestamp) }}
                </div>
              </div>
            </div>
          </transition-group>
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
          <button
            @click="sendMessage"
            class="send-button"
            :disabled="!newMessage.trim()"
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
      </div>
    </transition>
  </div>
</template>

<style scoped>
.chat-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: white;
  height: 100%;
  overflow: hidden;
}

.chat-content-wrapper {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.messages-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid var(--newbitdivider, #ececec);
  background-color: white;
  height: 70px;
  box-sizing: border-box;
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
}

.chat-header-info {
  display: flex;
  flex-direction: column;
}

.chat-nickname {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.chat-status {
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
  min-height: 70px;
  box-sizing: border-box;
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

.send-button:hover:not(:disabled) {
  background-color: #2563eb;
}

.send-button:disabled {
  background-color: #a0c0f8;
  cursor: not-allowed;
}

.empty-chat {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f8f9fb;
}

.empty-chat-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #999;
}

.empty-chat-icon {
  width: 64px;
  height: 64px;
  opacity: 0.5;
  margin-bottom: 16px;
}

.loading-container {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.loading-spinner {
  width: 30px;
  height: 30px;
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

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.fade-enter-from {
  opacity: 0;
  transform: translateX(10px);
}

.fade-leave-to {
  opacity: 0;
  transform: translateX(-10px);
}

.message-enter-active {
  transition: all 0.3s ease;
}
.message-leave-active {
  transition: all 0.3s cubic-bezier(1, 0.5, 0.8, 1);
}
.message-enter-from {
  transform: translateY(20px);
  opacity: 0;
}
.message-leave-to {
  transform: translateY(-20px);
  opacity: 0;
}
</style>
