export const coffeeletterRoutes = [
    {
        path: '/coffeeletter/:id',
        name : 'Coffeeletter',
        component : () => import('@/features/coffeeletter/views/CoffeeLetterChatView.vue')
    }

]