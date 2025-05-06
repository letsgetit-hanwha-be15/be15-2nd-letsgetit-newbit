<script setup>
import { ref } from 'vue'
import MentorProfileSideBar from '@/features/profile/components/MentorProfileSideBar.vue'
import MentorProfileTabBar from '@/features/profile/components/MentorProfileTabBar.vue'
import PagingBar from '@/components/common/PagingBar.vue'
import profileImage from '@/assets/image/default-profile.png'
import ColumnTab from "@/features/profile/components/ColumnTab.vue";
import SeriesTab from "@/features/profile/components/SeriesTab.vue";
import PostTab from "@/features/profile/components/PostTab.vue";
import ReviewTab from "@/features/profile/components/ReviewTab.vue";

// 로그인한 내 ID (임시)
const myId = 1

// 유저 정보 (API 연동 전용 Mock)
const user = ref({
  id: 1,
  profileImageUrl: profileImage,
  nickname: 'sezeme',
  jobName: '백엔드',
  temperature: 100,
  price: 50,
  preferredTime: '7시 이후 좋아요! 2시간 이하로 신청해주세요!',
  externalLinkUrl: 'https://example.com',
  introduction: '안녕하세요! 반갑습니다! 잘 부탁드립니다. 반갑습니다. 잘 부탁드립니다. 반갑스빈다.',
  isActive: true
})

// 내 프로필인지 여부
const isMyProfile = ref(user.value.id === myId)

// 페이징 상태
const currentPage = ref(1)
const totalPages = ref(5)

function handlePageChange(page) {
  currentPage.value = page
  // 👉 여기에 데이터 로딩 또는 API 호출 연결 가능
}

const selectedTab = ref('칼럼')
const paginationInfo = ref(null)
</script>

<template>
  <div class="flex">
    <!-- 왼쪽: 프로필 사이드바 -->
    <MentorProfileSideBar
        :isMyProfile="isMyProfile"
        :profileImageUrl="user.profileImageUrl"
        :nickname="user.nickname"
        :jobName="user.jobName"
        :temperature="user.temperature"
        :price="user.price"
        :preferredTime="user.preferredTime"
        :externalLinkUrl="user.externalLinkUrl"
        :introduction="user.introduction"
        :isActive="user.isActive"
    />

    <!-- 오른쪽: 탭 + 콘텐츠 -->
    <div class="flex flex-col flex-1 space-y-8 pr-25 ml-5">
      <MentorProfileTabBar v-model:tab="selectedTab" />

      <!-- 콘텐츠 카드 -->
      <div class="border rounded px-4 py-8 space-y-12">
        <ColumnTab v-if="selectedTab==='칼럼'"/>
        <SeriesTab v-else-if="selectedTab==='시리즈'"/>
        <PostTab v-else-if="selectedTab==='게시글'"/>
        <ReviewTab
            v-else-if="selectedTab==='리뷰'"
            v-model:pagination="paginationInfo"/>
      </div>

      <!-- 페이징 바 추가 -->
      <PagingBar
          class="mt-8"
          :current-page="currentPage"
          :total-pages="totalPages"
          @change-page="handlePageChange"
      />
    </div>
  </div>
</template>
