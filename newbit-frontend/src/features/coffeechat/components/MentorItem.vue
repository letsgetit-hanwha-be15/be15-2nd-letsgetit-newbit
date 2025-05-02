<script setup>
const {mentor} = defineProps({
  mentor: {
    type: Object,
    required: true
  }
});
</script>
<template>
      <!-- Mentor Profile Image -->
      <img
          :src="mentor.profileImageUrl"
          :alt="mentor.nickname"
          class="w-20 h-20 rounded-full mb-4"
      />

      <!-- Mentor Name and Role -->
      <p class="text-lg font-semibold text-gray-800">{{ mentor.nickname }}</p>
      <p class="text-sm text-gray-500 mb-4">{{ mentor.role }}</p>

      <!-- Temperature -->
      <div class="flex items-center justify-center mb-4">
        <svg
            xmlns="http://www.w3.org/2000/svg"
            class="h-5 w-5 text-yellow-500 mr-1"
            fill="currentColor"
            viewBox="0 0 24 24"
        >
          <path
              d="M12 2a10 10 0 100 20 10 10 0 000-20zm0 18a8 8 0 110-16 8 8 0 010 16zm-4-9h8v2H8v-2zm4 3h4v2h-4v-2z"
          />
        </svg>
        <span class="text-sm text-gray-800">{{ mentor.temperature }}°C</span>
      </div>

      <!-- Technology Stack -->
      <div class="flex flex-wrap gap-2 justify-center mb-4">
        <span
            v-for="tech in mentor.techstackName"
            :key="tech"
            class="bg-gray-200 text-sm text-gray-800 px-2 py-1 rounded"
        >
          {{ tech }}
        </span>
      </div>

      <!-- Price and Duration -->
      <div class="text-sm text-gray-500">
        {{ mentor.price }}💎 ({{ mentor.duration }})
      </div>
</template>

<style scoped>
/* 텍스트 유틸리티 클래스 연결 */
.text-20px-regular {
  font-family: var(--newbit-text-20px-regular-font-family), sans-serif;
  font-weight: var(--newbit-text-20px-regular-font-weight);
  font-size: var(--newbit-text-20px-regular-font-size);
  letter-spacing: var(--newbit-text-20px-regular-letter-spacing);
  line-height: var(--newbit-text-20px-regular-line-height);
  font-style: var(--newbit-text-20px-regular-font-style);
}

.text-16px-regular {
  font-family: var(--newbit-text-16px-regular-font-family), sans-serif;
  font-weight: var(--newbit-text-16px-regular-font-weight);
  font-size: var(--newbit-text-16px-regular-font-size);
  letter-spacing: var(--newbit-text-16px-regular-letter-spacing);
  line-height: var(--newbit-text-16px-regular-line-height);
  font-style: var(--newbit-text-16px-regular-font-style);
}
</style>
