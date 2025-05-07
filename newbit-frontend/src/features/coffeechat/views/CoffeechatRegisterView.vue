<script setup>

import RegisterCoffeechatForm from "@/features/coffeechat/components/RegisterCoffeechatForm.vue";
import {onMounted, ref} from "vue";
import {useRoute, useRouter} from "vue-router";
import {createCoffeechat} from "@/api/coffeechat.js";
import {useToast} from "vue-toastification";
import MentorProfileCard from "@/features/mypage/components/MentorProfileCard.vue";
import {getMentorById} from "@/api/mentor.js";

const router = useRouter();
const route = useRoute();
const isSubmitting = ref(false);
const error = ref(null);
const toast = useToast()
const mentor = ref({});

const fetchMentor = async () => {
  try {
    const response = await getMentorById(route.params.id);
    const data = response.data?.data;
    if (!data) {
      throw new Error('멘토 데이터가 존재하지 않습니다');
    }
    mentor.value = {
      profileImageUrl: data.profileImageUrl,
      nickname: data.nickname,
      jobName: data.jobName,
      temperature: data.temperature,
      price: data.price,
      preferredTime: data.preferredTime,
      externalLinkUrl: data.externalLinkUrl,
      introduction: data.introduction
  };

  } catch (e) {
    console.log('멘토 정보 로드 실패', e);
  }
}

const handleCreate = async (payload) => {
  isSubmitting.value = true;
  error.value = null;
  try{
    const resp = await createCoffeechat(JSON.parse(JSON.stringify(payload)).payload);
    console.log(resp.data.data.coffeechatId);
    const coffeechatId = resp.data.data.coffeechatId;
    toast.success('커피챗 요청 등록 완료했습니다')
    await router.push(`/mypage/history/coffeechats/${coffeechatId}`)
  } catch (e) {
    console.log('커피챗 등록 실패', e);
    error.value = '커피챗 등록에 실패하였습니다';
  }

  isSubmitting.value = false;

}

onMounted(() => fetchMentor())
</script>

<template>
  <div class="flex gap-4 w-full">
    <div class="flex-1">
      <h1 class="border-b border-[var(--newbitdivider)] m-2 p-2">커피챗 신청</h1>
      <RegisterCoffeechatForm
          :submitLabel="'등록'"
          @submit="handleCreate"
      />
      <div v-if="error" class="--error">{{ error }}</div>
    </div>
    <div class="">
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