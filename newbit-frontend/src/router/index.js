import { createRouter, createWebHistory } from 'vue-router'
import {mainRoutes} from "@/features/main/router.js";
import DefaultLayout from "@/components/layout/DefaultLayout.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: DefaultLayout,
      children: [
        ...mainRoutes,
      ]
    }
  ],
})

export default router
