<script setup>

import {computed, onMounted, ref} from "vue";
import profileImage from '@/assets/image/default-profile.png'
import MentorProfileCard from "@/features/mypage/components/MentorProfileCard.vue";
import {useRoute, useRouter} from "vue-router";
import CoffeechatDetail from "@/features/mypage/components/CoffeechatDetail.vue";
import {useToast} from "vue-toastification";
import {
  cancelCoffeechat,
  confirmPurchaseCoffeechat,
  getCoffeechatById,
  getRequestTimes,
  purchaseCoffeeChat
} from "@/api/coffeechat.js";
import {getMentorById} from "@/api/mentor.js";
import {useAuthStore} from "@/features/stores/auth.js";
import {getRoomIdByCoffeeChatId} from "@/api/coffeeletter.js";

const route = useRoute();
const router = useRouter();
const toast = useToast();
const coffeechatId = ref(Number(route.params.id))
const isPaymentModalOpen = ref(false);

// 현재 로그인한 유저 정보
const authStore = useAuthStore();


const mentor = ref({})

const coffeechat = ref({});

const requestTimes = ref([]);

const fetchMentor = async () => {
  try {
    const mentorData = await getMentorById(coffeechat.value.mentorId);
    mentor.value = mentorData.data.data || {};
  } catch (e) {
    console.log('멘토 상세 조회 실패', e);
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
  } catch (e) {
    console.log('커피챗 상세 조회 실패', e);
  }
}
onMounted(async () => {
  await fetchCoffeechat();
  await fetchMentor();
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

function cancelRegister() {
  router.push(`/mypage/history/coffeechats/${coffeechatId.value}/cancel`);
}

function openPaymentModal() {
  isPaymentModalOpen.value = true;
}

function closePaymentModal() {
  isPaymentModalOpen.value = false;
}

async function paymentCoffeechat() {
  const diamondUsage = mentor.value.price * coffeechat.value.purchaseQuantity;
  if(authStore.diamond < diamondUsage){
    await router.push('/products')
  }
  else {
    try {
      console.log(route.params.id);
      await purchaseCoffeeChat(route.params.id)
      const diamondBalance = authStore.diamond - diamondUsage;
      authStore.updateBalance(authStore.point, diamondBalance);
      toast.success('결제 완료되었습니다.');
      window.location.href = `/mypage/history/coffeechats/${coffeechatId.value}`;
    } catch (e) {
      console.log('커피챗 다이아 결제 에러', e);
    }
    isPaymentModalOpen.value = false;
  }
}

async function goCoffeeLetter() {
  const coffeeLetterId = await getRoomIdByCoffeeChatId(coffeechatId.value);
  await router.push(`/coffeeletter/${coffeeLetterId.data}`)
}

async function confirmPurchase() {
  await confirmPurchaseCoffeechat(coffeechatId.value);
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
          :diamondCount="mentor.price * coffeechat.purchaseQuantity"
      />
      <!-- 버튼들 -->
      <div class="flex flex-wrap gap-2 justify-end pb-10">
        <button v-if="coffeechat.progressStatus !== 'IN_PROGRESS' && coffeechat.progressStatus !== 'PAYMENT_WAITING' && coffeechat.progressStatus !== 'CANCEL'"
                @click="goCoffeeLetter"
                class="ml-2 rounded-md px-4 py-2 text-button bg-[var(--newbitnormal)] text-[var(--newbitlight)]  text-button">
          커피레터 입장
        </button>
        <button v-if="coffeechat.progressStatus === 'COMPLETE' && coffeechat.purchaseConfirmedAt === null"
                @click="confirmPurchase"
                class="ml-2 rounded-md px-4 py-2 text-button bg-[var(--newbitnormal)] text-[var(--newbitlight)]  text-button">
          구매 확정
        </button>
        <button v-if="coffeechat.progressStatus === 'COMPLETE'"
                :disabled="coffeechat.purchaseConfirmedAt === null"
                @click="registerReview"
                class="ml-2 rounded-md px-4 py-2 text-button bg-[var(--newbitnormal)] text-[var(--newbitlight)]  text-button disabled:opacity-50">
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
            현재 보유 다이아 {{ authStore.diamond }} 개, 필요 다이아 {{ mentor.price * coffeechat.purchaseQuantity }}개<br/>
            <template v-if="authStore.diamond < mentor.price * coffeechat.purchaseQuantity">
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
          :profileImageUrl="mentor.profileImageUrl"
          :nickname="mentor.nickname"
          :jobName="mentor.jobName"
          :temperature="mentor.temperature"
          :price="mentor.price"
          :preferredTime="mentor.preferredTime"
          :externalLinkUrl="mentor.externalLinkUrl"
          :introduction="mentor.introduction"
          :isActive="mentor.isActive"
      />
    </div>
  </div>
</template>

<style scoped>

</style>