<script setup>
const props = defineProps({
  coffeechat: {
    type: Object,
    required: true
  },
  isMentor: {
    type: Boolean,
    required: true
  }
});

const coffeechat = props.coffeechat;
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
  <router-link :to="`/mypage/${isMentor ? 'mentor' : 'history'}/coffeechats/${coffeechat.coffeechatId}`" class="hover:underline">
    <div class="flex items-center w-full px-4 py-5 border-b border-gray-200">
      <!-- 프로필 이미지 -->
      <img
          :src="coffeechat.profileImageUrl"
          :alt="coffeechat.nickname"
          class="w-10 h-10 rounded-full object-cover mr-4 shrink-0"
      />

      <!-- 닉네임 + 메시지 한 줄로 정렬 -->
      <div class="flex-1 overflow-hidden">
        <div
            class="text-sm w-[600px] whitespace-nowrap overflow-hidden text-ellipsis"
            :title="`${coffeechat.nickname} ${coffeechat.requestMessage}`"
        >
        <span class="font-medium text-[var(--newbittext)]">
          {{ coffeechat.nickname }}
        </span>
          <span class="text-gray-500 ml-3">
          {{ coffeechat.requestMessage }}
        </span>
        </div>
      </div>

      <!-- 상태 텍스트 -->
      <div class="ml-4 text-sm font-medium text-gray-600 shrink-0">
        {{ getStatusText(coffeechat.progressStatus) }}
      </div>
    </div>
  </router-link>
</template>

<style scoped>

</style>