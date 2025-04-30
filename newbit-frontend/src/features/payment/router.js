export const paymentRoutes = [
  {
    path: "/payments",
    name: "payment",
    component: () => import("@/features/payment/views/PaymentView.vue"),
  },
  {
    path: "/payments/success",
    name: "success",
    component: () => import("@/features/payment/views/SuccessView.vue"),
  },
  {
    path: "/payments/failed",
    name: "failed",
    component: () => import("@/features/payment/views/FailedView.vue"),
  },
];
