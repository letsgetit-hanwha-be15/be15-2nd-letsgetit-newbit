<script setup>

import {computed, onMounted, ref} from "vue";
import profileImage from '@/assets/image/default-profile.png'
import MentorProfileCard from "@/features/mypage/components/MentorProfileCard.vue";
import {useRoute, useRouter} from "vue-router";
import CoffeechatDetail from "@/features/mypage/components/CoffeechatDetail.vue";
import {useToast} from "vue-toastification";
import {getCoffeechatById, getRequestTimes} from "@/api/coffeechat.js";

const route = useRoute();
const router = useRouter();
const toast = useToast();
const coffeechatId = ref(Number(route.params.id))
const isPaymentModalOpen = ref(false);

// 프론트용 페이지
// 멘토 정보 (API 연동 전용 Mock)
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

// 현재 로그인한 유저 정보
const me = ref({
  "diamond": 30
})


// todo : coffeechat 상세 조회 api에서 coffeechat.sale_confirmed_at 속성 추가로 가져오기
const coffeechat = ref({});

const requestTimes = ref([]);

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
onMounted(() => fetchCoffeechat());

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

function cancelRegister() {
  router.push(`/mypage/history/coffeechats/${coffeechatId.value}/cancel`);
}

function openPaymentModal() {
  isPaymentModalOpen.value = true;
}

function closePaymentModal() {
  isPaymentModalOpen.value = false;
}

function paymentCoffeechat() {
  // todo : 현재 보유 중인 다이아 몇 개인지 검사 후, 다이아가 부족하면 상점으로 이동
  console.log(me.value.diamond + (user.value.price * coffeechat.value.purchaseQuantity))
  if(me.value.diamond < user.value.price * coffeechat.value.purchaseQuantity){
    router.push('/products')
  }
  else {
    // todo : 커피챗 결제 api 호출
    toast.success('결제 완료되었습니다.');
    isPaymentModalOpen.value = false;
  }
}

function goCoffeeLetter() {
  // todo : 커피챗 아이디로 커피레터 아이디 조회
  const coffeeLetterId = 1
  router.push(`/coffeeletter/${coffeeLetterId}`)
}

function confirmPurchase() {
  // todo : 멘토가 커피챗 종료 했는지 확인 -> DTO 변경
  // todo : 멘티가 구매를 확정하는 api 호출
  toast.success('구매 확정되었습니다.');
}

function registerReview() {
  router.push({ name: 'ReviewRegister', params: { id: coffeechatId.value } })

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
          :diamondCount="user.price * coffeechat.purchaseQuantity"
      />
      <!-- 버튼들 -->
      <div class="flex flex-wrap gap-2 justify-end pb-10">
        <button v-if="coffeechat.progressStatus !== 'IN_PROGRESS' && coffeechat.progressStatus !== 'PAYMENT_WAITING' && coffeechat.progressStatus !== 'CANCEL'"
                @click="goCoffeeLetter"
                class="ml-2 rounded-md px-4 py-2 text-button bg-[var(--newbitnormal)] text-[var(--newbitlight)]  text-button">
          커피레터 입장
        </button>
        <button v-if="coffeechat.progressStatus === 'COMPLETE'"
                :disabled="!coffeechat.saleConfirmedAt"
                @click="confirmPurchase"
                class="ml-2 rounded-md px-4 py-2 text-button bg-[var(--newbitnormal)] text-[var(--newbitlight)]  text-button">
          구매 확정
        </button>
        <button v-if="coffeechat.progressStatus === 'COMPLETE'"
                :disabled
                @click="registerReview"
                class="ml-2 rounded-md px-4 py-2 text-button bg-[var(--newbitnormal)] text-[var(--newbitlight)]  text-button">
          리뷰 작성
        </button>
        <button v-if="coffeechat.progressStatus === 'PAYMENT_WAITING'"
                @click="openPaymentModal"
                class="ml-2 rounded-md px-4 py-2 text-button bg-[var(--newbitnormal)] text-[var(--newbitlight)]  text-button">
          다이아 결제
        </button>
        <button
            v-if="coffeechat.progressStatus !== 'CANCEL' && coffeechat.progressStatus !== 'COMPLETE'"
            type="button"
                @click="cancelRegister"
                class="ml-2 rounded-md px-4 py-2 text-button bg-[var(--newbitred)] text-[var(--newbitlight)]  text-button">
          취소
        </button>
      </div>
      <!-- 모달 -->
      <!-- 커피챗 결제 모달 -->
      <div v-if="isPaymentModalOpen" class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
        <div class="bg-[var(--newbitbackground)] rounded-lg p-6 w-[400px] shadow-lg">
          <h2 class="text-heading3 mb-4">다이아 결제</h2>
          <p class="mb-6 text-13px-regular">
            현재 보유 다이아 {{ me.diamond }} 개, 필요 다이아 {{ user.price * coffeechat.purchaseQuantity }}개<br/>
            <template v-if="me.diamond < user.price * coffeechat.purchaseQuantity">
              다이아 결제창으로 넘어갑니다.<br/>
            </template>
            결제하시겠습니까?
          </p>
          <div class="flex justify-end gap-2">
            <button @click="closePaymentModal"
                    class="bg-[var(--newbitred)] text-[var(--newbitlight)] px-4 py-1 rounded-md font-semibold">
              아니요
            </button>
            <button @click="paymentCoffeechat"
                    class="bg-[var(--newbitnormal)] text-[var(--newbitlight)] px-4 py-1 rounded-md font-semibold">
              네
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div class="flex justify-end">
    <div class="w-fit">
      <MentorProfileCard
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