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
    }
];