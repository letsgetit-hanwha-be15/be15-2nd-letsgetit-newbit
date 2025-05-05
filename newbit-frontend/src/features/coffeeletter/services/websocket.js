import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";

class WebSocketService {
  constructor() {
    this.stompClient = null;
    this.connected = false;
    this.subscribers = new Map();

    // 환경에 따라 다른 WebSocket URL 설정
    // 개발 환경에서 직접 서비스에 연결
    this.webSocketUrl =
      process.env.NODE_ENV === "development"
        ? "http://localhost:8000/api/v1/feature/ws" // 개발 환경: 게이트웨이 통해 연결
        : "/api/v1/feature/ws"; // 프로덕션: 게이트웨이 통해 연결

    console.log("초기화된 WebSocket URL:", this.webSocketUrl);
    console.log("현재 환경:", process.env.NODE_ENV);
  }

  // SockJS info 엔드포인트 도달 가능성 검사
  async checkSockJSInfo() {
    try {
      const infoUrl = `${this.webSocketUrl}/info`;
      console.log("SockJS info 엔드포인트 확인 중:", infoUrl);

      const response = await fetch(infoUrl);
      const data = await response.json();

      console.log("SockJS info 응답:", data);
      return true;
    } catch (error) {
      console.error("SockJS info 엔드포인트 접근 실패:", error);
      return false;
    }
  }

  async connect(callbacks = {}) {
    if (this.stompClient && this.connected) {
      console.log("WebSocket이 이미 연결되어 있습니다.");
      if (callbacks.onConnected) callbacks.onConnected();
      return this.stompClient;
    }

    console.log("WebSocket 연결 시도:", this.webSocketUrl);

    // 먼저 SockJS info 엔드포인트 확인
    const infoEndpointOk = await this.checkSockJSInfo();
    if (!infoEndpointOk) {
      console.warn(
        "SockJS info 엔드포인트에 접근할 수 없습니다. CORS나 서버 접근성 문제일 수 있습니다."
      );
    }

    try {
      // SockJS 옵션 설정 (transports, server 재연결 등)
      const sockjsOptions = {
        transports: ["websocket", "xhr-streaming", "xhr-polling"],
        debug: true,
      };

      // SockJS 연결 설정
      console.log("SockJS 인스턴스 생성 시작...");

      // 문제 진단을 위해 전체 URL 로깅
      const fullUrl = this.webSocketUrl;
      console.log("SockJS 연결 시도 전체 URL:", fullUrl);

      // CORS 관련 설정 추가
      const headers = {
        Origin: window.location.origin,
      };

      // SockJS 연결 생성
      const socket = new SockJS(this.webSocketUrl, null, sockjsOptions);

      // SockJS 이벤트 핸들러 등록
      socket.onopen = () => console.log("SockJS 소켓 연결됨");
      socket.onclose = (e) => {
        console.log("SockJS 소켓 닫힘 코드:", e.code);
        console.log("SockJS 소켓 닫힘 이유:", e.reason);
        console.log("SockJS 소켓 닫힘 데이터:", e);
      };
      socket.onerror = (e) => console.error("SockJS 소켓 오류:", e);

      // STOMP 클라이언트 생성 방식 수정 - 직접 소켓 전달
      this.stompClient = Stomp.over(socket);

      // 디버그 로그 활성화
      this.stompClient.debug = function (str) {
        console.log("STOMP DEBUG:", str);
      };

      // 재연결 설정
      this.stompClient.reconnect_delay = 5000;

      console.log("STOMP 클라이언트 설정 완료, 연결 시도 중...");

      // 연결 타임아웃 설정
      const connectTimeoutMs = 10000; // 10초
      let connectTimeout;

      // 프로미스 기반 연결 처리
      await new Promise((resolve, reject) => {
        // 타임아웃 설정
        connectTimeout = setTimeout(() => {
          reject(new Error("WebSocket 연결 타임아웃"));
        }, connectTimeoutMs);

        // 연결 시도
        this.stompClient.connect(
          headers,
          () => {
            clearTimeout(connectTimeout);
            console.log("WebSocket 연결 성공 - STOMP Connected");
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
              this.subscribeToChatRoom(
                callbacks.roomId,
                callbacks.onNewMessage
              );
            }

            if (callbacks.onConnected) {
              callbacks.onConnected();
            }

            resolve();
          },
          (error) => {
            clearTimeout(connectTimeout);
            console.error("WebSocket 연결 실패:", error);
            console.error("상세 오류 정보:", {
              message: error.message,
              headers: error.headers,
              body: error.body,
            });
            this.connected = false;

            if (callbacks.onError) {
              callbacks.onError(error);
            }

            reject(error);
          }
        );
      }).catch((error) => {
        console.error("웹소켓 연결 프로미스 에러:", error);
        return null;
      });
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
      console.log("웹소켓으로 메시지 전송 시도:", messageData);

      try {
        // 전송할 대상 경로
        const destination = "/app/chat.sendMessage";
        console.log("STOMP 메시지 대상 경로:", destination);

        // 페이로드 확인
        const payload = JSON.stringify(messageData);
        console.log("STOMP 메시지 페이로드:", payload);

        // 메시지 전송
        this.stompClient.send(destination, {}, payload);

        console.log("웹소켓으로 메시지 전송 완료");
        return true;
      } catch (error) {
        console.error("STOMP 메시지 전송 중 오류 발생:", error);
        return false;
      }
    }
    console.error("웹소켓 연결이 되어있지 않아 메시지를 전송할 수 없습니다.");
    console.log("연결 상태:", this.connected);
    console.log(
      "STOMP 클라이언트:",
      this.stompClient ? "존재함" : "존재하지 않음"
    );
    return false;
  }
}

const webSocketService = new WebSocketService();

export default webSocketService;
