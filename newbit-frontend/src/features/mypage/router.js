import MyPageLayout from "@/components/layout/MyPageLayout.vue"

import MyProfileEditView from './views/MyProfileEditView.vue'
import MentorProfileEditView from './views/MentorProfileEditView.vue'
import AccountEditView from './views/AccountEditView.vue'
import LikedContentsView from './views/LikedContentsView.vue'
import PointHistoryView from './views/PointHistoryView.vue'
import MyReviewsView from './views/MyReviewsView.vue'
import CoffeechatDetailView from "@/features/mypage/views/CoffeechatDetailView.vue";
import CoffeechatListView from "@/features/mypage/views/CoffeechatListView.vue";
import ReviewRegisterView from "@/features/mypage/views/ReviewRegisterView.vue";
import DiamondHistoryView from "@/features/mypage/views/DiamondHistoryView.vue";
import SaleHistoryView from "@/features/mypage/views/SaleHistoryView.vue";

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
                path: 'contents/like',
                name: 'LikedContents',
                component: LikedContentsView,
            },
            {
                path: 'history/point',
                name: 'PointHistory',
                component: PointHistoryView,
            },
            {
                path: 'history/diamond',
                name: 'DiamondHistory',
                component: DiamondHistoryView,
            },
            {
                path: 'history/sale',
                name: 'SaleHistory',
                component: SaleHistoryView,
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
            {
                path: '/mypage/mentor/column-requests',
                name: 'ColumnRequestHistoryView',
                component: () => import('@/features/mypage/views/ColumnRequestHistoryView.vue')
            }
        ]
    }
]