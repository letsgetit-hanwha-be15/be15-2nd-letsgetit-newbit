import { Client } from "@stomp/stompjs";
import { useAuthStore } from "@/features/stores/auth";

class WebSocketService {
  constructor() {
    this.stompClient = null;
    this.connected = false;
    this.subscribers = new Map();
    this.connectionAttempts = 0;
    this.maxConnectionAttempts = 5;
    this.reconnectTimer = null;

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

    // 이미 연결 중이라면 중복 연결 방지
    if (this.stompClient && this.connected) {
      console.log("WebSocket이 이미 연결되어 있습니다.");
      if (callbacks.onConnected) callbacks.onConnected();
      return this.stompClient;
    }

    // 접속 시도 횟수 초과 체크
    if (this.connectionAttempts >= this.maxConnectionAttempts) {
      console.error(
        `[WebSocket] 최대 연결 시도 횟수(${this.maxConnectionAttempts})를 초과했습니다.`
      );
      if (callbacks.onError) {
        callbacks.onError(new Error("Maximum connection attempts exceeded"));
      }
      return null;
    }

    const authStore = useAuthStore();
    const accessToken = authStore.accessToken;

    // 접근 토큰이 없으면 연결 중단
    if (!accessToken) {
      console.warn("[WebSocket] 접근 토큰이 없어 연결을 중단합니다.");
      if (callbacks.onError) {
        callbacks.onError(new Error("No access token available"));
      }
      return null;
    }

    console.log("[WebSocket] 연결 시도 URL:", this.webSocketUrl);
    this.connectionAttempts++;

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
          this.connectionAttempts = 0; // 연결 성공 시 카운터 초기화
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

          // 토큰 만료 등의 이유로 STOMP 오류 발생 시
          this.handleReconnect(callbacks);
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

          // 오류 발생 시 재연결 시도
          this.handleReconnect(callbacks);
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

          // 연결 종료 시 재연결 시도
          this.handleReconnect(callbacks);
        },
      });

      // 토큰을 최신 상태로 유지
      const updatedToken = authStore.accessToken;
      if (updatedToken) {
        this.stompClient.connectHeaders[
          "Authorization"
        ] = `Bearer ${updatedToken}`;
      } else {
        console.warn("[WebSocket] 토큰이 소멸됨. 연결을 중단합니다.");
        if (callbacks.onError) {
          callbacks.onError(new Error("Token expired or missing"));
        }
        return null;
      }

      console.log(
        "[WebSocket] stompClient.activate 호출, headers:",
        this.stompClient.connectHeaders
      );

      this.stompClient.activate();
    } catch (error) {
      console.error("[WebSocket] 연결 시도 중 오류 발생:", error);
      if (callbacks.onError) {
        callbacks.onError(error);
      }

      // 예외 발생 시 재연결 시도
      this.handleReconnect(callbacks);
    }

    return this.stompClient;
  }

  // 재연결 처리 로직
  handleReconnect(callbacks) {
    // 이미 재연결 타이머가 설정되어 있으면 중복 설정 방지
    if (this.reconnectTimer) {
      return;
    }

    // 연결 시도 횟수가 최대치를 초과하지 않은 경우에만 재연결
    if (this.connectionAttempts < this.maxConnectionAttempts) {
      const delay = Math.min(
        1000 * Math.pow(2, this.connectionAttempts - 1),
        30000
      );
      console.log(
        `[WebSocket] ${delay / 1000}초 후 재연결 시도 (${
          this.connectionAttempts
        }/${this.maxConnectionAttempts})`
      );

      this.reconnectTimer = setTimeout(() => {
        this.reconnectTimer = null;

        // 재연결 시 최신 토큰 확인
        const authStore = useAuthStore();
        if (authStore.accessToken) {
          this.connect(callbacks);
        } else {
          console.warn("[WebSocket] 재연결 취소: 토큰이 없음");
        }
      }, delay);
    }
  }

  setupSubscriptions(callbacks = {}) {
    if (!this.stompClient || !this.connected) return;
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
}

const webSocketService = new WebSocketService();

export default webSocketService;
