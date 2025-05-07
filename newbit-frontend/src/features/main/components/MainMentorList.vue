<script setup>
import { ref, onMounted } from 'vue'
import { getMentors } from '@/api/mentor'

const mentors = ref([])

onMounted(async () => {
  try {
    const res = await getMentors({ size: 2 })
    mentors.value = res.data.data.mentors // ✅ 여기 수정됨
  } catch (err) {
    console.error('멘토 목록 조회 실패:', err)
  }
})
</script>

<template>
  <div class="w-full h-full p-5 rounded-lg border border-gray-200 bg-white">
    <h2 class="text-heading3 text-newbittext font-bold mb-4">멘토 프로필</h2>

    <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
      <div
          v-for="mentor in mentors"
          :key="mentor.mentorId"
          class="p-4 rounded-xl border border-gray-200 shadow-sm bg-white"
      >
        <!-- 상단 프로필 -->
        <div class="flex items-center gap-3 mb-2">
          <!-- 프로필 이미지 -->
          <img
              :src="mentor.profileImageUrl || '/assets/default-profile.png'"
              alt="프로필"
              class="w-10 h-10 rounded-full object-cover"
          />

          <!-- 닉네임만 -->
          <p class="text-sm font-bold">{{ mentor.nickname }}</p>

          <!-- 온도 -->
          <span class="ml-auto text-sm text-yellow-500 font-bold whitespace-nowrap">
    ⭐ {{ mentor.temperature }}℃
  </span>
        </div>

        <!-- 기술 스택 -->
        <div
            class="bg-gray-50 rounded-md p-2 min-h-[60px] flex flex-wrap gap-2"
        >
          <span
              v-if="mentor.techstackName"
              class="px-2 py-1 text-xs bg-gray-200 rounded"
          >
            {{ mentor.techstackName }}
          </span>
          <span
              v-else
              class="text-xs text-gray-400 italic"
          >
            기술스택 없음
          </span>
        </div>

        <!-- 가격 -->
        <div class="text-sm text-right mt-2 text-gray-600">
          {{ mentor.price }}💎 (30분)
        </div>
      </div>
    </div>
  </div>
</template>
