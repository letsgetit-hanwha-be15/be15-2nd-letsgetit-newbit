import api from "./axios.js";
import { useAuthStore } from "@/features/stores/auth";

/* 1. 내 채팅방 목록 조회 */
export function fetchMyChatRooms() {
  return api.get(`feature/coffeeletter/rooms/my`);
}

/* 2. 채팅방 정보 조회 */
export function fetchRoomInfo(roomId) {
  return api.get(`feature/coffeeletter/rooms/${roomId}`);
}

/* 3. 채팅방 메시지 목록 조회 */
export function fetchMessagesByRoom(roomId) {
  return api.get(`feature/coffeeletter/messages/${roomId}`);
}

/* 4. 채팅방 메시지 페이징 조회 */
export function fetchMessagesByRoomPaging(
  roomId,
  page = 0,
  size = 30,
  direction = "ASC"
) {
  return api.get(`feature/coffeeletter/messages/${roomId}/paging`, {
    params: { page, size, direction },
  });
}

/* 5. 메시지 전송 */
export function sendMessage(messageData) {
  return api.post(`feature/coffeeletter/messages`, messageData);
}

/* 6. 메시지 읽음 표시 */
export function markAsRead(roomId) {
  const authStore = useAuthStore();
  const userId = authStore.userId;
  return api.post(
    `feature/coffeeletter/messages/${roomId}/mark-as-read/${userId}`
  );
}

/* 7. 커피챗 ID로 채팅방 조회 */
export function getRoomByCoffeeChatId(coffeeChatId) {
  return api.get(`feature/coffeeletter/rooms/coffeechat/${coffeeChatId}`);
}

/* 8. 채팅방 생성 */
export function createRoom(roomData) {
  return api.post(`feature/coffeeletter/rooms`, roomData);
}

/* 9. 채팅방 종료 */
export function endRoom(roomId) {
  return api.put(`feature/coffeeletter/rooms/${roomId}/end`);
}

/* 10. 채팅방 취소 */
export function cancelRoom(roomId) {
  return api.put(`feature/coffeeletter/rooms/${roomId}/cancel`);
}

/* 11. 테스트용 채팅방 생성 */
export function createTestRoom(mentorId, menteeId) {
  console.log(
    `[API] Creating test room with mentorId: ${mentorId}, menteeId: ${menteeId}`
  );

  const authStore = useAuthStore();
  const menteeNickname = authStore.nickname || "멘티";
  const mentorNickname = "멘토스";

  // 테스트 멘토 프로필 이미지 URL
  const mentorProfileImageUrl =
    "https://via.placeholder.com/150/3b82f6/ffffff?text=멘토";

  const roomData = {
    mentorId: mentorId,
    menteeId: menteeId,
    mentorNickname: mentorNickname,
    menteeNickname: menteeNickname,
    mentorProfileImageUrl: mentorProfileImageUrl,
    menteeProfileImageUrl: authStore.profileImageUrl || null,
    status: "ACTIVE",
  };
  console.log("[API] Sending roomData:", roomData);
  return api.post(`feature/coffeeletter/rooms`, roomData);
}
