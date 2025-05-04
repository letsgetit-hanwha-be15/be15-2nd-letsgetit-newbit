<script setup>

import {computed, ref} from "vue";
import CoffeechatList from "@/features/mypage/components/CoffeechatList.vue";
import PagingBar from "@/components/common/PagingBar.vue";

const statuses = [
  '전체',
  '승인대기',
  '결제대기',
  '커피챗대기',
  '이용완료',
  '취소/환불'
];

const selectedStatus = ref('전체');
const originalCoffeechats = ref([
  {
    "coffeechatId": 13,
    "progressStatus": "IN_PROGRESS",
    "requestMessage": "안녕하세요 신입입니다.",
    "profileImageUrl": "/src/assets/image/profile-mentee.png",
    "nickname": "mentee"
  },
  {
    "coffeechatId": 6,
    "progressStatus": "COMPLETE",
    // "requestMessage": "CS 기초 상담 부탁드립니다.",
    "requestMessage": "CS 기초 상담 부탁드립니다.CS 기초 상담 부탁드립니다.CS 기초 상담 부탁드립니다.CS 기초 상담 부탁드립니다.CS 기초 상담 부탁드립니다.CS 기초 상담 부탁드립니다.CS 기초 상담 부탁드립니다.CS 기초 상담 부탁드립니다.CS 기초 상담 부탁드립니다.CS 기초 상담 부탁드립니다.CS 기초 상담 부탁드립니다.CS 기초 상담 부탁드립니다.CS 기초 상담 부탁드립니다.",
    "profileImageUrl": "/src/assets/image/profile-mentee.png",
    "nickname": "백엔드꿈나무"
  },
  {
    "coffeechatId": 3,
    "progressStatus": "CANCEL",
    "requestMessage": "커피챗 신청합니다.",
    "profileImageUrl": "/src/assets/image/profile-mentee.png",
    "nickname": "코린이"
  }
]);

// 프론트용 구현 시작

const statusMap = {
  IN_PROGRESS: '승인대기',
  PAYMENT_WAITING: '결제대기',
  COFFEECHAT_WAITING: '커피챗대기',
  COMPLETE: '이용완료',
  CANCEL: '취소/환불',
};

function getStatusText(status) {
  return statusMap[status] || '알 수 없음'
}

const coffeechats = computed(() => {
  let filtered = originalCoffeechats.value;

  if (selectedStatus.value === '전체') return filtered
  return filtered.filter(chat => getStatusText(chat.progressStatus) === selectedStatus.value) || null
})

// 프론트용 구현 끝
</script>

<template>
  <div class="p-6 w-screen">
    <h1 class="text-heading1">커피챗 내역</h1>
    <div class="flex flex-wrap gap-2 mb-6">
      <button
          class="px-4 py-2 rounded-full text-sm font-medium transition bg-gray-100 text-gray-700"
      >
        STATUS
      </button>
      <button
          v-for="status in statuses"
          :key="status"
          @click="selectedStatus = status"
          :class="[
          'px-4 py-2 rounded-full text-sm font-medium transition',
          selectedStatus === status
            ? 'bg-[var(--newbitnormal-hover)] text-white'
            : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
          ]"
      >
        {{ status }}
      </button>
    </div>
    <CoffeechatList :coffeechats="coffeechats"/>
<!--    <PagingBar
        v-bind="pagination"
        @page-changed="fetchCoffeechats"
    />-->
  </div>

</template>

<style scoped>

</style>