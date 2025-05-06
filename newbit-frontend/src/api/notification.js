import api from './axios'

/* --- 알림 관련 --- */

// 알림 목록 조회 (초기용)
export const fetchUnreadNotifications = () =>
    api.get('/feature/notification/unread');

// 알림 읽음 처리
export const markAllAsRead = () =>
    api.patch('/feature/notification/mark-all-read');

// SSE 연결
export const connectNotificationSSE = (userId) => {
    const es = new EventSource(`/api/v1/feature/notification/subscribe?userId=${userId}`);
    return es;
};