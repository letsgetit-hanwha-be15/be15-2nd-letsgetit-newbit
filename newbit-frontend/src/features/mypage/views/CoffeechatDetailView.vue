<script setup>

import {ref} from "vue";
import profileImage from '@/assets/image/default-profile.png'
import MentorProfileCard from "@/features/mypage/components/MentorProfileCard.vue";

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
</script>

<template>
 커피챗 상세 조회 페이지
  <div class="flex justify-end w-screen">
    <div class="w-fit">
      <MentorProfileCard
          :isMyProfile=false
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
    </div>
  </div>
</template>

<style scoped>

</style>