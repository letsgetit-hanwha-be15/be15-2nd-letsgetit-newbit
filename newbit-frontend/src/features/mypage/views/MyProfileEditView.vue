<script setup>
import { ref, onMounted } from 'vue';
import { FetchJobList, FetchTechstackList } from '@/api/user';
import api from '@/api/axios';
import MyProfileEditForm from '@/features/mypage/components/MyProfileEditForm.vue';

const profile = ref({});
const jobOptions = ref([]);
const techstackOptions = ref([]);
const showModal = ref(false);
const pendingRequest = ref(false);
const error = ref('');
const formData = ref(null);

onMounted(async () => {
  try {
    const [jobRes, techRes, profileRes] = await Promise.all([
      FetchJobList(),
      FetchTechstackList(),
      api.get('/user/users/me') // ✅ 프로필 불러오기 API
    ]);

    if (jobRes.data.success) {
      jobOptions.value = jobRes.data.data;
    }

    if (techRes.data.success) {
      techstackOptions.value = techRes.data.data;
    }

    if (profileRes.data.success) {
      profile.value = profileRes.data.data;
    }
  } catch (e) {
    error.value = '데이터 불러오기 실패';
    console.error(e);
  }
});

const onFormSubmit = (data) => {
  formData.value = data;
  showModal.value = true;
};

const confirmSubmit = async () => {
  try {
    pendingRequest.value = true;
    await api.put('/user/users/me', formData.value); // ✅ 실제 수정 API 호출
    alert('프로필이 수정되었습니다.');
  } catch (e) {
    alert('수정 실패');
    console.error(e);
  } finally {
    pendingRequest.value = false;
    showModal.value = false;
  }
};
</script>

<template>
  <div class="w-full max-w-4xl mx-auto">
    <h2 class="text-heading3 mb-4">회원 정보 수정</h2>

    <div v-if="error" class="text-red-500 text-sm mb-4">{{ error }}</div>

    <div class="w-full px-36 py-24 border rounded-lg shadow-sm">
      <MyProfileEditForm
          :initialProfile="profile"
          :jobOptions="jobOptions"
          :techstackOptions="techstackOptions"
          @submit="onFormSubmit"
      />
    </div>

    <!-- 수정 확인 모달 -->
    <div v-if="showModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 w-full max-w-md text-center shadow-lg">
        <p class="text-lg font-medium mb-6">입력한 정보로 수정하시겠습니까?</p>
        <div class="flex justify-center space-x-4">
          <button
              @click="confirmSubmit"
              :disabled="pendingRequest"
              class="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 disabled:opacity-50"
          >
            확인
          </button>
          <button
              @click="showModal = false"
              :disabled="pendingRequest"
              class="bg-gray-300 text-gray-800 px-4 py-2 rounded hover:bg-gray-400 disabled:opacity-50"
          >
            취소
          </button>
        </div>
      </div>
    </div>
  </div>
</template>