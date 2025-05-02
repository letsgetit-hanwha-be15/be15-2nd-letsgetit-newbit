import { createRouter, createWebHistory } from "vue-router";
import { mainRoutes } from "@/features/main/router.js";
import { coffeeletterRoutes } from "@/features/coffeeletter/router.js";
import { coffeechatRoutes } from "@/features/coffeechat/router.js";
import { paymentRoutes } from "@/features/payment/router.js";
import { productRoutes } from "@/features/product/router.js";
import { reportRoutes } from "@/features/report/router.js";
import { adminRoutes } from "@/features/admin/router.js";
import { perkRoutes } from "@/features/perk/router.js";
import DefaultLayout from "@/components/layout/DefaultLayout.vue";
import {mypageRoutes} from "@/features/mypage/router.js";
import {columnRoutes} from "@/features/column/router.js";
import PostListView from '@/features/post/views/PostListView.vue'


export const postRoutes = [
  {
    path: '/posts',
    name: 'PostList',
    component: PostListView,
  },
  {
    path: '/posts/category/:categoryId',
    name: 'PostByCategory',
    component: PostListView,
    props: true
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      component: DefaultLayout,
      children: [
        ...mainRoutes,
        ...coffeechatRoutes,
        ...coffeeletterRoutes,
        ...paymentRoutes,
        ...productRoutes,
        ...reportRoutes,
        ...adminRoutes,
        ...perkRoutes,
        ...postRoutes,
        ...columnRoutes,
        ...profileRouters,
      ],
    },
    ...mypageRoutes,
  ],
});

export default router;
