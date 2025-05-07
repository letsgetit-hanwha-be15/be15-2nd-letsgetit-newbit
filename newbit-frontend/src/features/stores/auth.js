import { defineStore } from "pinia";
import { computed, ref } from "vue";

function decodeJwtPayload(token) {
    const payload = token.split('.')[1];
    const decoded = atob(payload);
    const utf8Payload = new TextDecoder().decode(Uint8Array.from(decoded, c => c.charCodeAt(0)));
    return JSON.parse(utf8Payload);
}

export const useAuthStore = defineStore('auth', () => {
    const accessToken = ref(null);
    const userRole = ref(null);
    const userId = ref(null);
    const expirationTime = ref(null);

    // ✅ 추가 상태
    const nickname = ref(null);
    const profileImageUrl = ref(null);
    const point = ref(0);
    const diamond = ref(0);
    const mentorId = ref(null);

    const isAuthenticated = computed(() =>
        !!accessToken.value && Date.now() < (expirationTime.value || 0)
    );

    function updateBalance(newPoint, newDiamond) {
        point.value = newPoint;
        diamond.value = newDiamond;
    }

    function setAuth(at) {
        accessToken.value = at;
        try {
            const payload = decodeJwtPayload(at);
            console.log('payload', payload);
            userRole.value = payload.authority;
            userId.value = payload.userId;
            nickname.value = payload.nickname || null;
            profileImageUrl.value = payload.profileImageUrl || null;
            mentorId.value = payload.mentorId || null;
            expirationTime.value = payload.exp * 1000;

            localStorage.setItem('accessToken', at);
        } catch (e) {
            clearAuth();
        }
    }

    function clearAuth() {
        accessToken.value = null;
        userRole.value = null;
        userId.value = null;
        nickname.value = null;
        profileImageUrl.value = null;
        point.value = null;
        diamond.value = null;
        mentorId.value = null;
        expirationTime.value = null;

        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
    }

    return {
        accessToken,
        userRole,
        userId,
        nickname,
        profileImageUrl,
        point,
        diamond,
        mentorId,
        expirationTime,
        isAuthenticated,
        setAuth,
        clearAuth,
        updateBalance,

        pointValue: computed(() => point.value),
        diamondValue: computed(() => diamond.value),
    };
});
