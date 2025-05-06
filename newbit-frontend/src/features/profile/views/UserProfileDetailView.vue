<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/features/stores/auth'
import { getUserProfile } from '@/api/user'
import { getPostUserId } from '@/api/post'
import UserProfileSideBar from '@/features/profile/components/UserProfileSideBar.vue'
import UserProfileTabBar from '@/features/profile/components/UserProfileTabBar.vue'
import PagingBar from '@/components/common/PagingBar.vue'
import UserPostListItem from '@/features/profile/components/UserPostListItem.vue'
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
const currentPage = ref(1)
const totalPages = ref(0)
const posts = ref([])

// 항상 8줄 고정
const paddedPosts = computed(() => {
  const filled = [...posts.value]
  while (filled.length < 8) {
    filled.push(null)
  }
  return filled
})

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

// 게시글 목록 API
async function fetchPosts() {
  try {
    const res = await getPostUserId(userId, currentPage.value - 1, 8) // page, size
    const data = res.data
    posts.value = data.content
    totalPages.value = data.totalPages
  } catch (e) {
    console.error('게시글 조회 실패:', e)
  }
}

// 페이지 변경 시 호출
function handlePageChange(page) {
  currentPage.value = page
  fetchPosts()
}

// 최초 로딩
onMounted(async () => {
  await fetchUserProfile()
  await fetchPosts()
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

      <!-- 게시글 테이블 -->
      <div class="bg-white border border-gray-200 rounded-2xl p-6 shadow">
        <table class="w-full table-auto border-collapse">
          <thead>
          <tr class="border-b">
            <th class="py-2">번호</th>
            <th class="text-left pl-4 py-2">제목</th>
            <th class="py-2">작성자</th>
            <th class="py-2">작성일</th>
            <th class="py-2">좋아요</th>
          </tr>
          </thead>
          <tbody>
          <UserPostListItem
              v-for="(post, index) in paddedPosts"
              :key="index"
              :post="post"
              :rowIndex="index"
              :currentPage="currentPage"
              :pageSize="8"
          />
          </tbody>
        </table>
      </div>

      <!-- 페이징바 -->
      <PagingBar
          class="mt-8"
          :current-page="currentPage"
          :total-pages="totalPages"
          @page-change="handlePageChange"
      />
    </div>
  </div>

  <div v-else class="text-center text-gray-500 py-20">
    프로필 정보를 불러오는 중입니다...
  </div>
</template>
