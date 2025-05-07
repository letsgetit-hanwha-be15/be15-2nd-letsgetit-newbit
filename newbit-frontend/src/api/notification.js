import api from './axios'
import { EventSourcePolyfill } from 'event-source-polyfill';

/* --- 알림 관련 --- */

// 알림 목록 조회 (초기용)
export const fetchUnreadNotifications = () =>
    api.get('/feature/notification/unread');

// 알림 읽음 처리
export const markAllAsRead = () =>
    api.patch('/feature/notification/read-all');

// SSE 연결
export const connectNotificationSSE = () => {
    const token = localStorage.getItem('accessToken');
    const es = new EventSourcePolyfill('http://localhost:8000/api/v1/feature/notification/subscribe', {
        headers: {
            Authorization: `Bearer ${token}`
        }
    });
    return es;
};