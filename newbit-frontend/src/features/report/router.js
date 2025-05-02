export const reportRoutes = [
  {
    path: "/admin",
    component: () => import("@/features/report/layout/AdminLayout.vue"),
    children: [
      {
        path: "",
        name: "AdminReportList",
        component: () => import("@/features/report/views/ReportListView.vue"),
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
        component: () => import("@/features/report/views/ProductListView.vue"),
      },
      {
        path: "columns",
        name: "AdminColumnApproval",
        component: () =>
          import("@/features/report/views/ColumnApprovalView.vue"),
      },
      {
        path: "users",
        name: "AdminUserManage",
        component: () => import("@/features/report/views/UserManageView.vue"),
      },
    ],
  },
  {
    path: "/reports",
    name: "ReportList",
    component: () => import("@/features/report/views/ReportListView.vue"),
  },
  {
    path: "/reports/:id",
    name: "ReportDetail",
    component: () => import("@/features/report/views/ReportDetailView.vue"),
  },
  {
    path: "/reports/dashboard",
    name: "ReportDashboard",
    component: () => import("@/features/report/views/ReportDashboardView.vue"),
  },
];
