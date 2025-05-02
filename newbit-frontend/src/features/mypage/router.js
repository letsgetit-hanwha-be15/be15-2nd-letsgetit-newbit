import MyPageLayout from "@/components/layout/MyPageLayout.vue"

import MyProfileEditView from './views/MyProfileEditView.vue'
import MentorProfileEditView from './views/MentorProfileEditView.vue'
import AccountEditView from './views/AccountEditView.vue'
import MyContentsView from './views/MyContentsView.vue'
import MyHistoryView from './views/MyHistoryView.vue'
import MyReviewsView from './views/MyReviewsView.vue'
import CoffeechatDetailView from "@/features/mypage/views/CoffeechatDetailView.vue";
import CoffeechatListView from "@/features/mypage/views/CoffeechatListView.vue";
import ReviewRegisterView from "@/features/mypage/views/ReviewRegisterView.vue";

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
                path: 'history',
                name: 'MyHistory',
                component: MyHistoryView,
            },
            {
                path: 'reviews',
                name: 'MyReviews',
                component: MyReviewsView,
            },
            {
                path: 'coffeechats',
                name: 'CoffeechatList',
                component: CoffeechatListView,
            },
            {
                path: 'coffeechats/:id',
                name: 'CoffeechatDetail',
                component: CoffeechatDetailView,
            },
            {
                path: 'coffeechats/:id/review',
                name: 'ReviewRegister',
                component: ReviewRegisterView,
            },
        ]
    }
]