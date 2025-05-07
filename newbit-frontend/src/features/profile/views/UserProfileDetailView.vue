<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/features/stores/auth'
import { getUserProfile } from '@/api/user'
import UserProfileSideBar from '@/features/profile/components/UserProfileSideBar.vue'
import UserProfileTabBar from '@/features/profile/components/UserProfileTabBar.vue'
import PostTab from '@/features/profile/components/PostTab.vue' // ✅ 게시글 탭 컴포넌트
import profileImage from '@/assets/image/default-profile.png'

// 인증 및 라우터 정보
const authStore = useAuthStore()
const myId = authStore.userId
const route = useRoute()
const userId = Number(route.params.id)

// 상태 정의
const user = ref({
  id: userId,
  profileImageUrl: profileImage,
  nickname: '',
  jobName: ''
})
const isMyProfile = ref(false)
const isLoaded = ref(false)

// 유저 정보 API
async function fetchUserProfile() {
  try {
    const res = await getUserProfile(userId)
    const data = res.data.data

    user.value = {
      id: userId,
      profileImageUrl: data.profileImageUrl || profileImage,
      nickname: data.nickname,
      jobName: data.jobName
    }

    isMyProfile.value = String(userId) === String(myId)
  } catch (e) {
    console.error('유저 프로필 조회 실패:', e)
  }
}

onMounted(async () => {
  await fetchUserProfile()
  isLoaded.value = true
})
</script>

<template>
  <div v-if="isLoaded" class="flex">
    <!-- 좌측 프로필 사이드바 -->
    <UserProfileSideBar
        :profileImageUrl="user.profileImageUrl"
        :nickname="user.nickname"
        :jobName="user.jobName"
        :isMyProfile="isMyProfile"
    />

    <!-- 우측 콘텐츠 -->
    <div class="flex flex-col flex-1 py-16 pr-25 ml-5">
      <UserProfileTabBar />

      <!-- 게시글 탭 -->
      <div class="bg-white border border-gray-200 rounded-2xl p-6 shadow mt-6">
        <PostTab />
      </div>
    </div>
  </div>

  <div v-else class="text-center text-gray-500 py-20">
    프로필 정보를 불러오는 중입니다...
  </div>
</template>
