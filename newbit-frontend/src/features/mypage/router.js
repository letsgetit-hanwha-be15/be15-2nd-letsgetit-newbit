import MyPageLayout from "@/components/layout/MyPageLayout.vue";

import MyProfileEditView from "./views/MyProfileEditView.vue";
import MentorProfileEditView from "./views/MentorProfileEditView.vue";
import AccountEditView from "./views/AccountEditView.vue";
import LikedContentsView from "./views/LikedContentsView.vue";
import PointHistoryView from "./views/PointHistoryView.vue";
import CoffeechatDetailMenteeView from "@/features/mypage/views/CoffeechatDetailMenteeView.vue";
import CoffeechatListMenteeView from "@/features/mypage/views/CoffeechatListMenteeView.vue";
import ReviewRegisterView from "@/features/mypage/views/ReviewRegisterView.vue";
import DiamondHistoryView from "@/features/mypage/views/DiamondHistoryView.vue";
import SaleHistoryView from "@/features/mypage/views/SaleHistoryView.vue";
import PurchasedColumnsView from "@/features/mypage/views/PurchasedColumnsView.vue";
import AccountDeleteView from "@/features/mypage/views/AccountDeleteView.vue";
import MonthlySettlementHistoryView from "@/features/mypage/views/MonthlySettlementHistoryView.vue";
import MyPostsView from "@/features/mypage/views/MyPostsView.vue";
import SubscribedSeriesView from "@/features/mypage/views/SubscribedSeriesView.vue";
import MyColumnsView from "@/features/mypage/views/MyColumnsView.vue";
import MySeriesView from "@/features/mypage/views/MySeriesView.vue";
import CoffeechatListMentorView from "@/features/mypage/views/CoffeechatListMentorView.vue";
import CoffeechatCancelView from "@/features/mypage/views/CoffeechatCancelView.vue";
import CoffeechatDetailMentorView from "@/features/mypage/views/CoffeechatDetailMentorView.vue";

const token = localStorage.getItem("accessToken");

export const mypageRoutes = [
    {
        path: "/mypage",
        component: MyPageLayout,
        meta: { requiresAuth: true },
        children: [
            {
                path: "",
                redirect: {name: "MyProfileEdit"}, // 기본 진입 시 프로필 페이지로
            },
            {
                path: "profile/edit",
                name: "MyProfileEdit",
                component: MyProfileEditView,
            },
            {
                path: "mentor/edit",
                name: "MentorProfileEdit",
                component: MentorProfileEditView,
            },
            {
                path: "mentor/columns",
                name: "MyColumns",
                component: MyColumnsView,
            },
            {
                path: "mentor/series",
                name: "MySeries",
                component: MySeriesView,
            },
            {
                path: "account",
                name: "AccountEdit",
                component: AccountEditView,
            },
            {
                path: "contents/like",
                name: "LikedContents",
                component: LikedContentsView,
            },
            {
                path: "history/point",
                name: "PointHistory",
                component: PointHistoryView,
            },
            {
                path: "history/diamond",
                name: "DiamondHistory",
                component: DiamondHistoryView,
            },
            {
                path: "history/sale",
                name: "SaleHistory",
                component: SaleHistoryView,
            },
            {
                path: "history/settlement",
                name: "MonthlySettlementHistory",
                component: MonthlySettlementHistoryView,
            },
            {
                path: "history/coffeechats",
                name: "CoffeechatListMentee",
                component: CoffeechatListMenteeView,
            },
            {
                path: "mentor/coffeechats",
                name: "CoffeechatListMentor",
                component: CoffeechatListMentorView,
            },
            {
                path: "history/coffeechats/:id",
                name: "CoffeechatDetailMentee",
                component: CoffeechatDetailMenteeView,
            },
            {
                path: "mentor/coffeechats/:id",
                name: "CoffeechatDetailMentor",
                component: CoffeechatDetailMentorView,
            },
            {
                path: "history/coffeechats/:id/review",
                name: "ReviewRegister",
                component: ReviewRegisterView,
            },
            {
                path: "history/coffeechats/:id/cancel",
                name: "CoffeechatCancel",
                component: CoffeechatCancelView,
            },
            {
                path: "contents/purchased-columns",
                name: "PurchasedColumns",
                component: PurchasedColumnsView,
            },
            {
                path: "contents/posts",
                name: "MyPosts",
                component: MyPostsView,
            },
            {
                path: "contents/subscribe",
                name: "SubscribedSeries",
                component: SubscribedSeriesView,
            },
            {
                path: "account/delete",
                name: "AccountDelete",
                component: AccountDeleteView,
            },
            {
                path: "/mypage/mentor/column-requests",
                name: "ColumnRequestHistoryView",
                component: () =>
                    import("@/features/mypage/views/ColumnRequestHistoryView.vue"),
            },
            {
                path: "/mypage/history/payment",
                name: "PurchaseHistory",
                component: () => import("@/features/mypage/views/PurchaseHistory.vue"),
            },
        ],
    },
];
