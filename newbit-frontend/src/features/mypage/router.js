import MyPageLayout from "@/components/layout/MyPageLayout.vue"

import MyProfileEditView from './views/MyProfileEditView.vue'
import MentorProfileEditView from './views/MentorProfileEditView.vue'
import AccountEditView from './views/AccountEditView.vue'
import MyContentsView from './views/MyContentsView.vue'
import PointHistoryView from './views/PointHistoryView.vue'
import MyReviewsView from './views/MyReviewsView.vue'

export const mypageRoutes = [
    {
        path: '/mypage',
        component: MyPageLayout,
        children: [
            {
                path: '',
                redirect: { name: 'MyProfileEdit' } // 기본 진입 시 프로필 페이지로
            },
            {
                path: 'profile/edit',
                name: 'MyProfileEdit',
                component: MyProfileEditView,
            },
            {
                path: 'mentor/edit',
                name: 'MentorProfileEdit',
                component: MentorProfileEditView,
            },
            {
                path: 'account',
                name: 'AccountEdit',
                component: AccountEditView,
            },
            {
                path: 'contents',
                name: 'MyContents',
                component: MyContentsView,
            },
            {
                path: 'history/point',
                name: 'PointHistory',
                component: PointHistoryView,
            },
            {
                path: 'history/diamond',
                name: 'DiamondHistory',
                component: PointHistoryView,
            },
            {
                path: 'reviews',
                name: 'MyReviews',
                component: MyReviewsView,
            },
        ]
    }
]