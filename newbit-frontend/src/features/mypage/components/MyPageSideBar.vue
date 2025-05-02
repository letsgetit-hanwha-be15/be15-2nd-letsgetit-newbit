<script setup>
import SideBarItem from '@/features/mypage/components/SideBarItem.vue'
import {computed} from "vue";
const props = defineProps({
  authority: {
    type: String,
    required: true
  }
})

const sidebarSections = computed(() => {
  const commonSections = [
    {
      title: '마이페이지',
      items: [{ text: '프로필', to: '/mypage/profile/edit' }]
    },
    {
      title: '내 콘텐츠',
      items: [
        { text: '작성한 게시글', to: '/mypage/contents?type=posts' },
        { text: '좋아요한 콘텐츠', to: '/mypage/contents?type=likes' },
        { text: '구매한 칼럼', to: '/mypage/contents?type=purchased-columns' },
        { text: '구독한 시리즈', to: '/mypage/contents?type=subscribed-series' },
      ]
    },
    {
      title: '커피챗',
      items: [
        { text: '커피챗 내역', to: '/mypage/history?type=coffeechat' },
        { text: '내 리뷰', to: '/mypage/reviews' },
      ]
    },
    {
      title: '활동 내역',
      items: [
        { text: '포인트 내역', to: '/mypage/history?type=point' },
        { text: '다이아 내역', to: '/mypage/history?type=diamond' },
        { text: '결제 내역', to: '/mypage/history?type=payment' },
      ]
    },
    {
      title: '설정',
      items: [{ text: '계정 정보', to: '/mypage/account' }]
    },
  ]

  const mentorSections = [
    {
      title: '멘토 콘텐츠',
      items: [
        { text: '멘토 프로필', to: '/mypage/mentor/edit' },
        { text: '작성한 칼럼', to: '/mypage/mentor/columns' },
        { text: '내 시리즈 관리', to: '/mypage/mentor/series' },
        { text: '커피챗 관리', to: '/mypage/mentor/coffeechats' },
        { text: '판매 내역', to: '/mypage/history?type=sale' },
        { text: '월별 정산 내역', to: '/mypage/history?type=settlement' },
        { text: '칼럼 요청 내역', to: '/mypage/mentor/column-requests' }
      ]
    }
  ]

  return props.authority !== 'MENTOR'
      ? [...commonSections.slice(0, 1), ...mentorSections, ...commonSections.slice(1)]
      : commonSections
})
</script>

<template>
  <aside class="sidebar">
    <div v-for="section in sidebarSections" :key="section.title" class="section">
      <div class="section-title text-13px-regular">{{ section.title }}</div>
      <ul class="item-list">
        <SideBarItem
            v-for="item in section.items"
            :key="item.text"
            :to="item.to"
            :text="item.text"
        />
      </ul>
    </div>
  </aside>
</template>

<style scoped>
.sidebar {
  width: 214px;
  border: 1px solid var(--newbitnormal);
  border-radius: 8px;
  padding: 24px;
  background-color: var(--newbitbackground);
}

.section {
  margin-bottom: 32px;
}

.section-title {
  color: var(--newbitgray);
  margin-bottom: 12px;
}

.item-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
</style>