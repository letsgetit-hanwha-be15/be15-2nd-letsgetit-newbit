<script setup>

import {computed, ref} from "vue";
import profileImage from '@/assets/image/default-profile.png'
import MentorProfileCard from "@/features/mypage/components/MentorProfileCard.vue";
import {useRoute, useRouter} from "vue-router";
import CoffeechatDetail from "@/features/mypage/components/CoffeechatDetail.vue";

const route = useRoute();
const coffeechatId = ref(Number(route.params.id))

// 프론트용 페이지
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

const originalCoffeechats = ref([
  {
    "success": true,
    "data": {
      "coffeechat": {
        "coffeechatId": 1,
        "progressStatus": "IN_PROGRESS",
        "requestMessage": "안녕하세요웅웅",
        "purchaseQuantity": 2,
        "confirmedSchedule": null,
        "endedAt": null,
        "updatedAt": null,
        "reason": null,
        "mentorId": 3,
        "menteeId": 2
      }
    },
    "errorCode": null,
    "message": null,
    "timestamp": "2025-05-04T22:55:16.9462967"
  }, {
    "success": true,
    "data": {
      "coffeechat": {
        "coffeechatId": 2,
        "progressStatus": "PAYMENT_WAITING",
        "requestMessage": "안녕하세요웅웅",
        "purchaseQuantity": 2,
        "confirmedSchedule": null,
        "endedAt": null,
        "updatedAt": null,
        "reason": null,
        "mentorId": 3,
        "menteeId": 2
      }
    },
    "errorCode": null,
    "message": null,
    "timestamp": "2025-05-04T22:55:16.9462967"
  },
  {
    "success": true,
    "data": {
      "coffeechat": {
        "coffeechatId": 3,
        "progressStatus": "COFFEECHAT_WAITING",
        "requestMessage": "안녕하세요웅웅",
        "purchaseQuantity": 2,
        "confirmedSchedule": null,
        "endedAt": null,
        "updatedAt": null,
        "reason": null,
        "mentorId": 3,
        "menteeId": 2
      }
    },
    "errorCode": null,
    "message": null,
    "timestamp": "2025-05-04T22:55:16.9462967"
  },
  {
    "success": true,
    "data": {
      "coffeechat": {
        "coffeechatId": 4,
        "progressStatus": "CANCEL",
        "requestMessage": "안녕하세요웅웅",
        "purchaseQuantity": 2,
        "confirmedSchedule": null,
        "endedAt": null,
        "updatedAt": "2025-05-20T22:55:16",
        "reason": "단순변심",
        "mentorId": 3,
        "menteeId": 2
      }
    },
    "errorCode": null,
    "message": null,
    "timestamp": "2025-05-04T22:55:16.9462967"
  },
  {
    "success": true,
    "data": {
      "coffeechat": {
        "coffeechatId": 5,
        "progressStatus": "IN-PROGRESS",
        "requestMessage": "안녕하세요웅웅",
        "purchaseQuantity": 2,
        "confirmedSchedule": null,
        "endedAt": null,
        "updatedAt": null,
        "reason": null,
        "mentorId": 3,
        "menteeId": 2
      }
    },
    "errorCode": null,
    "message": null,
    "timestamp": "2025-05-04T22:55:16.9462967"
  },
]);

const requestTimes = {
  "requestTimes": [
    {
      "requestTimeId": 1,
      "eventDate": "2025-05-10T",
      "startTime": "2025-05-10T14:00:00",
      "endTime": "2025-05-10T16:00:00",
      "coffeechatId": 1
    }, {
      "requestTimeId": 2,
      "eventDate": "2025-05-11T",
      "startTime": "2025-05-11T14:00:00",
      "endTime": "2025-05-11T16:00:00",
      "coffeechatId": 1
    }, {
      "requestTimeId": 3,
      "eventDate": "2025-05-12T",
      "startTime": "2025-05-12T14:00:00",
      "endTime": "2025-05-12T16:00:00",
      "coffeechatId": 1
    }]
}

const coffeechat = computed(() => {
  return originalCoffeechats.value
      .map(item => item.data.coffeechat)
      .find(c => c.coffeechatId === coffeechatId.value);
});

// 프론트용 끝

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
      <!-- 1. 상태가 in-progress일 때 보여주는 컴포넌트 -->
      <CoffeechatDetail
          v-if="coffeechat.progressStatus === 'IN_PROGRESS'"
          :coffeechat="coffeechat"
          :requestTimes="requestTimes.requestTimes"
          :isMentor=true
      />
      <!-- 버튼들 -->

    </div>
  </div>
  <div class="flex justify-end">
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