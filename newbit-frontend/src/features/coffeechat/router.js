export const coffeechatRoutes = [
    {
        path: '/coffeechats',
        name: 'CoffeechatMain',
        component: () => import('@/features/coffeechat/views/CoffeechatMainView.vue')
    },
    {
        path: '/coffeechats/register',
        name: 'CoffeechatRegister',
        component: () => import('@/features/coffeechat/views/CoffeechatRegisterView.vue')
    }
];