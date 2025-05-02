import { createRouter, createWebHistory } from "vue-router";
import { mainRoutes } from "@/features/main/router.js";
import { coffeeletterRoutes } from "@/features/coffeeletter/router.js";
import { paymentRoutes } from "@/features/payment/router.js";
import { productRoutes } from "@/features/product/router.js";
import { reportRoutes } from "@/features/report/router.js";
import { perkRoutes } from "@/features/perk/router.js";
import DefaultLayout from "@/components/layout/DefaultLayout.vue";
import {mypageRoutes} from "@/features/mypage/router.js";
import {postRoutes} from "@/features/post/router.js";
import {columnRoutes} from "@/features/column/router.js";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: DefaultLayout,
      children: [
        ...mainRoutes,
        ...coffeeletterRoutes,
        ...paymentRoutes,
        ...productRoutes,
        ...reportRoutes,
        ...perkRoutes,
        ...postRoutes,
        ...columnRoutes,
      ],
    },
    ...mypageRoutes
  ],
});

export default router;
