import { defineStore } from "pinia";
import { computed, ref } from "vue";

export const useAuthStore = defineStore('auth', () => {
    const accessToken = ref(null);
    const userRole = ref(null);
    const userId = ref(null); // ✅ 추가
    const expirationTime = ref(null);

    const isAuthenticated = computed(() =>
        !!accessToken.value && Date.now() < (expirationTime.value || 0)
    );

    function setAuth(at) {
        accessToken.value = at;
        try {
            const payload = JSON.parse(atob(at.split('.')[1]));
            console.log('payload', payload);
            userRole.value = payload.authority;
            userId.value = payload.userId; // ✅ 반드시 추가
            expirationTime.value = payload.exp * 1000;
            localStorage.setItem('accessToken', at);
        } catch (e) {
            accessToken.value = null;
            userRole.value = null;
            userId.value = null;
            expirationTime.value = null;
            localStorage.removeItem('accessToken');
        }
    }

    function clearAuth() {
        accessToken.value = null;
        userRole.value = null;
        userId.value = null; // ✅ 초기화도 포함
        expirationTime.value = null;
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
    }

    return {
        accessToken,
        userRole,
        userId,           // ✅ 꼭 반환
        expirationTime,
        isAuthenticated,
        setAuth,
        clearAuth
    };
});
