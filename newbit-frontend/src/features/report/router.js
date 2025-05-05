import ReportListView from "./views/ReportListView.vue";
import PostReportDetailView from "./views/PostReportDetailView.vue";
import CommentReportDetailView from "./views/CommentReportDetailView.vue";

export const reportRoutes = [
  {
    path: "/admin/reports",
    name: "ReportList",
    component: ReportListView,
    meta: { requiresAuth: true, requiresAdmin: true },
  },
  {
    path: "/admin/post/:id",
    name: "PostReportDetail",
    component: PostReportDetailView,
    props: true,
    meta: { requiresAuth: true, requiresAdmin: true },
  },
  {
    path: "/admin/comment/:id",
    name: "CommentReportDetail",
    component: CommentReportDetailView,
    props: true,
    meta: { requiresAuth: true, requiresAdmin: true },
  },
];
