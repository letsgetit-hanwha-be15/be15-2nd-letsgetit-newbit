<script setup>

import RegisterCoffeechatForm from "@/features/coffeechat/components/RegisterCoffeechatForm.vue";
import {ref} from "vue";
import {useRouter} from "vue-router";
import {createCoffeechat} from "@/features/coffeechat/api.js";
import {useToast} from "vue-toastification";

const router = useRouter();
const isSubmitting = ref(false);
const error = ref(null);
const toast = useToast()

const handleCreate = async (payload) => {
  isSubmitting.value = true;
  // error.value = null;
  // try{
  //   const resp = await createCoffeechat(payload);
  //   console.log(resp);
  const coffeechatId = 1; //resp.data.data.CoffeechatId;
  //   await router.push(`/coffeechats/${coffeechatId}`)
  // } catch (e) {
  //   console.log('커피챗 등록 실패', e);
  //   error.value = '커피챗 등록에 실패하였습니다';
  // }
  toast.success('커피챗 요청 등록 완료했습니다')
  isSubmitting.value = false;
  await router.push(`/mypage/coffeechats/${coffeechatId}`)
}
</script>

<template>
  <div class="flex flex-wrap gap-4 justify-between">
    <div class="w-[65%]">
      <h1 class="border-b border-[var(--newbitdivider)] m-2 p-2">커피챗 신청</h1>
      <RegisterCoffeechatForm
          :submitLabel="'등록'"
          @submit="handleCreate"
      />
      <div v-if="error" class="--error">{{ error }}</div>
    </div>
    <div class="w-[25%] border rounded  m-2 p-2">
      <!-- todo : 멘토 프로필 컴포넌트 끼워넣기 -->
      멘토 프로필
    </div>
  </div>
</template>

<style scoped>
</style>