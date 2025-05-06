<script setup>

// 프론트용 페이지
// 멘토 정보 (API 연동 전용 Mock)
import {ref} from "vue";
import profileImage from '@/assets/image/default-profile.png'
import RegisterReviewForm from "@/features/mypage/components/RegisterReviewForm.vue";
import {useRoute, useRouter} from "vue-router";
import {useToast} from "vue-toastification";

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

const isSubmitting = ref(false);
const route = useRoute();
const router = useRouter();
const toast = useToast();
const coffeechatId = ref(Number(route.params.id));

const handleCreate = async (formData) => {
  isSubmitting.value = true;

  // todo : 리뷰 등록 api 호출
  toast.success('리뷰 등록 완료')

  isSubmitting.value = false;
  await router.push(`/mypage/history/coffeechats/${coffeechatId.value}`)
}
</script>

<template>
  <div class="space-y-8 w-screen">
    <div class="border-b pb-3 space-x-4 ">
      <span class="text-heading2">리뷰 등록</span>
    </div>
    <div class="border rounded p-4">
      <RegisterReviewForm
      :submitLabel="'등록'"
      @submit="handleCreate"/>
    </div>
  </div>
</template>

<style scoped>

</style>