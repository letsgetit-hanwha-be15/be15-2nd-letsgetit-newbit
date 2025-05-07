<script setup>
import Footer from "@/components/common/Footer.vue";
import { useNotificationStore } from "@/features/stores/notification.js";
import { useAuthStore } from "@/features/stores/auth.js";
import { onMounted, onUnmounted, watch, ref } from "vue";
import { useRouter, useRoute } from "vue-router";
import webSocketService from "@/features/coffeeletter/services/websocket";
import { useChatStore } from "@/features/coffeeletter/stores/chatStore";

const auth = useAuthStore();
const notification = useNotificationStore();
const chatStore = useChatStore();
const router = useRouter();
const route = useRoute();

const globalWebSocketStatus = ref({
  roomsSubscribed: false,
  userEventsSubscribed: false,
});

// 웹소켓 재연결 횟수 제한을 위한 카운터
const reconnectCounter = ref(0);
const MAX_RECONNECT_ATTEMPTS = 5;

const setupGlobalWebSocket = () => {
  console.log("App.vue: 전역 WebSocket 설정 시도");

  if (!auth.userId) {
    console.log("App.vue: 로그인 상태가 아님, WebSocket 설정 건너뜀");
    return;
  }

  // 재연결 시도 횟수 제한
  if (reconnectCounter.value >= MAX_RECONNECT_ATTEMPTS) {
    console.log(
      `App.vue: 최대 재연결 횟수(${MAX_RECONNECT_ATTEMPTS})에 도달, 추가 시도 중단`
    );
    return;
  }

  webSocketService.connect({
    userId: auth.userId,
    onConnected: () => {
      console.log("App.vue: WebSocket 연결 성공");
      reconnectCounter.value = 0; // 연결 성공 시 카운터 초기화

      // 전역적으로 구독 설정 (이미 구독 중이 아닌 경우에만)
      if (!globalWebSocketStatus.value.roomsSubscribed) {
        webSocketService.subscribeToRoomUpdates((updatedRoom) => {
          console.log("App.vue: 룸 업데이트 이벤트 수신", updatedRoom);
          chatStore.updateRoomFromTopicEvent(updatedRoom);
        });
        globalWebSocketStatus.value.roomsSubscribed = true;
      }

      if (!globalWebSocketStatus.value.userEventsSubscribed) {
        webSocketService.subscribeToUserEvents(auth.userId, (event) => {
          console.log("App.vue: 사용자 이벤트 수신", event);
          chatStore.updateRoomFromUserEvent(event);
        });
        globalWebSocketStatus.value.userEventsSubscribed = true;
      }

      // 채팅방 목록 로드
      chatStore.fetchRooms();
    },
    onError: (error) => {
      console.error("App.vue: WebSocket 연결 오류:", error);
      globalWebSocketStatus.value.roomsSubscribed = false;
      globalWebSocketStatus.value.userEventsSubscribed = false;
      reconnectCounter.value++; // 오류 발생 시 카운터 증가
    },
  });
};

// 웹소켓 연결 상태를 주기적으로 확인하고 필요시 재연결
const checkAndReconnectWebSocket = () => {
  if (!auth.userId) return;

  if (
    !webSocketService.isConnected() ||
    !globalWebSocketStatus.value.roomsSubscribed ||
    !globalWebSocketStatus.value.userEventsSubscribed
  ) {
    console.log("App.vue: WebSocket 연결 체크 - 재연결 필요");
    setupGlobalWebSocket();
  } else {
    console.log("App.vue: WebSocket 연결 체크 - 정상 연결 상태");
  }
};

// 라우터 변경 감지 - 페이지 이동 시에도 웹소켓 연결 유지 확인
watch(
  () => route.path,
  (newPath, oldPath) => {
    console.log(`App.vue: 페이지 이동 감지 ${oldPath} -> ${newPath}`);
    // 페이지 이동 시 웹소켓 연결 상태 확인
    if (auth.userId && !webSocketService.isConnected()) {
      console.log("App.vue: 페이지 이동 후 WebSocket 재연결 시도");
      setupGlobalWebSocket();
    }
  }
);

// 15초마다 웹소켓 연결 상태 확인
let reconnectInterval;
onMounted(() => {
  if (auth.userId) {
    notification.initSSE(auth.userId);
    notification.loadInitialNotifications();
    setupGlobalWebSocket();

    // 연결 유지를 위한 주기적 체크 설정
    reconnectInterval = setInterval(checkAndReconnectWebSocket, 15000);
  }
});

// 컴포넌트 언마운트 시 인터벌 정리
onUnmounted(() => {
  if (reconnectInterval) {
    clearInterval(reconnectInterval);
  }
});

watch(
  () => auth.userId,
  (newUserId, oldUserId) => {
    console.log("App.vue: User ID 변경 감지", oldUserId, "->", newUserId);

    // 로그아웃 시 구독 상태 초기화
    if (!newUserId && oldUserId) {
      globalWebSocketStatus.value.roomsSubscribed = false;
      globalWebSocketStatus.value.userEventsSubscribed = false;
      reconnectCounter.value = 0; // 로그아웃 시 카운터 초기화

      if (webSocketService.isConnected()) {
        console.log("App.vue: 로그아웃으로 WebSocket 구독 해제");
        webSocketService.unsubscribe("/topic/rooms");
        webSocketService.unsubscribe(`/user/${oldUserId}/queue/events`);
      }
    }

    if (newUserId) {
      setupGlobalWebSocket();
      notification.initSSE(newUserId);
      notification.loadInitialNotifications();
    }
  }
);
</script>

<template>
  <RouterView />
  <Footer />
</template>

<style scoped></style>
