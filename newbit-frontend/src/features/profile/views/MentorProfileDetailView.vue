<script setup>
import { ref } from 'vue'
import MentorProfileSideBar from '@/features/profile/components/MentorProfileSideBar.vue'
import MentorProfileTabBar from '@/features/profile/components/MentorProfileTabBar.vue'
import PagingBar from '@/components/common/PagingBar.vue'
import profileImage from '@/assets/image/default-profile.png'

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
    <div class="flex flex-col flex-1 py-16 pr-25 ml-5">
      <MentorProfileTabBar />

      <!-- 콘텐츠 카드 -->
      <div class="bg-white border border-gray-200 rounded-2xl p-6 shadow">
        <h1 class="text-xl font-bold text-[var(--newbittext)] mb-4">
          유저 프로필 상세 조회
        </h1>
        <p>여기에 유저 게시글, 시리즈, 리뷰 등의 콘텐츠가 들어갈 예정입니다.</p>
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
