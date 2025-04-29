import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

import '@/assets/styles/global.css'
import '@/assets/styles/text-utilities.css'

const app = createApp(App)

app.use(createPinia())
app.use(router)

app.mount('#app')
