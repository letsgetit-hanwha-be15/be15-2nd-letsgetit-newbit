<script setup>
import { ref, onMounted } from 'vue';
// import { useUserStore } from '@/stores/userStore';
import { getMentorById, patchMentorCoffeechatInfo, patchMentorIntroduction } from '@/api/mentor.js';
import axios from 'axios';
import { useToast } from 'vue-toastification'
import MentorProfileEditForm from '@/features/mypage/components/MentorProfileEditForm.vue';


const toast = useToast()
const profileData = ref({});
const coffeechatData = ref({});
const introduceData = ref({});
const errorMessage = ref('');
const successMessage = ref('');
const showModal = ref(false);

// const userStore = useUserStore();

onMounted(async () => {
  try {
    //TODO: store에서 mentorId 가져오기
    const mentorId = 2; // const mentorId = userStore.mentorId;
    if (!mentorId) throw new Error('mentorId 없음');

    const response = await getMentorById(mentorId);
    const data = response.data?.data;

    profileData.value = {
      profileImageUrl: data.profileImageUrl,
      nickname: data.nickname,
      temperature: data.temperature,
      jobName: data.jobName,
    };

    coffeechatData.value = {
      isActive: data.isActive,
      preferredTime: data.preferredTime,
      price: data.price,
    };

    introduceData.value = {
      introduction: data.introduction,
      externalLinkUrl: data.externalLinkUrl,
    };
  } catch (e) {
    errorMessage.value = '멘토 정보를 불러오지 못했습니다.';
    showModal.value = true;
  }
});

const submitProfile = async (data) => {
  try {
    await axios.put('/api/mentor/profile', data); // 수정 시 필요시 별도 API 분리 가능
    successMessage.value = '프로필 정보가 수정되었습니다.';
  } catch (e) {
    errorMessage.value = '프로필 정보 수정 실패';
    showModal.value = true;
  }
};

const submitCoffeechat = async (data) => {
  try {
    await patchMentorCoffeechatInfo(data);
    successMessage.value = '커피챗 정보가 수정되었습니다.';
    toast.success('커피챗 정보가 수정되었습니다.')
  } catch (e) {
    errorMessage.value = '커피챗 정보 수정 실패';
    showModal.value = true;
  }
};

const submitIntroduce = async (data) => {
  try {
    await patchMentorIntroduction(data);
    successMessage.value = '소개 정보가 수정되었습니다.';
    toast.success('소개 정보가 수정되었습니다.')
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
        :profile="profileData"
        :coffeechat="coffeechatData"
        :introduce="introduceData"
        @updateProfile="submitProfile"
        @updateCoffeechat="submitCoffeechat"
        @updateIntroduce="submitIntroduce"
    />

    <p v-if="successMessage" class="text-[var(--newbitnormal)] text-sm mt-4">
      {{ successMessage }}
    </p>

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
