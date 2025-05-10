export const columnRoutes = [
    {
        path: "/columns",
        name: "ColumnList",
        component: () => import("@/features/column/views/ColumnListView.vue"),
    },
    {
        path: "/columns/requests",
        name: "ColumnRequestPage",
        component: () => import("@/features/column/views/ColumnRequestView.vue"),
        meta: { requiresAuth: true },
    },
    {
        path: "/columns/edit/:id",
        name: "ColumnEditRequestPage",
        component: () => import("@/features/column/views/ColumnEditRequestView.vue"),
        props: true,
        meta: { requiresAuth: true },
    },
    {
        path: "/columns/:id",
        name: "ColumnDetail",
        component: () => import("@/features/column/views/ColumnDetailView.vue"),
        props: true,
        meta: { requiresAuth: true },
    },
    {
        path: "/series",
        name: "SeriesListView",
        component: () => import("@/features/column/views/SeriesListView.vue")
    },
    {
        path: '/series/:id',
        name: 'SeriesDetail',
        component: () => import('@/features/column/views/SeriesDetailView.vue'),
    },
];