export const userRoutes = [
    {
        path: '/signup',
        name: 'SignUp',
        component: () => import('@/features/user/views/SignUpView.vue')
    }
];