import { defineStore } from 'pinia'
import { connectNotificationSSE } from '@/api/notification'
import {ref} from "vue";

export const useNotificationStore = defineStore('notification', () => {
    const notifications = ref([]);
    const hasNew = ref(false);
    let eventSource = null;

    const initSSE = (userId) => {
        eventSource = connectNotificationSSE(userId);
        eventSource.onmessage = (e) => {
            const n = JSON.parse(e.data);
            notifications.value.unshift(n);
            hasNew.value = true;
        };
        eventSource.onerror = () => {
            eventSource.close();
            console.error('SSE 연결 실패');
        };
    };

    const disconnect = () => {
        eventSource?.close();
    };

    return { notifications, hasNew, initSSE, disconnect };
});