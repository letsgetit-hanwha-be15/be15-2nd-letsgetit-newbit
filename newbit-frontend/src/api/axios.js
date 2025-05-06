/* 요청, 응답에 공통적으로 적용 될 부분을 axios 객체로 정의 */
import axios from "axios";
import { useAuthStore } from "@/features/stores/auth.js";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  headers: { "Content-Type": "application/json" },
  // HttpOnly Cookie 사용하실 경우
  withCredentials : true
});

// 해당 객체를 통해 요청을 하면 interceptor에 의해 access token이 header에 삽입 된다
api.interceptors.request.use(config => {
    const authStore = useAuthStore();
    if(authStore.accessToken)
        config.headers.Authorization = `Bearer ${authStore.accessToken}`
    return config
});

api.interceptors.response.use(
    res => res,
    async err => {
        const authStore = useAuthStore();
        const { config, response } = err;

        if (config.url.includes('/auth/refresh')) {
            // 리프레시 요청에서 401이 오면 바로 로그아웃 처리
            await authStore.clearAuth();
            return Promise.reject(err);
        }

        // 401 에러만 처리
        if (response?.status === 401) {
            // 이미 재시도 한 요청이면 로그아웃 하고 에러 던짐
            if (config._retry) {
                await authStore.clearAuth();
                return Promise.reject(err);
            }
            config._retry = true;

            // 토큰 재발급 시도
            try {
                const refreshRes = await refreshUserToken();
                const newToken = refreshRes.data.data.accessToken;
                authStore.setAuth(newToken);

                // 헤더 갱신 후 원래 요청 재시도
                config.headers.Authorization = `Bearer ${newToken}`;
                return api(config);
            } catch (refreshErr) {
                // 재발급 실패하면 로그아웃
                await authStore.clearAuth();
                return Promise.reject(refreshErr);
            }
        }

        // 그 외 에러는 그대로 던짐
        return Promise.reject(err);
    }
);

export default api;
