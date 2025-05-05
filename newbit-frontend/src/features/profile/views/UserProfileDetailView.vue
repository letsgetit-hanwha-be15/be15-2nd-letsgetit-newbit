<script setup>
import { ref } from 'vue'
import UserProfileSideBar from '@/features/profile/components/UserProfileSideBar.vue'
import UserUserProfileTabBar from '@/features/profile/components/UserProfileTabBar.vue'
import PagingBar from '@/components/common/PagingBar.vue'
import profileImage from '@/assets/image/default-profile.png'

// 로그인한 내 ID (임시)
const myId = 1

// 프로필 대상 유저 정보 (API 연동 전용 Mock)
const user = ref({
  id: 1,
  profileImageUrl: profileImage,
  nickname: 'sezeme',
  jobName: '백엔드',
})

// 내 프로필인지 여부
const isMyProfile = ref(user.value.id === myId)

// 페이징 상태
const currentPage = ref(1)
const totalPages = ref(5)

function handlePageChange(page) {
  currentPage.value = page
  // 👉 여기에 API 호출 등 페이지 변경에 따른 로직 추가 가능
}
</script>

<template>
  <div class="flex">
    <!-- 왼쪽: 프로필 사이드바 -->
    <UserProfileSideBar
        :isMyProfile="isMyProfile"
        :profileImageUrl="user.profileImageUrl"
        :nickname="user.nickname"
        :jobName="user.jobName"
    />

    <!-- 오른쪽: 탭 + 콘텐츠 -->
    <div class="flex flex-col flex-1 py-16 pr-25 ml-5">
      <UserUserProfileTabBar />

      <!-- 콘텐츠 카드 -->
      <div class="bg-white border border-gray-200 rounded-2xl p-6 shadow">
        <h1 class="text-xl font-bold text-[var(--newbittext)] mb-4">
          유저 프로필 상세 조회
        </h1>
        <p>여기에 유저 게시글, 시리즈, 리뷰 등의 콘텐츠가 들어갈 예정입니다.</p>
      </div>

      <!-- 페이징 바 -->
      <PagingBar
          class="mt-8"
          :current-page="currentPage"
          :total-pages="totalPages"
          @change-page="handlePageChange"
      />
    </div>
  </div>
</template>
