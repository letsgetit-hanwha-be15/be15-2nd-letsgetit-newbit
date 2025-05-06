import { defineStore } from "pinia";
import { computed, ref } from "vue";

export const useAuthStore = defineStore('auth', () => {
    const accessToken = ref(null);
    const userRole = ref(null);
    const expirationTime = ref(null);

    // ✅ 인증 여부를 확인하는 getter
    const isAuthenticated = computed(() =>
        !!accessToken.value && Date.now() < (expirationTime.value || 0)
    );

    // ✅ 토큰 설정 + payload 파싱 + localStorage 저장
    function setAuth(at) {
        accessToken.value = at;
        try {
            const payload = JSON.parse(atob(at.split('.')[1]));
            console.log('payload', payload);
            userRole.value = payload.authority;
            expirationTime.value = payload.exp * 1000;
            localStorage.setItem('accessToken', at); // 로컬 스토리지 저장
        } catch (e) {
            accessToken.value = null;
            userRole.value = null;
            expirationTime.value = null;
            localStorage.removeItem('accessToken'); // 에러 발생 시 제거
        }
    }

    // ✅ 로그아웃 시 상태 및 로컬스토리지 모두 초기화
    function clearAuth() {
        accessToken.value = null;
        userRole.value = null;
        expirationTime.value = null;
        localStorage.removeItem('accessToken'); // 로컬스토리지 제거
        localStorage.removeItem('refreshToken'); // 로컬스토리지 제거
    }

    return {
        accessToken,
        userRole,
        expirationTime,
        isAuthenticated,
        setAuth,
        clearAuth
    };
});
