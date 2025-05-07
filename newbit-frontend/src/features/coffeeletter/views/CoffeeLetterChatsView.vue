<template>
  <div class="coffeeletter-container">
    <!-- 왼쪽: 채팅방 목록 -->
    <ChatRoomList
      :selected-room-id="selectedRoomId"
      @select-room="selectRoom"
    />

    <!-- 오른쪽: 채팅 내용 -->
    <Chat :room-id="selectedRoomId" />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import webSocketService from "@/features/coffeeletter/services/websocket";
import ChatRoomList from "@/features/coffeeletter/components/ChatRoomList.vue";
import Chat from "@/features/coffeeletter/components/Chat.vue";
import { useAuthStore } from "@/features/stores/auth";

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const selectedRoomId = ref(null);

const currentUserId = ref(0);

watch(
  () => route.query.roomId,
  (newRoomId) => {
    if (newRoomId) {
      selectedRoomId.value = newRoomId;
    }
  },
  { immediate: true }
);

const setupGlobalWebSocket = () => {
  // 사용자 ID가 없는 경우 연결 시도하지 않음
  if (!authStore.userId) {
    console.warn(
      "CoffeeLetterChatsView: WebSocket 연결 시도 중단 (사용자 ID 없음)"
    );
    return;
  }

  currentUserId.value = authStore.userId;

  webSocketService.connect({
    userId: currentUserId.value,
    onUserEvent: (event) => {
      console.log("전역 사용자 이벤트 수신:", event);
    },
    onConnected: () => {
      console.log("전역 WebSocket 연결 성공");
    },
    onError: (error) => {
      console.error("전역 WebSocket 연결 오류:", error);
    },
  });
};

onMounted(() => {
  setupGlobalWebSocket();
});

const selectRoom = (roomId) => {
  selectedRoomId.value = roomId;

  router.replace({
    query: { roomId: roomId },
  });
};

onUnmounted(() => {
  webSocketService.unsubscribe(`/user/${currentUserId.value}/queue/events`);
});
</script>

<style scoped>
.coffeeletter-container {
  display: flex;
  height: calc(100vh - 110px);
  width: 100%;
  background-color: #f5f7fa;
  margin: 0;
  overflow: hidden;
  position: relative;
  z-index: 1;
}
</style>
