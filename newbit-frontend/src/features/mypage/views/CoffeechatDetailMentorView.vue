<script setup>

import {computed, onMounted, ref} from "vue";
import profileImage from '@/assets/image/default-profile.png'
import MentorProfileCard from "@/features/mypage/components/MentorProfileCard.vue";
import {useRoute, useRouter} from "vue-router";
import CoffeechatDetail from "@/features/mypage/components/CoffeechatDetail.vue";
import UserProfileCard from "@/features/mypage/components/UserProfileCard.vue";
import UserProfileSideBar from "@/features/profile/components/UserProfileSideBar.vue";
import {getCoffeechatById, getRequestTimes} from "@/api/coffeechat.js";
import {getUserProfile} from "@/api/user.js";

const route = useRoute();
const coffeechatId = ref(Number(route.params.id))

// 프론트용 페이지
// 유저 정보 (API 연동 전용 Mock)
const mentee = ref({})

const coffeechat = ref({});

const requestTimes = ref([]);

const fetchMentee = async () => {
  try {
    const menteeData = await getUserProfile(coffeechat.value.coffeechatId);
    mentee.value = menteeData.data.data || {};
    console.log(menteeData);
  } catch (e) {
    console.log('멘티 상세 조회 실패', e);
  }
}

const fetchCoffeechat = async () => {
  try {
    const {data : wrapper} = await getCoffeechatById(coffeechatId.value);
    coffeechat.value = wrapper.data.coffeechat || {};

    if(coffeechat.value.progressStatus === 'IN_PROGRESS'){
      const timesData = await getRequestTimes(coffeechatId.value);
      requestTimes.value = timesData.data.data.requestTimes || [];
    }
    console.log('커피챗객체', coffeechat.value)
    console.log('요청시간객체', requestTimes.value)
  } catch (e) {
    console.log('커피챗 상세 조회 실패', e);
  }
}

onMounted(async () => {
  await fetchCoffeechat();
  await fetchMentee();
});

const statusMap = {
  IN_PROGRESS: '승인대기',
  PAYMENT_WAITING: '결제대기',
  COFFEECHAT_WAITING: '커피챗대기',
  COMPLETE: '이용완료',
  CANCEL: '취소/환불',
}

function getStatusText(status) {
  return statusMap[status] || '알 수 없음'
}

</script>

<template>
  <!--  커피챗 상세 조회 페이지-->
  <div class="space-y-8 w-screen">
    <div class="border-b pb-3 space-x-4 ">
      <span class="text-heading2">진행 상태</span>
      <span class="text-16px-regular">{{ getStatusText(coffeechat.progressStatus) }}</span>
    </div>
    <div class="border rounded p-4">
      <CoffeechatDetail
          :coffeechat="coffeechat"
          :requestTimes="requestTimes"
          :isMentor=true
      />
      <!-- 버튼들 -->

    </div>
  </div>
  <div class="flex justify-end">
    <div class="w-fit">
      <UserProfileCard
          :profileImageUrl="mentee.profileImageUrl"
          :nickname="mentee.nickname"
          :jobName="mentee.jobName"
      />
    </div>
  </div>
</template>

<style scoped>

</style>