export const columnRoutes = [
    {
        path: "/columns",
        name: "ColumnList",
        component: () => import("@/features/column/views/ColumnListView.vue"),
    },
    {
        path: "/columns/:id",
        name: "ColumnDetail",
        component: () => import("@/features/column/views/ColumnDetailView.vue"),
        props: true
    },
    {
        path: "/columns/requests",
        name: "ColumnRequestPage",
        component: () => import("@/features/column/views/ColumnRequestView.vue")
    },
    {
        path: "/columns/edit/:id",
        name: "ColumnEditRequestPage",
        component: () => import("@/features/column/views/ColumnEditRequestView.vue"),
        props: true,
    },
];