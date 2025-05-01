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
      },
      {
        path: "new",
        name: "ProductCreate",
        component: () =>
          import("@/features/product/views/ProductCreateView.vue"),
      },
      {
        path: ":id/edit",
        name: "ProductEdit",
        component: () => import("@/features/product/views/ProductEditView.vue"),
      },
      {
        path: "manage",
        name: "ProductManage",
        component: () =>
          import("@/features/product/views/ProductManageView.vue"),
      },
      {
        path: "payment",
        name: "ProductPayment",
        component: ProductPaymentView,
      },
    ],
  },
];
