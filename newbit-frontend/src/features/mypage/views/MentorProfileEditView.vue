<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import MentorProfileEditForm from '@/features/mypage/components/MentorProfileEditForm.vue';

const coffeechatData = ref({});
const introduceData = ref({});
const errorMessage = ref('');
const successMessage = ref('');
const showModal = ref(false);

onMounted(async () => {
  try {
    const [coffeechatRes, introduceRes] = await Promise.all([
      axios.get('/api/mentor/coffeechat'),
      axios.get('/api/mentor/introduce')
    ]);
    coffeechatData.value = coffeechatRes.data;
    introduceData.value = introduceRes.data;
  } catch (e) {
    errorMessage.value = '멘토 정보 불러오기 실패';
    showModal.value = true;
  }
});

const submitCoffeechat = async (data) => {
  try {
    await axios.put('/api/mentor/coffeechat', data);
    successMessage.value = '커피챗 정보가 수정되었습니다.';
  } catch (e) {
    errorMessage.value = '커피챗 정보 수정 실패';
    showModal.value = true;
  }
};

const submitIntroduce = async (data) => {
  try {
    await axios.put('/api/mentor/introduce', data);
    successMessage.value = '소개 정보가 수정되었습니다.';
  } catch (e) {
    errorMessage.value = '소개 정보 수정 실패';
    showModal.value = true;
  }
};
</script>

<template>
  <div class="w-full max-w-4xl mx-auto">

    <h2 class="text-heading3 mb-4">멘토 프로필 수정</h2>
    <MentorProfileEditForm
        :coffeechat="coffeechatData"
        :introduce="introduceData"
        @updateCoffeechat="submitCoffeechat"
        @updateIntroduce="submitIntroduce"
    />

    <p v-if="successMessage" class="text-green-600 text-sm">{{ successMessage }}</p>

    <!-- 에러 모달 -->
    <div v-if="showModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white p-6 rounded-lg shadow-lg text-center max-w-sm w-full">
        <p class="text-lg font-semibold mb-4">{{ errorMessage }}</p>
        <button @click="showModal = false" class="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600">
          확인
        </button>
      </div>
    </div>
  </div>
</template>