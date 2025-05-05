<script setup>


import {ref} from "vue";
import MentorProfileCard from "@/features/mypage/components/MentorProfileCard.vue";
import profileImage from '@/assets/image/default-profile.png'
import {useRoute, useRouter} from "vue-router";

const isCancelModalOpen = ref(false);
const selectedReasonKey = ref(null);
const router = useRouter();
const route = useRoute();
const coffeechatId = ref(Number(route.params.id))

const statusMap = {
  1: '개인적인 사정',
  2: '건강 문제',
  4: '단순 변심',
  5: '필요성 감소',
};

function cancelRegister() {
  if (selectedReasonKey.value === null) {
    alert('취소 사유를 선택해주세요.')
    return
  }

  isCancelModalOpen.value = true
}

function closeCancelModal() {
  isCancelModalOpen.value = false
}

function confirmCancel() {
  const reasonKey = selectedReasonKey.value
  const reasonText = statusMap[reasonKey]

  // todo : 취소 API 호출
  console.log(`선택된 키: ${reasonKey}, 사유: ${reasonText}`)

  isCancelModalOpen.value = false
  router.push(`/mypage/history/coffeechats/${coffeechatId.value}`)
}

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

</script>

<template>
  <div class="w-full space-y-8 p-5 border rounded">
    <!-- 라디오 버튼 -->
    <div class="mb-4 space-y-2">
      <p class="text-16px-regular font-medium text-gray-700">커피챗 취소 사유</p>
      <div v-for="(label, key) in statusMap" :key="key" class="flex items-center gap-2 ml-2">
        <input type="radio"
               :id="`reason-${key}`"
               :value="Number(key)"
               v-model="selectedReasonKey"
               class="accent-[var(--newbitnormal)]">
        <label :for="`reason-${key}`" class="text-13px-regular">{{ label }}</label>
      </div>
    </div>
    <div class="mb-4 space-y-2">
      <div class="text-16px-regular">환불 정책 안내</div>
      <div class="text-13px-regular">커피챗 시작 하루 전 취소할 경우, 환불은 불가합니다.</div>
    </div>
    <div class="flex flex-wrap gap-2 justify-end pb-10">
      <button type="button"
              @click="cancelRegister"
              class="ml-2 rounded-md px-3 py-1 text-button bg-[var(--newbitred)] text-[var(--newbitlight)]  text-button">
        취소
      </button>
    </div>
  </div>
  <!-- 커피챗 취소 모달 -->
  <div v-if="isCancelModalOpen" class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
    <div class="bg-[var(--newbitbackground)] rounded-lg p-6 w-[400px] shadow-lg">
      <h2 class="text-heading3 mb-4">커피챗 취소</h2>
      <p class="mb-6 text-13px-regular">
        커피챗 시작 하루 전 취소할 경우, 환불은 불가합니다.<br />
        정말 취소하시겠습니까?
      </p>
      <div class="flex justify-end gap-2">
        <button @click="closeCancelModal"
                class="bg-[var(--newbitred)] text-[var(--newbitlight)] px-4 py-1 rounded-md font-semibold">
          아니요
        </button>
        <button @click="confirmCancel"
                class="bg-[var(--newbitnormal)] text-[var(--newbitlight)] px-4 py-1 rounded-md font-semibold">
          네
        </button>
      </div>
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