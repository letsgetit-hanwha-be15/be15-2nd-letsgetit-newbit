import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";

class WebSocketService {
  constructor() {
    this.stompClient = null;
    this.connected = false;
    this.subscribers = new Map();
    const API_PREFIX = import.meta.env.VITE_API_BASE_URL;
    this.webSocketUrl = `${API_PREFIX}feature/ws`;
    console.log("초기화된 WebSocket URL:", this.webSocketUrl);
  }

  connect(callbacks = {}) {
    if (this.stompClient && this.connected) {
      console.log("WebSocket이 이미 연결되어 있습니다.");
      if (callbacks.onConnected) callbacks.onConnected();
      return this.stompClient;
    }

    console.log("WebSocket 연결 시도:", this.webSocketUrl);

    try {
      const socket = new SockJS(this.webSocketUrl);
      this.stompClient = Stomp.over(socket);
      this.stompClient.debug = function () {};

      if (this.stompClient.reconnect_delay) {
        this.stompClient.reconnect_delay = 5000;
      }

      this.stompClient.connect(
        {},
        () => {
          console.log("WebSocket 연결 성공");
          this.connected = true;

          if (callbacks.userId) {
            if (callbacks.onUserEvent) {
              this.subscribeToUserEvents(
                callbacks.userId,
                callbacks.onUserEvent
              );
            }
          }

          if (callbacks.onRoomListUpdate) {
            this.subscribeToRoomUpdates(callbacks.onRoomListUpdate);
          }

          if (callbacks.onNewMessage && callbacks.roomId) {
            this.subscribeToChatRoom(callbacks.roomId, callbacks.onNewMessage);
          }

          if (callbacks.onConnected) {
            callbacks.onConnected();
          }
        },
        (error) => {
          console.error("WebSocket 연결 실패:", error);
          this.connected = false;

          if (callbacks.onError) {
            callbacks.onError(error);
          }
        }
      );
    } catch (error) {
      console.error("WebSocket 연결 시도 중 오류 발생:", error);
      if (callbacks.onError) {
        callbacks.onError(error);
      }
    }

    return this.stompClient;
  }

  disconnect() {
    if (this.stompClient && this.connected) {
      this.stompClient.disconnect();
      this.connected = false;
      this.subscribers.clear();
      console.log("WebSocket 연결 종료");
    }
  }

  subscribeToUserEvents(userId, callback) {
    const destination = `/user/${userId}/queue/events`;

    if (!this.subscribers.has(destination)) {
      const subscription = this.stompClient.subscribe(
        destination,
        (message) => {
          try {
            const eventData = JSON.parse(message.body);
            callback(eventData);
          } catch (e) {
            console.error("사용자 이벤트 처리 오류:", e);
          }
        }
      );

      this.subscribers.set(destination, subscription);
    }
  }

  subscribeToRoomUpdates(callback) {
    const destination = "/topic/rooms";

    if (!this.subscribers.has(destination)) {
      const subscription = this.stompClient.subscribe(
        destination,
        (message) => {
          try {
            const roomData = JSON.parse(message.body);
            callback(roomData);
          } catch (e) {
            console.error("룸 업데이트 이벤트 처리 오류:", e);
          }
        }
      );

      this.subscribers.set(destination, subscription);
    }
  }

  subscribeToChatRoom(roomId, callback) {
    const destinations = [
      `/topic/chat/room/${roomId}`,
      `/topic/chat/${roomId}`,
    ];

    destinations.forEach((destination) => {
      if (!this.subscribers.has(destination)) {
        const subscription = this.stompClient.subscribe(
          destination,
          (message) => {
            try {
              const chatMessage = JSON.parse(message.body);
              callback(chatMessage);
            } catch (e) {
              console.error(`메시지 처리 오류 (${destination}):`, e);
            }
          }
        );
        this.subscribers.set(destination, subscription);
      }
    });
  }

  unsubscribe(destination) {
    if (this.subscribers.has(destination)) {
      const subscription = this.subscribers.get(destination);
      subscription.unsubscribe();
      this.subscribers.delete(destination);
    }
  }

  joinChatRoom(roomId, userData) {
    if (this.stompClient && this.connected) {
      this.stompClient.send(
        `/app/chat.addUser/${roomId}`,
        {},
        JSON.stringify(userData)
      );
      return true;
    }
    return false;
  }

  isConnected() {
    return this.connected && this.stompClient !== null;
  }

  sendMessage(messageData) {
    if (this.stompClient && this.connected) {
      this.stompClient.send(
        "/app/chat.sendMessage",
        {},
        JSON.stringify(messageData)
      );
      return true;
    }
    return false;
  }
}

const webSocketService = new WebSocketService();

export default webSocketService;
