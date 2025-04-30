import { createRouter, createWebHistory } from 'vue-router'
import {mainRoutes} from "@/features/main/router.js";
import DefaultLayout from "@/components/layout/DefaultLayout.vue";
import {mypageRoutes} from "@/features/mypage/router.js";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: DefaultLayout,
      children: [
        ...mainRoutes,
      ]
    },
    ...mypageRoutes
  ],
})

export default router
