export const paymentRoutes = [
    {
        path: '/payments/success',
        name : 'success',
        component : () => import('@/features/payment/views/SuccessView.vue')
    },
    {
        path : '/payments/failed',
        name : 'failed',
        component : () => import('@/features/payment/views/FailedView.vue')
    }

]