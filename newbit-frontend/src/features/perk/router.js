export const perkRoutes = [
    {
        path: '/perks',
        name : 'PerkList',
        component : () => import('@/features/perk/views/PerkListView.vue')
    },
    {
        path : '/perks/quiz',
        name : 'PersonalQuiz',
        component : () => import('@/features/perk/views/PersonalQuizView.vue')
    },
    {
        path : '/perks/quiz-free',
        name : 'PersonalQuiz',
        component : () => import('@/features/perk/views/FreePersonalQuizView.vue')
    }
]