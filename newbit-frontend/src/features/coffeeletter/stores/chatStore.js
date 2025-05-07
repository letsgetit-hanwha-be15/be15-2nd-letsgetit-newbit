import { defineStore } from "pinia";
import { ref } from "vue";
import { fetchMyChatRooms, markAsRead } from "@/api/coffeeletter";
import { useAuthStore } from "@/features/stores/auth";

export const useChatStore = defineStore("chat", () => {
  const rooms = ref([]);
  const isLoadingRooms = ref(false);
  const authStore = useAuthStore();

  async function fetchRooms() {
    console.log("[ChatStore] Fetching rooms...");
    isLoadingRooms.value = true;
    if (!authStore.accessToken) {
      console.warn("[ChatStore] No access token found. Aborting fetchRooms.");
      rooms.value = [];
      isLoadingRooms.value = false;
      return;
    }

    try {
      const response = await fetchMyChatRooms();
      rooms.value = response.data || [];
      console.log(
        "[ChatStore] Rooms fetched successfully:",
        rooms.value.length
      );
    } catch (error) {
      console.error(
        "[ChatStore] Failed to fetch rooms:",
        error.response?.data || error.message
      );
      rooms.value = [];
    } finally {
      isLoadingRooms.value = false;
    }
  }

  function updateRoomFromTopicEvent(updatedRoom) {
    console.log(
      "ChatStore: Room update event received (topic/rooms)",
      updatedRoom
    );
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
  }

  function updateRoomFromUserEvent(event) {
    console.log("ChatStore: User event received (queue/events)", event);
    const currentUserId = authStore.parsedUserId;
    if (!currentUserId) return;

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
        rooms.value.sort((a, b) => {
          const timeA = a.lastMessageTime
            ? new Date(a.lastMessageTime).getTime()
            : 0;
          const timeB = b.lastMessageTime
            ? new Date(b.lastMessageTime).getTime()
            : 0;
          return timeB - timeA;
        });
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

  function clearChatState() {
    console.log("ChatStore: Clearing chat state (rooms)");
    rooms.value = [];
    isLoadingRooms.value = false;
  }

  return {
    rooms,
    isLoadingRooms,
    fetchRooms,
    updateRoomFromTopicEvent,
    updateRoomFromUserEvent,
    clearChatState,
  };
});
