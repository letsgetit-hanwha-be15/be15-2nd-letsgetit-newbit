<script setup>
import { ref, watch } from 'vue';
import { useRouter } from 'vue-router';

const props = defineProps({
  profile: Object,
  coffeechat: Object,
  introduce: Object
});

const emit = defineEmits(['updateCoffeechat', 'updateIntroduce']);

const router = useRouter();

// profile
const profileImageUrl = ref('');
const nickname = ref('');
const jobName = ref('');
const temperature = ref(0);

// coffeechat
const isActive = ref(true);
const preferredTime = ref('');
const price = ref(0);

// introduce
const introduction = ref('');
const externalLinkUrl = ref('');

// Watch props
watch(
    () => props.profile,
    (val) => {
      if (val) {
        profileImageUrl.value = val.profileImageUrl || '';
        nickname.value = val.nickname || '';
        jobName.value = val.jobName || '';
        temperature.value = val.temperature || 0;
      }
    },
    { immediate: true }
);

watch(
    () => props.coffeechat,
    (val) => {
      if (val) {
        isActive.value = val.isActive ?? true;
        preferredTime.value = val.preferredTime || '';
        price.value = val.price || 0;
      }
    },
    { immediate: true }
);

watch(
    () => props.introduce,
    (val) => {
      if (val) {
        introduction.value = val.introduction || '';
        externalLinkUrl.value = val.externalLinkUrl || '';
      }
    },
    { immediate: true }
);

// 이동 함수
const goToProfileEdit = () => {
  router.push('/mypage/profile/edit');
};

const submitCoffeechat = () => {
  emit('updateCoffeechat', {
    isActive: isActive.value,
    preferredTime: preferredTime.value,
    price: price.value
  });
};

const submitIntroduce = () => {
  emit('updateIntroduce', {
    introduction: introduction.value,
    externalLinkUrl: externalLinkUrl.value
  });
};
</script>

<template>
  <div class="space-y-8">
    <div class="flex gap-6">
      <!-- 왼쪽: 프로필 정보 -->
      <div class="w-1/3 border rounded-lg p-6 flex flex-col items-center">
        <img :src="profileImageUrl || '@/assets/image/profile.png'" class="w-32 h-32 rounded-full object-cover" />
        <h2 class="text-xl font-bold mt-4 flex items-center gap-1">
          {{ nickname }}
          <span>👑</span>
        </h2>
        <p class="text-sm text-gray-500">{{ jobName }}</p>
        <p class="text-sm mt-2">온도 <span>{{ temperature }}</span></p>
        <button
            @click="goToProfileEdit"
            class="mt-4 bg-[var(--newbitnormal)] text-white px-4 py-2 rounded"
        >
          프로필 수정
        </button>
      </div>

      <!-- 오른쪽: 커피챗 관리 -->
      <div class="w-2/3 border rounded-lg p-6 flex flex-col justify-between">
        <div>
          <h3 class="text-base font-semibold mb-4">커피챗 관리</h3>

          <div class="mb-4">
            <label class="mr-4">활동여부</label>
            <label class="mr-2">
              <input type="radio" v-model="isActive" :value="true" /> 활동
            </label>
            <label>
              <input type="radio" v-model="isActive" :value="false" /> 미활동
            </label>
          </div>

          <div class="mb-4">
            <label class="block mb-1">선호시간</label>
            <textarea
                v-model="preferredTime"
                rows="3"
                class="w-full border rounded px-3 py-2"
                placeholder="7시 이후 좋아요! 2시간 이하로 신청해주세요!"
            ></textarea>
          </div>

          <div class="mb-4">
            <label class="block mb-1">가격(다이아 개수)</label>
            <input
                type="number"
                v-model="price"
                class="w-full border rounded px-3 py-2"
                step="50"
                min="50"
            />
          </div>
        </div>
        <div class="flex justify-end">
          <button @click="submitCoffeechat" class="bg-[var(--newbitnormal)] text-white px-4 py-2 rounded">
            수정
          </button>
        </div>
      </div>
    </div>

    <!-- 소개 영역 -->
    <div class="border rounded-lg p-6 flex flex-col justify-between">
      <div>
        <h3 class="text-base font-semibold mb-4">내 소개</h3>
        <textarea v-model="introduction" rows="4" class="w-full border rounded px-3 py-2 mb-4"></textarea>

        <label class="block mb-1">외부 소개 링크</label>
        <input v-model="externalLinkUrl" class="w-full border rounded px-3 py-2 mb-4" />
      </div>
      <div class="flex justify-end">
        <button @click="submitIntroduce" class="bg-[var(--newbitnormal)] text-white px-4 py-2 rounded">
          수정
        </button>
      </div>
    </div>
  </div>
</template>