import axios from "axios";

const API_PREFIX = import.meta.env.VITE_API_BASE_URL;

export const fetchChatRoomsByUser = (userId) => {
  return axios.get(`${API_PREFIX}feature/coffeeletter/rooms/user/${userId}`);
};

export const fetchRoomInfo = (roomId) => {
  return axios.get(`${API_PREFIX}feature/coffeeletter/rooms/${roomId}`);
};

export const fetchMessagesByRoom = (roomId) => {
  return axios.get(`${API_PREFIX}feature/coffeeletter/messages/${roomId}`);
};

export const fetchMessagesByRoomPaging = (
  roomId,
  page = 0,
  size = 30,
  direction = "ASC"
) => {
  return axios.get(
    `${API_PREFIX}feature/coffeeletter/messages/${roomId}/paging`,
    {
      params: { page, size, direction },
    }
  );
};

export const sendMessage = (messageData) => {
  return axios.post(`${API_PREFIX}feature/coffeeletter/messages`, messageData);
};

export const markAsRead = (roomId, userId) => {
  return axios.post(
    `${API_PREFIX}feature/coffeeletter/messages/${roomId}/mark-as-read/${userId}`
  );
};

// 커피챗 ID로 채팅방 조회
export const getRoomByCoffeeChatId = (coffeeChatId) => {
  return axios.get(
    `${API_PREFIX}feature/coffeeletter/rooms/coffeechat/${coffeeChatId}`
  );
};

// 채팅방 생성
export const createRoom = (roomData) => {
  return axios.post(`${API_PREFIX}feature/coffeeletter/rooms`, roomData);
};

// 채팅방 종료
export const endRoom = (roomId) => {
  return axios.put(`${API_PREFIX}feature/coffeeletter/rooms/${roomId}/end`);
};

// 채팅방 취소
export const cancelRoom = (roomId) => {
  return axios.put(`${API_PREFIX}feature/coffeeletter/rooms/${roomId}/cancel`);
};
