<script setup>
defineProps({
  profileImageUrl: String,
  nickname: String,
  jobName: String,
  isMyProfile: Boolean,
  temperature: Number,
  price: Number,
  preferredTime: String,
  externalLinkUrl: String,
  introduction: String,
  isActive: Boolean
})
</script>

<template>
  <aside
      class="w-[300px] bg-[var(--newbitlight)] px-8 pt-10 pb-8 border-r border-[var(--newbitdivider)] rounded-xl drop-shadow-md"
  >
    <!-- 프로필 상단 -->
    <!-- 프로필 상단 영역 -->
    <div class="relative flex flex-col items-center text-center space-y-6">
      <img
          :src="profileImageUrl"
          alt="프로필 이미지"
          class="w-[100px] h-[100px] rounded-full object-cover"
      />

      <!-- 닉네임 -->
      <h2
          class="text-[color:var(--newbittext)] text-heading2"
      >
        {{ nickname }}
      </h2>

      <!-- 직무명 + 활동 상태 -->
      <div class="flex items-center justify-center space-x-10">
        <div class="text-16px-regular text-[color:var(--newbittext)]">
          {{ jobName }}
        </div>
        <div v-if="isActive" class="text-16px-regular text-[color:var(--newbittext)]">
          활동 중
        </div>
      </div>

      <!-- 멘토 정보 카드 -->
      <div class="bg-[var(--newbitlight)] border border-blue-300 rounded-md p-4 text-left w-full space-y-8 break-words">
        <div>
          <div class="text-heading3 text-[var(--newbittext)]">온도</div>
          <div class="text-13px-regular mt-1">{{ temperature }}℃</div>
        </div>
        <div>
          <div class="text-heading3 text-[var(--newbittext)]">커피챗 가격</div>
          <div class="text-16px-regular mt-1">{{ price }}💎 · 30분</div>
        </div>
        <div>
          <div class="text-heading3 text-[var(--newbittext)]">선호시간</div>
          <div class="text-16px-regular whitespace-pre-line break-words mt-1">{{ preferredTime }}</div>
        </div>
        <div>
          <div class="text-heading3 text-[var(--newbittext)]">멘토 외부 링크</div>
          <a
              v-if="externalLinkUrl"
              :href="externalLinkUrl"
              class="text-16px-regular text-blue-500 underline break-all mt-1"
              target="_blank"
              rel="noopener noreferrer"
          >
            {{ externalLinkUrl }}
          </a>
          <p v-else class="text-16px-regular text-[var(--newbitgray)]">링크 없음</p>
        </div>
        <div>
          <div class="text-heading3 text-[var(--newbittext)]">멘토 소개</div>
          <div class="text-16px-regular whitespace-pre-line break-words mt-1">{{ introduction }}</div>
        </div>
      </div>
    </div>
  </aside>
</template>