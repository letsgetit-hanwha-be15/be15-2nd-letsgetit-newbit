export const coffeechatRoutes = [
    {
        path: '/mentors',
        name: 'CoffeechatMain',
        component: () => import('@/features/coffeechat/views/CoffeechatMainView.vue'),
    },
    {
        path: '/mentors/:id/coffeechat-register',
        name: 'CoffeechatRegister',
        component: () => import('@/features/coffeechat/views/CoffeechatRegisterView.vue'),
        meta: { requiresAuth: true },
    }
];