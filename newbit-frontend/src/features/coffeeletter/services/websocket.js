import { Client } from "@stomp/stompjs";
import { useAuthStore } from "@/features/stores/auth";

class WebSocketService {
  constructor() {
    this.stompClient = null;
    this.connected = false;
    this.subscribers = new Map();

    const protocol = window.location.protocol === "https:" ? "wss:" : "ws:";
    const host = window.location.host;
    this.webSocketUrl = `${protocol}//${host}/api/v1/feature/ws`;

    console.log("초기화된 순수 WebSocket URL:", this.webSocketUrl);
  }

  connect(callbacks = {}) {
    console.log(
      "[WebSocket] Pure WebSocket connect function entered.",
      callbacks
    );
    if (this.stompClient && this.connected) {
      console.log("WebSocket이 이미 연결되어 있습니다.");
      if (callbacks.onConnected) callbacks.onConnected();

      // 이미 연결된 상태에서 추가 구독 설정
      this.setupSubscriptions(callbacks);
      return this.stompClient;
    }

    console.log("[WebSocket] 연결 시도 URL:", this.webSocketUrl);

    try {
      this.stompClient = new Client({
        brokerURL: this.webSocketUrl,
        connectHeaders: {},
        debug: (str) => {
          console.log("[WebSocket][STOMP debug]", str);
        },
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
        onConnect: () => {
          console.log("[WebSocket] 연결 성공 (Pure WebSocket)");
          this.connected = true;
          this.setupSubscriptions(callbacks);
          if (callbacks.onConnected) {
            callbacks.onConnected();
          }
        },
        onStompError: (frame) => {
          console.error(
            "[WebSocket] STOMP 오류:",
            frame.headers["message"],
            frame.body
          );
          this.connected = false;
          if (callbacks.onError) {
            callbacks.onError(
              new Error(`STOMP error: ${frame.headers["message"]}`)
            );
          }
        },
        onWebSocketError: (error) => {
          console.error(
            "[WebSocket] WebSocket 오류 (onWebSocketError):",
            error
          );
          this.connected = false;
          if (callbacks.onError) {
            callbacks.onError(
              error instanceof Error ? error : new Error("WebSocket error")
            );
          }
        },
        onWebSocketClose: (event) => {
          console.log(
            "[WebSocket] WebSocket 연결 종료 (onWebSocketClose):",
            event.code,
            event.reason,
            event.wasClean
          );
          this.connected = false;

          if (callbacks.onError) {
            callbacks.onError(
              new Error(`WebSocket closed: ${event.code} ${event.reason}`)
            );
          }
        },
      });

      const authStore = useAuthStore();
      const accessToken = authStore.accessToken;
      console.log("[WebSocket] 현재 accessToken:", accessToken);
      if (accessToken) {
        this.stompClient.connectHeaders[
          "Authorization"
        ] = `Bearer ${accessToken}`;
      }
      console.log(
        "[WebSocket] stompClient.activate 호출, headers:",
        this.stompClient.connectHeaders
      );

      this.stompClient.activate();
    } catch (error) {
      console.error("[WebSocket] 연결 시도 중 오류 발생:", error);
      this.connected = false;
      if (callbacks.onError) {
        callbacks.onError(error);
      }
    }

    return this.stompClient;
  }

  setupSubscriptions(callbacks = {}) {
    if (!this.stompClient || !this.connected) {
      console.warn(
        "[WebSocket] setupSubscriptions: 웹소켓이 연결되지 않아 구독을 설정할 수 없습니다."
      );
      return;
    }

    console.log("[WebSocket] Setting up subscriptions...");

    if (callbacks.userId && callbacks.onUserEvent) {
      this.subscribeToUserEvents(callbacks.userId, callbacks.onUserEvent);
    }
    if (callbacks.onRoomListUpdate) {
      this.subscribeToRoomUpdates(callbacks.onRoomListUpdate);
    }
    if (callbacks.onNewMessage && callbacks.roomId) {
      this.subscribeToChatRoom(callbacks.roomId, callbacks.onNewMessage);
    }
  }

  disconnect() {
    if (this.stompClient && this.connected) {
      this.stompClient.deactivate();
      this.connected = false;
      this.subscribers.clear();
      console.log("WebSocket 연결 종료 (deactivated)");
    }
  }

  subscribeToUserEvents(userId, callback) {
    if (!this.stompClient || !this.connected) {
      console.warn(
        "[WebSocket] subscribeToUserEvents: 웹소켓이 연결되지 않아 구독할 수 없습니다."
      );
      return null;
    }

    const destination = `/user/${userId}/queue/events`;
    if (!this.subscribers.has(destination)) {
      try {
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
        console.log(`[WebSocket] 구독 성공: ${destination}`);
        return subscription;
      } catch (error) {
        console.error(`[WebSocket] 구독 실패: ${destination}`, error);
        return null;
      }
    } else {
      console.log(`[WebSocket] 이미 구독 중: ${destination}`);
      return this.subscribers.get(destination);
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
      try {
        const subscription = this.subscribers.get(destination);
        subscription.unsubscribe();
        this.subscribers.delete(destination);
        console.log(`[WebSocket] 구독 해제: ${destination}`);
      } catch (error) {
        console.error(`[WebSocket] 구독 해제 오류: ${destination}`, error);
      }
    } else {
      console.log(
        `[WebSocket] 구독 해제 시도했으나 이미 구독되지 않음: ${destination}`
      );
    }
  }

  joinChatRoom(roomId, userData) {
    if (this.stompClient && this.connected) {
      const authStore = useAuthStore();
      const headers = {};
      if (authStore.accessToken) {
        headers["Authorization"] = `Bearer ${authStore.accessToken}`;
      }
      this.stompClient.publish({
        destination: `/app/chat.addUser/${roomId}`,
        body: JSON.stringify(userData),
        headers: headers,
      });
      return true;
    }
    return false;
  }

  isConnected() {
    return this.stompClient !== null && this.stompClient.active;
  }

  sendMessage(messageData) {
    if (this.stompClient && this.connected) {
      const authStore = useAuthStore();
      const headers = {};
      if (authStore.accessToken) {
        headers["Authorization"] = `Bearer ${authStore.accessToken}`;
      }
      this.stompClient.publish({
        destination: "/app/chat.sendMessage",
        body: JSON.stringify(messageData),
        headers: headers,
      });
      return true;
    }
    return false;
  }

  // 읽음 처리 메시지 전송
  sendReadReceipt(roomId) {
    if (this.stompClient && this.connected) {
      const authStore = useAuthStore();
      if (!authStore.accessToken) return false;

      const headers = {
        Authorization: `Bearer ${authStore.accessToken}`,
      };

      const messageData = {
        roomId: roomId,
        senderId: parseInt(authStore.userId),
        senderName: authStore.nickname || "알 수 없는 사용자",
        content: "메시지를 읽었습니다",
        type: "READ_RECEIPT",
        timestamp: new Date().toISOString(),
      };

      this.stompClient.publish({
        destination: "/app/chat.markAsRead",
        body: JSON.stringify(messageData),
        headers: headers,
      });
      return true;
    }
    return false;
  }
}

const webSocketService = new WebSocketService();

export default webSocketService;
