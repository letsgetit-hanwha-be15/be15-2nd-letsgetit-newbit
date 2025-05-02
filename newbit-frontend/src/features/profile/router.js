export const profileRouters = [
    {
        path: '/profile/mentor/:id',
        name: 'MentorDetail',
        component: () => import('@/features/profile/views/MentorProfileDetailView.vue')
    },
    {
        path: '/profile/user/:id',
        name: 'UserDetail',
        component: () => import('@/features/profile/views/UserProfileDetailView.vue')
    }
];