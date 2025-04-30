export const reportRoutes = [
    {
        path: '/reports',
        name : 'ReportList',
        component : () => import('@/features/report/views/ReportListView.vue')
    },
    {
        path : '/reports/:id',
        name : 'ReportDetail',
        component : () => import('@/features/report/views/ReportDetailView.vue')
    },
    {
        path : '/reports/dashboard',
        name : 'ReportDashboard',
        component : () => import('@/features/report/views/ReportDashboardView.vue')
    }
]