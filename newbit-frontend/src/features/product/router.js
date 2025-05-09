import ProductListView from "./views/ProductListView.vue";
import ProductPaymentView from "./views/ProductPaymentView.vue";

export const productRoutes = [
  {
    path: "/products",
    children: [
      {
        path: "",
        name: "ProductList",
        component: ProductListView,
        meta: { requiresAuth: true },
      },
      {
        path: "new",
        name: "ProductCreate",
        component: () =>
          import("@/features/product/views/ProductCreateView.vue"),
        meta: { requiresAuth: true, requiresAdmin: true },
      },
      {
        path: ":id/edit",
        name: "ProductEdit",
        component: () => import("@/features/product/views/ProductEditView.vue"),
        meta: { requiresAuth: true, requiresAdmin: true },
      },
      {
        path: "manage",
        name: "ProductManage",
        component: () =>
          import("@/features/product/views/ProductManageView.vue"),
        meta: { requiresAuth: true, requiresAdmin: true },
      },
      {
        path: "payment",
        name: "ProductPayment",
        component: ProductPaymentView,
        meta: { requiresAuth: true },
      },
    ],
  },
];
