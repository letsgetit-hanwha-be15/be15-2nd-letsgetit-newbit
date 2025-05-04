
<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import MyProfileEditForm from '@/features/mypage/components/MyProfileEditForm.vue';

const profile = ref({});
const jobOptions = ref(['개발자', '디자이너']);
const techstackOptions = ref(['java', 'js', 'javascript', 'typescript', 'typescript']);
const showModal = ref(false);
const pendingRequest = ref(false);
const error = ref('');
const formData = ref(null);

onMounted(async () => {
  // try {
  //   const [jobRes, stackRes, profileRes] = await Promise.all([
  //     axios.get('/api/jobs'),
  //     axios.get('/api/techstacks'),
  //     axios.get('/api/user/profile')
  //   ]);
  //   jobOptions.value = jobRes.data;
  //   techstackOptions.value = stackRes.data;
  //   profile.value = profileRes.data;
  // } catch (e) {
  //   error.value = '데이터 불러오기 실패';
  // }
});

const onFormSubmit = (data) => {
  formData.value = data;
  showModal.value = true;
};

const confirmSubmit = async () => {
  try {
    pendingRequest.value = true;
    await axios.put('/api/user/profile', formData.value);
    alert('프로필이 수정되었습니다.');
  } catch (e) {
    alert('수정 실패');
  } finally {
    pendingRequest.value = false;
    showModal.value = false;
  }
};
</script>

<template>
  <div class="w-full max-w-4xl mx-auto">

    <h2 class="text-heading3 mb-4">회원 정보 수정</h2>
    <div class="w-full px-36 py-24 border rounded-lg shadow-sm">
      <MyProfileEditForm
          :initialProfile="profile"
          :jobOptions="jobOptions"
          :techstackOptions="techstackOptions"
          @submit="onFormSubmit"
      />
    </div>
  </div>


  <!-- 임시 모달 -->
  <div v-if="showModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div class="bg-white rounded-lg p-6 w-full max-w-md text-center shadow-lg">
      <p class="text-lg font-medium mb-6">입력한 정보로 수정하시겠습니까?</p>
      <div class="flex justify-center space-x-4">
        <button
            @click="confirmSubmit"
            class="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
        >확인</button>
        <button
            @click="showModal = false"
            class="bg-gray-300 text-gray-800 px-4 py-2 rounded hover:bg-gray-400"
        >취소</button>
      </div>
    </div>
  </div>

</template>



