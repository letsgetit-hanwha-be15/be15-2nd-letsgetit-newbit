import { defineStore } from "pinia";
import { ref, computed } from "vue";
import { fetchMyChatRooms, markAsRead } from "@/api/coffeeletter";
import { useAuthStore } from "@/features/stores/auth";
import { useProfileStore } from "@/features/stores/profile";
import webSocketService from "@/features/coffeeletter/services/websocket";

export const useChatStore = defineStore("chat", () => {
  const rooms = ref([]);
  const isLoadingRooms = ref(false);
  const lastFetchTime = ref(0);
  const fetchStatus = ref("idle");
  const authStore = useAuthStore();
  const profileStore = useProfileStore();

  const CACHE_VALIDITY = 5000;

  const currentUserInfo = computed(() => {
    return {
      userId: parseInt(authStore.userId) || null,
      nickname: authStore.nickname || null,
      profileImageUrl: authStore.profileImageUrl || null,
      isMentor: authStore.userRole === "ROLE_MENTOR",
    };
  });

  async function fetchRooms(force = false) {
    console.log("[ChatStore] Fetching rooms...");

    if (!authStore.accessToken) {
      console.warn("[ChatStore] No access token found. Aborting fetchRooms.");
      rooms.value = [];
      isLoadingRooms.value = false;
      fetchStatus.value = "error";
      return;
    }

    if (fetchStatus.value === "pending") {
      console.log("[ChatStore] Already fetching rooms. Request ignored.");
      return;
    }

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

      await loadRoomParticipantsProfiles();
    } catch (error) {
      console.error(
        "[ChatStore] Failed to fetch rooms:",
        error.response?.data || error.message
      );
      fetchStatus.value = "error";
    } finally {
      isLoadingRooms.value = false;
    }
  }

  async function loadRoomParticipantsProfiles() {
    if (!rooms.value.length) return;

    const usersToFetch = new Set();

    rooms.value.forEach((room) => {
      usersToFetch.add(room.mentorId);
      usersToFetch.add(room.menteeId);
    });

    console.log("[ChatStore] Loading profiles for users:", [...usersToFetch]);

    const profilePromises = [...usersToFetch].map((userId) =>
      profileStore.fetchUserProfile(userId)
    );

    const results = await Promise.allSettled(profilePromises);
    console.log("[ChatStore] Profile fetch results:", results);

    updateRoomsWithProfileInfo();
  }

  function updateRoomsWithProfileInfo() {
    console.log("[ChatStore] Updating rooms with profile info");

    rooms.value.forEach((room) => {
      const mentorProfile = profileStore.getCachedProfile(room.mentorId);
      if (mentorProfile) {
        console.log(
          `[ChatStore] Updating mentor info for room ${room.id}`,
          mentorProfile
        );
        room.mentorNickname = mentorProfile.nickname || room.mentorNickname;
        room.mentorProfileImageUrl =
          mentorProfile.profileImageUrl || room.mentorProfileImageUrl;
      }

      const menteeProfile = profileStore.getCachedProfile(room.menteeId);
      if (menteeProfile) {
        console.log(
          `[ChatStore] Updating mentee info for room ${room.id}`,
          menteeProfile
        );
        room.menteeNickname = menteeProfile.nickname || room.menteeNickname;
        room.menteeProfileImageUrl =
          menteeProfile.profileImageUrl || room.menteeProfileImageUrl;
      }
    });
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

    const isUserInRoom =
      updatedRoom.mentorId === parseInt(currentUserId) ||
      updatedRoom.menteeId === parseInt(currentUserId);

    if (!isUserInRoom) {
      console.log(
        `[ChatStore] Room update event for room ${updatedRoom.id} ignored as user ${currentUserId} is not part of it.`
      );
      return;
    }

    const index = rooms.value.findIndex((room) => room.id === updatedRoom.id);
    if (index !== -1) {
      console.log(`[ChatStore] Updating existing room ${updatedRoom.id}`);

      const oldRoom = rooms.value[index];
      updatedRoom.mentorNickname =
        updatedRoom.mentorNickname || oldRoom.mentorNickname;
      updatedRoom.mentorProfileImageUrl =
        updatedRoom.mentorProfileImageUrl || oldRoom.mentorProfileImageUrl;
      updatedRoom.menteeNickname =
        updatedRoom.menteeNickname || oldRoom.menteeNickname;
      updatedRoom.menteeProfileImageUrl =
        updatedRoom.menteeProfileImageUrl || oldRoom.menteeProfileImageUrl;

      rooms.value[index] = updatedRoom;
    } else {
      console.log(`[ChatStore] Adding new room ${updatedRoom.id}`);
      rooms.value.unshift(updatedRoom);

      const partnerUserId =
        updatedRoom.mentorId === parseInt(currentUserId)
          ? updatedRoom.menteeId
          : updatedRoom.mentorId;

      profileStore.fetchUserProfile(partnerUserId).then((profile) => {
        if (profile) {
          const newRoomIndex = rooms.value.findIndex(
            (r) => r.id === updatedRoom.id
          );
          if (newRoomIndex !== -1) {
            if (updatedRoom.mentorId === partnerUserId) {
              rooms.value[newRoomIndex].mentorNickname =
                profile.nickname || rooms.value[newRoomIndex].mentorNickname;
              rooms.value[newRoomIndex].mentorProfileImageUrl =
                profile.profileImageUrl ||
                rooms.value[newRoomIndex].mentorProfileImageUrl;
            } else {
              rooms.value[newRoomIndex].menteeNickname =
                profile.nickname || rooms.value[newRoomIndex].menteeNickname;
              rooms.value[newRoomIndex].menteeProfileImageUrl =
                profile.profileImageUrl ||
                rooms.value[newRoomIndex].menteeProfileImageUrl;
            }
          }
        }
      });
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

    console.log("[ChatStore] 이벤트 처리 시작:", {
      eventType: event.type,
      roomId: event.roomId,
      senderId: event.senderId,
      content: event.content,
      hasRoomInStore: rooms.value.some((room) => room.id === event.roomId),
    });

    const roomIndex = rooms.value.findIndex((room) => room.id === event.roomId);
    const isCurrentUserMentorInRoom = (room) =>
      room && room.mentorId === parseInt(currentUserId);

    if (roomIndex !== -1) {
      const room = { ...rooms.value[roomIndex] };

      if (event.type === "NEW_MESSAGE") {
        room.lastMessageContent = event.content;
        room.lastMessageTime = event.timestamp;
        room.lastMessageSenderId = event.senderId;

        if (event.senderId !== parseInt(currentUserId)) {
          if (isCurrentUserMentorInRoom(room)) {
            room.unreadCountMentor = (room.unreadCountMentor || 0) + 1;
          } else {
            room.unreadCountMentee = (room.unreadCountMentee || 0) + 1;
          }
          console.log("[ChatStore] 읽지 않은 메시지 카운트 증가:", {
            roomId: room.id,
            isMentor: isCurrentUserMentorInRoom(room),
            unreadCountMentor: room.unreadCountMentor,
            unreadCountMentee: room.unreadCountMentee,
          });
        }

        rooms.value.splice(roomIndex, 1, room);

        sortRoomsByLastMessageTime();

        console.log(
          "ChatStore: Room updated by NEW_MESSAGE event",
          room.id,
          room
        );
      } else if (
        event.type === "READ_UPDATE" ||
        event.type === "READ_RECEIPT"
      ) {
        if (isCurrentUserMentorInRoom(room)) {
          room.unreadCountMentor = 0;
        } else {
          room.unreadCountMentee = 0;
        }

        rooms.value.splice(roomIndex, 1, room);

        console.log(
          `ChatStore: Room updated by ${event.type} event`,
          room.id,
          room
        );
      }
    } else if (event.type === "NEW_MESSAGE") {
      console.log(
        `ChatStore: New message for unknown room ${event.roomId}, fetching rooms...`
      );
      fetchRooms(true).then(() => {
        const newRoomIndex = rooms.value.findIndex(
          (room) => room.id === event.roomId
        );
        if (newRoomIndex !== -1) {
          console.log(
            `ChatStore: Room ${event.roomId} now found after fetching`
          );
        } else {
          console.warn(
            `ChatStore: Room ${event.roomId} still not found after fetching`
          );
        }
      });
    }
  }

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

  async function markRoomAsRead(roomId) {
    if (!authStore.accessToken) {
      console.warn("[ChatStore] No access token found. Cannot mark as read.");
      return false;
    }

    try {
      await markAsRead(roomId);

      if (webSocketService.isConnected()) {
        webSocketService.sendReadReceipt(roomId);
      }

      const roomIndex = rooms.value.findIndex((room) => room.id === roomId);
      if (roomIndex !== -1) {
        const room = rooms.value[roomIndex];
        const currentUserId = authStore.userId;

        if (room.mentorId === parseInt(currentUserId)) {
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

  function getPartnerInfo(room) {
    if (!room) return null;

    const isUserMentor = room.mentorId === parseInt(authStore.userId);
    const partnerId = isUserMentor ? room.menteeId : room.mentorId;

    const cachedProfile = profileStore.getCachedProfile(partnerId);

    console.log(`[ChatStore] Getting partner info for room ${room.id}:`, {
      isUserMentor,
      partnerId,
      cachedProfile,
      roomMentorNickname: room.mentorNickname,
      roomMenteeNickname: room.menteeNickname,
    });

    const defaultName = isUserMentor ? "멘티" : "멘토";

    const partnerNickname =
      cachedProfile?.nickname ||
      (isUserMentor ? room.menteeNickname : room.mentorNickname) ||
      defaultName;

    return {
      userId: partnerId,
      nickname: partnerNickname,
      profileImageUrl: isUserMentor
        ? room.menteeProfileImageUrl || cachedProfile?.profileImageUrl
        : room.mentorProfileImageUrl || cachedProfile?.profileImageUrl,
      isMentor: !isUserMentor,
    };
  }

  return {
    rooms,
    isLoadingRooms,
    fetchStatus,
    currentUserInfo,
    fetchRooms,
    updateRoomFromTopicEvent,
    updateRoomFromUserEvent,
    markRoomAsRead,
    clearChatState,
    getPartnerInfo,
  };
});
