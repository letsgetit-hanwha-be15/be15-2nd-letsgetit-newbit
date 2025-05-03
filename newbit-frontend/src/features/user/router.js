export const userRoutes = [
    {
        path: '/signup',
        name: 'SignUp',
        component: () => import('@/features/user/views/SignUpView.vue')
    },
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/features/user/views/LoginView.vue')
    },
];