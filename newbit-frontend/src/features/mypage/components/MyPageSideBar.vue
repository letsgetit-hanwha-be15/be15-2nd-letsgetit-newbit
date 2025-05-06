<template>
  <div class="flex flex-col gap-5">
    <aside class="w-[214px] border border-[--newbitnormal] rounded-[8px] p-6 bg-[--newbitbackground] shrink-0">
      <div
          v-for="section in sidebarSections"
          :key="section.title"
          class="mb-8"
      >
        <div class="text-13px-regular text-[--newbitgray] mb-3">
          {{ section.title }}
        </div>
        <ul class="list-none p-0 m-0">
          <SideBarItem
              v-for="item in section.items"
              :key="item.text"
              :to="item.to"
              :text="item.text"
          />
        </ul>
      </div>
    </aside>
    <div
        class="text-gray-400 text-13px-regular cursor-pointer hover:underline"
        @click="$router.push('/mypage/account/delete')"
    >
      회원 탈퇴
    </div>
  </div>

</template>

<script setup>
import SideBarItem from '@/features/mypage/components/SideBarItem.vue'
import { computed } from 'vue'

import { useRouter } from 'vue-router';
const router = useRouter();

const props = defineProps({
  authority: {
    type: String,
    required: true,
  },
})

const sidebarSections = computed(() => {
  const commonSections = [
    {
      title: '마이페이지',
      items: [{ text: '프로필', to: '/mypage/profile/edit' }],
    },
    {
      title: '내 콘텐츠',
      items: [
        { text: '작성한 게시글', to: '/mypage/contents/posts' },
        { text: '좋아요한 콘텐츠', to: '/mypage/contents/like' },
        { text: '구매한 칼럼', to: '/mypage/contents/purchased-columns' },
        { text: '구독한 시리즈', to: '/mypage/contents/subscribe' },
      ],
    },
    {
      title: '활동 내역',
      items: [
        { text: '포인트 내역', to: '/mypage/history/point' },
        { text: '다이아 내역', to: '/mypage/history/diamond' },
        { text: '결제 내역', to: '/mypage/history/payment' },
        { text: '커피챗 내역', to: '/mypage/history/coffeechats' },
      ],
    },
    {
      title: '설정',
      items: [{ text: '계정 정보', to: '/mypage/account' }],
    },
  ]

  const mentorSections = [
    {
      title: '멘토 콘텐츠',
      items: [
        { text: '멘토 프로필', to: '/mypage/mentor/edit' },
        { text: '작성한 칼럼', to: '/mypage/mentor/my-columns' },
        { text: '내 시리즈 관리', to: '/mypage/mentor/series' },
        { text: '커피챗 관리', to: '/mypage/mentor/coffeechats' },
        { text: '판매 내역', to: '/mypage/history/sale' },
        { text: '월별 정산 내역', to: '/mypage/history/settlement' },
        { text: '칼럼 요청 내역', to: '/mypage/mentor/column-requests' },
      ],
    },
  ]

  return props.authority !== 'MENTOR'
      ? [...commonSections.slice(0, 1), ...mentorSections, ...commonSections.slice(1)]
      : commonSections
})
</script>