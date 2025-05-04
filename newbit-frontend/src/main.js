import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

import '@/assets/styles/global.css'
import '@/assets/styles/text-utilities.css'

import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

import Toast from 'vue-toastification'
import 'vue-toastification/dist/index.css'

async function bootstrap() {
    const app = createApp(App)

    app.use(createPinia())
    app.use(router)
    app.use(ElementPlus)
    app.use(Toast)

    app.mount('#app')
}

bootstrap()
