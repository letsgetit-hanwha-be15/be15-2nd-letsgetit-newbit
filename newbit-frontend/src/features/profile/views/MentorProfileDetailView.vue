<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/features/stores/auth'
import { getMentorProfile } from '@/api/user'
import MentorProfileSideBar from '@/features/profile/components/MentorProfileSideBar.vue'
import MentorProfileTabBar from '@/features/profile/components/MentorProfileTabBar.vue'
import profileImage from '@/assets/image/default-profile.png'
import ColumnTab from "@/features/profile/components/ColumnTab.vue"
import SeriesTab from "@/features/profile/components/SeriesTab.vue";
import PostTab from "@/features/profile/components/PostTab.vue";
import ReviewTab from "@/features/profile/components/ReviewTab.vue";

// 인증 및 라우터 정보
const authStore = useAuthStore()
const myId = authStore.mentorId
const route = useRoute()
const mentorId = Number(route.params.id)

// 상태 정의
const user = ref({
  id: mentorId,
  profileImageUrl: profileImage,
  nickname: '',
  jobName: '',
  temperature: 0,
  price: 0,
  preferredTime: '',
  externalLinkUrl: '',
  introduction: '',
  isActive: false
})
const isMyProfile = ref(false)
const isLoaded = ref(false)
const selectedTab = ref('칼럼')

// 데이터
const columns = ref([])
const series = ref([])
const posts= ref([])
const reviews = ref([])

// 멘토 프로필 API
async function fetchMentorProfile() {
  try {
    const res = await getMentorProfile(mentorId)
    const data = res.data.data

    user.value = {
      id: mentorId,
      profileImageUrl: data.profileImageUrl || profileImage,
      nickname: data.nickname,
      jobName: data.jobName,
      temperature: data.temperature,
      price: data.price,
      preferredTime: data.preferredTime,
      externalLinkUrl: data.externalLinkUrl,
      introduction: data.introduction,
      isActive: data.isActive
    }

    isMyProfile.value = String(mentorId) === String(myId)

    // 칼럼만 사용
    columns.value = data.columns || []

  } catch (e) {
    console.error('멘토 프로필 조회 실패:', e)
  }
}

// 최초 로딩
onMounted(async () => {
  await fetchMentorProfile()
  isLoaded.value = true
})
</script>

<template>
  <div v-if="isLoaded" class="flex">
    <!-- 좌측 프로필 사이드바 -->
    <MentorProfileSideBar
        :profileImageUrl="user.profileImageUrl"
        :nickname="user.nickname"
        :jobName="user.jobName"
        :isMyProfile="isMyProfile"
        :temperature="user.temperature"
        :price="user.price"
        :preferredTime="user.preferredTime"
        :externalLinkUrl="user.externalLinkUrl"
        :introduction="user.introduction"
        :isActive="user.isActive"
    />

    <!-- 우측 콘텐츠 -->
    <div class="flex flex-col flex-1 py-16 pr-25 ml-5">
      <MentorProfileTabBar v-model:tab="selectedTab" />

      <div class="bg-white border border-gray-200 rounded-2xl p-6 shadow mt-8">
        <ColumnTab v-if="selectedTab === '칼럼'" :columns="columns" />
        <SeriesTab v-if="selectedTab === '시리즈'" :series="series" />
        <PostTab v-if="selectedTab === '게시글'" :post="posts"/>
        <ReviewTab v-if="selectedTab === '리뷰'" :review="reviews" />
      </div>

    </div>
  </div>

  <div v-else class="text-center text-gray-500 py-20">
    멘토 프로필 정보를 불러오는 중입니다...
  </div>
</template>
