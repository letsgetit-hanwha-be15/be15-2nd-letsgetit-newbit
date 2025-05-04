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
<h1>커피챗 신청</h1>
  <RegisterCoffeechatForm
  :submitLabel="'등록'"
  @submit="handleCreate"
  />
  <div v-if="error" class="--error">{{ error }}</div>
</template>

<style scoped>
h1 { margin:10px; padding:10px; border-bottom:1px solid #000; }
.error { color: red; margin-top: 10px; }
</style>