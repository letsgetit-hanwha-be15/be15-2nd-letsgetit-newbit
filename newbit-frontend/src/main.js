import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import { useAuthStore } from '@/features/stores/auth.js'
import { refreshUserToken } from '@/api/user.js'

import '@/assets/styles/global.css'
import '@/assets/styles/text-utilities.css'

import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

import Toast from 'vue-toastification'
import 'vue-toastification/dist/index.css'

async function bootstrap() {
    const app = createApp(App)

    const pinia = createPinia()
    app.use(pinia)

    const authStore = useAuthStore()

    const savedToken = localStorage.getItem('accessToken')
    if (savedToken) {
        authStore.setAuth(savedToken)
        console.log('초기화: 로컬스토리지에서 토큰 복원됨')
    } else {
        try {
            const resp = await refreshUserToken()
            const token = resp.data.data.accessToken
            authStore.setAuth(token)
            console.log('초기화: 서버에서 토큰 재발급')
        } catch (e) {
            console.log('초기화: 로그인 안 된 상태 유지')
        }
    }

    app.use(router)
    app.use(ElementPlus)
    app.use(Toast)

    app.mount('#app')
}

bootstrap()
