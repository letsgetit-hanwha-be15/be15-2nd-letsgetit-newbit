<script setup>
import { ref, watch } from 'vue';

const props = defineProps({
  initialProfile: Object,
  jobOptions: Array,          // [{ jobId, jobName }]
  techstackOptions: Array     // [{ techstackId, techstackName }]
});

const emit = defineEmits(['submit']);

const nickname = ref('');
const profileImageUrl = ref('@/assets/image/profile.png');
const jobName = ref('');
const techstackNames = ref([]); // ["Java", "Spring", ...]
const selectedStack = ref('');
const errorMessage = ref('');
const fileInput = ref(null);

// 초기 데이터 바인딩
watch(
    () => props.initialProfile,
    (profile) => {
      if (profile) {
        nickname.value = profile.nickname || '';
        profileImageUrl.value = profile.profileImageUrl || '';
        jobName.value = profile.jobName || '';
        techstackNames.value = profile.techstackNames ? [...profile.techstackNames] : [];
      }
    },
    { immediate: true }
);

// 기술 스택 추가
const addTechstack = () => {
  if (selectedStack.value && !techstackNames.value.includes(selectedStack.value)) {
    techstackNames.value.push(selectedStack.value);
    selectedStack.value = '';
  }
};

// 기술 스택 제거
const removeTechstack = (stack) => {
  techstackNames.value = techstackNames.value.filter((s) => s !== stack);
};

// 제출
const handleSubmit = () => {
  if (!nickname.value) {
    errorMessage.value = '닉네임은 필수입니다.';
    return;
  }

  emit('submit', {
    nickname: nickname.value,
    profileImageUrl: profileImageUrl.value,
    jobName: jobName.value,
    techstackNames: techstackNames.value
  });
};

// 프로필 이미지 변경
const triggerFileSelect = () => {
  fileInput.value?.click();
};

const handleImageUpload = (event) => {
  const file = event.target.files[0];
  if (!file) return;

  const reader = new FileReader();
  reader.onload = (e) => {
    profileImageUrl.value = e.target.result;
  };
  reader.readAsDataURL(file);
};
</script>

<template>
  <form @submit.prevent="handleSubmit" class="space-y-6">
    <!-- 프로필 이미지 -->
    <div class="relative w-32 h-32 group mx-auto">
      <img :src="profileImageUrl" alt="profile" class="w-32 h-32 rounded-full object-cover border" />
      <input type="file" accept="image/*" ref="fileInput" class="hidden" @change="handleImageUpload" />
      <button
          type="button"
          @click="triggerFileSelect"
          class="absolute left-1/2 -translate-x-1/2 bottom-[-12px] bg-gray-400 text-white text-xs px-2 py-1 rounded opacity-0 group-hover:opacity-100 transition"
      >
        수정
      </button>
    </div>

    <!-- 닉네임 -->
    <div>
      <label class="block text-sm mb-2">닉네임</label>
      <input v-model="nickname" class="w-full px-4 py-2 border rounded" />
    </div>

    <!-- 직종 -->
    <div>
      <label class="block text-sm mb-2">직종</label>
      <select v-model="jobName" class="w-full px-4 py-2 border rounded">
        <option disabled value="">직종 선택</option>
        <option
            v-for="job in jobOptions"
            :key="job.jobId"
            :value="job.jobName"
        >
          {{ job.jobName }}
        </option>
      </select>
    </div>

    <!-- 기술 스택 -->
    <div>
      <label class="block text-sm mb-2">기술 스택</label>
      <select v-model="selectedStack" @change="addTechstack" class="w-full px-4 py-2 border rounded">
        <option disabled value="">Techstack</option>
        <option
            v-for="stack in techstackOptions"
            :key="stack.techstackId"
            :value="stack.techstackName"
        >
          {{ stack.techstackName }}
        </option>
      </select>
      <div class="flex flex-wrap mt-3 gap-2 min-h-[60px] border border-gray-300 p-3 rounded">
        <span
            v-for="stack in techstackNames"
            :key="stack"
            class="bg-gray-200 px-2 py-1 rounded text-sm flex items-center gap-1"
        >
          {{ stack }}
          <button type="button" @click="removeTechstack(stack)" class="text-sm text-red-500">×</button>
        </span>
      </div>
    </div>

    <p v-if="errorMessage" class="text-red-500 text-sm">{{ errorMessage }}</p>

    <button
        type="submit"
        class="w-full py-3 bg-[var(--newbitnormal)] text-white rounded-xl hover:bg-[var(--newbitnormal-hover)]"
    >
      프로필 수정
    </button>
  </form>
</template>