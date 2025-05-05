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

const route = useRoute();
const router = useRouter();

const selectedRoomId = ref(null);

// TODO: 사용자 정보 auth 적용 후 수정
const currentUserId = ref(5);

// 쿼리 파라미터 roomId 변화를 감지해서 selectedRoomId를 갱신
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
