import { createRouter, createWebHistory } from "vue-router";
import { mainRoutes } from "@/features/main/router.js";
import { coffeeletterRoutes } from "@/features/coffeeletter/router.js";
import { coffeechatRoutes } from "@/features/coffeechat/router.js";
import { paymentRoutes } from "@/features/payment/router.js";
import { productRoutes } from "@/features/product/router.js";
import { reportRoutes } from "@/features/report/router.js";
import { perkRoutes } from "@/features/perk/router.js";
import DefaultLayout from "@/components/layout/DefaultLayout.vue";
import { mypageRoutes } from "@/features/mypage/router.js";
import { columnRoutes } from "@/features/column/router.js";
import { profileRouters } from "@/features/profile/router.js";
import { postRoutes } from "@/features/post/router.js";
import { userRoutes } from "@/features/user/router.js";
import { adminRoutes } from "@/features/admin/router.js";
import { useAuthStore } from "@/features/stores/auth";

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
        ...perkRoutes,
        ...postRoutes,
        ...columnRoutes,
        ...profileRouters,
        ...userRoutes,

        ...adminRoutes,
      ],
    },
    ...mypageRoutes,
  ],
});

// 전역 가드 (라우팅 전 확인하여 라우팅 여부 결정 가능)
router.beforeEach((to) => {
  const authStore = useAuthStore(); // 이 위치에서 호출 가능함 (라우터가 등록된 이후로)

  if (to.name === "PersonalQuiz") {
    const perkPurchased = localStorage.getItem("perk_color_quiz_purchased");
    if (!perkPurchased) {
      return { name: "PerkList" };
    }
  }

  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    return {
      name: "login",
      query: { redirect: to.fullPath },
    };
  }

  if (
    (to.name === "login" || to.name === "register") &&
    authStore.isAuthenticated
  ) {
    return { name: "main" };
  }

  return true; // 명시적으로 통과 처리
});

export default router;
