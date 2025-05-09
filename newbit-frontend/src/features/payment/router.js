export const paymentRoutes = [
  {
    path: "/payments/success",
    name: "success",
    component: () => import("@/features/payment/views/SuccessView.vue"),
    meta: { requiresAuth: true },
  },
  {
    path: "/payments/fail",
    name: "fail",
    component: () => import("@/features/payment/views/FailView.vue"),
    meta: { requiresAuth: true },
  },
];
