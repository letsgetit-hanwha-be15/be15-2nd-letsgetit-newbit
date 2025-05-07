import { defineStore } from "pinia";
import { ref } from "vue";
import { fetchMyChatRooms, markAsRead } from "@/api/coffeeletter";
import { useAuthStore } from "@/features/stores/auth";

export const useChatStore = defineStore("chat", () => {
  const rooms = ref([]);
  const isLoadingRooms = ref(false);
  const lastFetchTime = ref(0);
  const fetchStatus = ref("idle"); // 'idle', 'pending', 'success', 'error'
  const authStore = useAuthStore();

  // 캐시 유효 시간 (milliseconds) - 5초
  const CACHE_VALIDITY = 5000;

  async function fetchRooms(force = false) {
    console.log("[ChatStore] Fetching rooms...");

    // 액세스 토큰 검사
    if (!authStore.accessToken) {
      console.warn("[ChatStore] No access token found. Aborting fetchRooms.");
      rooms.value = [];
      isLoadingRooms.value = false;
      fetchStatus.value = "error";
      return;
    }

    // 이미 로딩 중이면 중복 요청 방지
    if (fetchStatus.value === "pending") {
      console.log("[ChatStore] Already fetching rooms. Request ignored.");
      return;
    }

    // 캐시 유효 시간 내에 이미 불러왔고, 강제 로드 아니면 스킵
    const now = Date.now();
    if (
      !force &&
      lastFetchTime.value > 0 &&
      now - lastFetchTime.value < CACHE_VALIDITY
    ) {
      console.log("[ChatStore] Using cached rooms data.");
      return;
    }

    isLoadingRooms.value = true;
    fetchStatus.value = "pending";

    try {
      const response = await fetchMyChatRooms();
      rooms.value = response.data || [];
      lastFetchTime.value = Date.now();
      fetchStatus.value = "success";
      console.log(
        "[ChatStore] Rooms fetched successfully:",
        rooms.value.length
      );
    } catch (error) {
      console.error(
        "[ChatStore] Failed to fetch rooms:",
        error.response?.data || error.message
      );
      fetchStatus.value = "error";
      // 에러 발생 시 이전 데이터 보존
    } finally {
      isLoadingRooms.value = false;
    }
  }

  function updateRoomFromTopicEvent(updatedRoom) {
    console.log(
      "ChatStore: Room update event received (topic/rooms)",
      updatedRoom
    );

    const currentUserId = authStore.userId;
    if (!currentUserId) {
      console.warn(
        "[ChatStore] No user ID found. Skipping room update from topic event."
      );
      return;
    }

    // 현재 사용자가 해당 채팅방의 멘토 또는 멘티인지 확인
    const isUserInRoom =
      updatedRoom.mentorId === currentUserId ||
      updatedRoom.menteeId === currentUserId;

    if (!isUserInRoom) {
      console.log(
        `[ChatStore] Room update event for room ${updatedRoom.id} ignored as user ${currentUserId} is not part of it.`
      );
      return;
    }

    const index = rooms.value.findIndex((room) => room.id === updatedRoom.id);
    if (index !== -1) {
      rooms.value[index] = updatedRoom;
    } else {
      rooms.value.unshift(updatedRoom);
    }
    sortRoomsByLastMessageTime();
  }

  function updateRoomFromUserEvent(event) {
    console.log("ChatStore: User event received (queue/events)", event);
    const currentUserId = authStore.userId;
    if (!currentUserId) {
      console.warn("[ChatStore] No user ID found. Skipping user event update.");
      return;
    }

    const roomIndex = rooms.value.findIndex((room) => room.id === event.roomId);
    const isCurrentUserMentorInRoom = (room) =>
      room && room.mentorId === currentUserId;

    if (roomIndex !== -1) {
      const room = rooms.value[roomIndex];
      if (event.type === "NEW_MESSAGE") {
        room.lastMessageContent = event.content;
        room.lastMessageTime = event.timestamp;
        if (event.senderId !== currentUserId) {
          if (isCurrentUserMentorInRoom(room)) {
            room.unreadCountMentor = (room.unreadCountMentor || 0) + 1;
          } else {
            room.unreadCountMentee = (room.unreadCountMentee || 0) + 1;
          }
        }
        sortRoomsByLastMessageTime();
        console.log("ChatStore: Room updated by NEW_MESSAGE event", room.id);
      } else if (event.type === "READ_UPDATE") {
        if (isCurrentUserMentorInRoom(room)) {
          room.unreadCountMentor = 0;
        } else {
          room.unreadCountMentee = 0;
        }
        console.log("ChatStore: Room updated by READ_UPDATE event", room.id);
      }
    } else if (event.type === "NEW_MESSAGE") {
      console.log("ChatStore: New room message detected, fetching rooms...");
      fetchRooms();
    }
  }

  // 마지막 메시지 시간 기준으로 채팅방 정렬
  function sortRoomsByLastMessageTime() {
    rooms.value.sort((a, b) => {
      const timeA = a.lastMessageTime
        ? new Date(a.lastMessageTime).getTime()
        : 0;
      const timeB = b.lastMessageTime
        ? new Date(b.lastMessageTime).getTime()
        : 0;
      return timeB - timeA;
    });
  }

  // 채팅방 읽음 처리 함수
  async function markRoomAsRead(roomId) {
    if (!authStore.accessToken) {
      console.warn("[ChatStore] No access token found. Cannot mark as read.");
      return false;
    }

    try {
      await markAsRead(roomId);

      // 현재 사용자 기준으로 읽음 상태 업데이트
      const roomIndex = rooms.value.findIndex((room) => room.id === roomId);
      if (roomIndex !== -1) {
        const room = rooms.value[roomIndex];
        const currentUserId = authStore.userId;

        if (room.mentorId === currentUserId) {
          room.unreadCountMentor = 0;
        } else {
          room.unreadCountMentee = 0;
        }
      }

      return true;
    } catch (error) {
      console.error("[ChatStore] Failed to mark room as read:", error);
      return false;
    }
  }

  function clearChatState() {
    console.log("ChatStore: Clearing chat state (rooms)");
    rooms.value = [];
    isLoadingRooms.value = false;
    lastFetchTime.value = 0;
    fetchStatus.value = "idle";
  }

  return {
    rooms,
    isLoadingRooms,
    fetchStatus,
    fetchRooms,
    updateRoomFromTopicEvent,
    updateRoomFromUserEvent,
    markRoomAsRead,
    clearChatState,
  };
});
