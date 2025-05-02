export const adminRoutes = [
  {
    path: "/admin",
    component: () => import("@/features/admin/layout/AdminLayout.vue"),
    children: [
      {
        path: "",
        name: "AdminReportList",
        component: () => import("@/features/report/views/ReportListView.vue"),
      },
      {
        path: "post/:id",
        name: "PostReportDetail",
        component: () =>
          import("@/features/report/views/PostReportDetailView.vue"),
      },
      {
        path: "comment/:id",
        name: "CommentReportDetail",
        component: () =>
          import("@/features/report/views/CommentReportDetailView.vue"),
      },
      {
        path: "false-reports",
        name: "AdminFalseReportList",
        component: () =>
          import("@/features/report/views/FalseReportListView.vue"),
      },
      {
        path: "products",
        name: "AdminProductList",
        component: () => import("@/features/admin/views/ProductListView.vue"),
      },
      {
        path: "columns",
        name: "AdminColumnApproval",
        component: () =>
          import("@/features/admin/views/ColumnApprovalView.vue"),
      },
      {
        path: "users",
        name: "AdminUserManage",
        component: () => import("@/features/admin/views/UserManageView.vue"),
      },
    ],
  },
  {
    path: "/reports/dashboard",
    name: "ReportDashboard",
    component: () => import("@/features/admin/views/ReportDashboardView.vue"),
  },
];
