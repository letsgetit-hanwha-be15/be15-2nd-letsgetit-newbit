<script setup>
import {computed} from "vue";

const {mentor} = defineProps({
  mentor: {
    type: Object,
    required: true
  }
});

const techstackArray = computed(() =>
    mentor.techstackName
    ? mentor.techstackName
        .split(',')
        .map(item => item.trim())
        .filter(item => item.length > 0)
        : null
)
</script>
<template>
  <!-- mentor-card -->
  <router-link :to="`/profile/mentor/${mentor.mentorId}`">
    <div class="p-4 text-[var(--newbittext)] mx-auto space-y-4 border rounded-xl shadow-md">
      <!-- 상단 -->
      <div class="flex justify-center items-center space-x-5">
        <!-- 왼쪽 -->
        <div class="space-y-2">
          <span class="text-lg text-heading3">{{ mentor.nickname }}</span>
          <div class="flex items-center mb-4">
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

            <span class="text-sm mr-2">{{ mentor.role }}백엔드</span>

            <span class="text-sm">{{ mentor.temperature }}°C</span>
          </div>
        </div>
        <!-- 오른쪽 -->
        <img
            :src="mentor.profileImageUrl"
            :alt="mentor.nickname"
            class="w-20 h-20 rounded-full"
        />
      </div>

      <!-- 하단 -->
      <!-- Technology Stack -->
      <div class="flex flex-wrap items-start gap-4 w-[90%] min-h-20 mx-auto rounded-md p-2 bg-[var(--newbitlightgray)]">
        <template v-if="techstackArray != null && techstackArray.length > 0">
          <div
              v-for="(tech, index) in techstackArray"
              :key="index"
              class="text-13px-regular bg-[var(--newbitdivider)] px-2 py-1 rounded"
          >
            {{ tech }}
          </div>
        </template>
      </div>

      <!-- Price and Duration -->
      <div class="text-13px-regular text-right">
        {{ mentor.price }} 💎(30분)
      </div>
    </div>
  </router-link>
</template>

<style scoped>
</style>
