<script setup>
import { onMounted, ref } from 'vue';
import { getUserInfo, putUserInfo } from "@/api/user.js"; // ✅ 추가

const phoneNumber = ref('');
const currentPassword = ref('');
const newPassword = ref('');
const confirmNewPassword = ref('');
const showModal = ref(false);
const errorMessage = ref('');
const successMessage = ref('');

// 회원 정보 조회
onMounted(async () => {
  try {
    const response = await getUserInfo();
    phoneNumber.value = response.data.data.phoneNumber;
  } catch (error) {
    errorMessage.value = '계정 정보를 불러오지 못했습니다.';
  }
});

const handleSubmit = () => {
  errorMessage.value = '';

  if (confirmNewPassword.value !== newPassword.value) {
    errorMessage.value = '비밀번호가 일치하지 않습니다.';
    return;
  }

  if (!phoneNumber.value) {
    errorMessage.value = '전화번호는 필수입니다.';
    return;
  }

  showModal.value = true;
};

const submitUpdateRequest = async () => {
  try {
    const payload = {
      phoneNumber: phoneNumber.value,
      currentPassword: currentPassword.value,
      newPassword: newPassword.value || undefined,
    };

    const response = await putUserInfo(payload);

    if (response.data?.success) {
      successMessage.value = '회원 정보가 변경되었습니다.';
      errorMessage.value = '';
    } else {
      errorMessage.value = response.data?.message || '수정에 실패했습니다.';
      successMessage.value = '';
    }
  } catch (error) {
    if (error.response?.data?.message) {
      errorMessage.value = error.response.data.message;
    } else {
      errorMessage.value = '요청 중 오류가 발생했습니다.';
    }
  } finally {
    showModal.value = false;
  }
};
</script>

<template>
  <div class="w-full max-w-4xl mx-auto">
    <h2 class="text-heading3 mb-4">프로필 수정</h2>
    <div class="flex items-center justify-center">
      <div class="w-full px-36 py-24 border rounded-lg shadow-sm">
        <form @submit.prevent="handleSubmit" class="space-y-6 flex flex-col items-center justify-center">
          <!-- 전화번호 -->
          <div class="w-full flex flex-col items-center justify-center">
            <label for="phone" class="block text-sm text-16px-regular mb-4 w-3/5">전화번호</label>
            <input
                id="phone"
                v-model="phoneNumber"
                placeholder="010-0000-0000"
                class="w-3/5 px-4 py-3 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-400"
                required
            />
          </div>

          <!-- 현재 비밀번호 -->
          <div class="w-full flex flex-col items-center justify-center">
            <label for="currentPassword" class="block text-sm text-16px-regular mb-4 w-3/5">현재 비밀번호</label>
            <input
                id="currentPassword"
                type="password"
                v-model="currentPassword"
                placeholder="현재 비밀번호"
                class="w-3/5 px-4 py-3 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-400"
            />
          </div>

          <!-- 새 비밀번호 -->
          <div class="w-full flex flex-col items-center justify-center">
            <label for="newPassword" class="block text-sm text-16px-regular mb-4 w-3/5">새 비밀번호</label>
            <input
                id="newPassword"
                type="password"
                v-model="newPassword"
                placeholder="새 비밀번호"
                class="w-3/5 px-4 py-3 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-400"
            />
          </div>

          <!-- 새 비밀번호 확인 -->
          <div class="w-full flex flex-col items-center justify-center">
            <label for="confirmNewPassword" class="block text-sm text-16px-regular mb-4 w-3/5">새 비밀번호 확인</label>
            <input
                id="confirmNewPassword"
                type="password"
                v-model="confirmNewPassword"
                placeholder="새 비밀번호 확인"
                class="w-3/5 px-4 py-3 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-400"
            />
          </div>

          <!-- 오류 메시지 -->
          <p v-if="errorMessage" class="text-red-500 text-sm w-3/5 text-left">{{ errorMessage }}</p>

          <!-- 저장하기 버튼 -->
          <button
              type="submit"
              class="w-3/5 py-3 bg-[var(--newbitnormal)] text-white rounded-xl text-center hover:bg-[var(--newbitnormal-hover)] transition"
          >
            저장하기
          </button>
          <p
              v-if="successMessage"
              class="text-blue-500 text-sm w-3/5 text-left mt-2"
          >
            {{ successMessage }}
          </p>
        </form>
      </div>
    </div>

    <!-- 임시 모달 -->
    <div v-if="showModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 w-full max-w-md text-center shadow-lg">
        <p class="text-lg font-medium mb-6">입력한 정보로 저장하시겠습니까?</p>
        <div class="flex justify-center space-x-4">
          <button
              @click="submitUpdateRequest"
              class="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
          >
            확인
          </button>
          <button
              @click="showModal = false"
              class="bg-gray-300 text-gray-800 px-4 py-2 rounded hover:bg-gray-400"
          >
            취소
          </button>
        </div>
      </div>
    </div>
  </div>
</template>