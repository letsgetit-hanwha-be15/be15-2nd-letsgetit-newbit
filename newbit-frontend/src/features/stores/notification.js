import { defineStore } from 'pinia'
import {connectNotificationSSE, fetchUnreadNotifications, markAllAsRead} from '@/api/notification'
import { ref } from 'vue'

export const useNotificationStore = defineStore('notification', () => {
    const notifications = ref([]);      // 전체 알림 목록 (초기 + 실시간)
    const hasNew = ref(false);
    let eventSource = null;

    // 초기 알림 불러오기
    const loadInitialNotifications = async () => {
        try {
            const res = await fetchUnreadNotifications();
            if (res.data.success) {
                notifications.value = res.data.data; // 초기 알림 저장
                hasNew.value = res.data.data.some(n => !n.isRead); // 안읽은 게 있으면 true
            }
        } catch (e) {
            console.error('초기 알림 조회 실패', e);
        }
    };

    // SSE 연결
    const initSSE = (userId) => {
        if (eventSource) return; // 중복 연결 방지

        eventSource = connectNotificationSSE(userId);

        eventSource.onopen = () => {
            console.log('✅ SSE 연결 성공');
        };

        eventSource.onerror = (err) => {
            console.error('❌ SSE 연결 오류', err);
        };

        eventSource.onmessage = (e) => {
            const newNotification = JSON.parse(e.data);
            notifications.value.unshift(newNotification);  // 실시간 알림 추가
            hasNew.value = true;
        };

        eventSource.onerror = (e) => {
            console.error('SSE 오류:', e);
            eventSource.close();
            eventSource = null;
        };
    };

    const markAllRead = async () => {
        try {
            await markAllAsRead();
            notifications.value.forEach(n => n.isRead = true);
            hasNew.value = false;
        } catch (e) {
            console.error('알림 읽음 처리 실패', e);
        }
    };

    const disconnect = () => {
        eventSource?.close();
        eventSource = null;
    };

    return {
        notifications,
        hasNew,
        initSSE,
        loadInitialNotifications,
        markAllRead,
        disconnect
    };
});