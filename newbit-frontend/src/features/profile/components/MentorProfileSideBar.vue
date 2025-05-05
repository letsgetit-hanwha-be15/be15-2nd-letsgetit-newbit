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
      class="w-[406px] min-h-[960px] bg-[var(--newbitlight)] px-8 pt-10 pb-8 border-r border-[var(--newbitdivider)]"
  >
    <!-- 프로필 상단 -->
    <!-- 프로필 상단 영역 -->
    <div class="relative flex flex-col items-center text-center mt-[42px]">
      <img
          :src="profileImageUrl"
          alt="프로필 이미지"
          class="w-[141px] h-[141px] rounded-full object-cover mb-[25px]"
      />

      <!-- 닉네임 -->
      <h2
          class="text-[color:var(--newbittext)] mb-[25px]"
          style="
      font-size: var(--newbit-text-heading1-font-size);
      font-weight: var(--newbit-text-heading1-font-weight);
      font-family: var(--newbit-text-heading1-font-family);
    "
      >
        {{ nickname }}
      </h2>

      <button
          v-if="isMyProfile"
          class="absolute right-0 top-[calc(178px)] text-sm text-white bg-[var(--newbitnormal)] px-2 py-1 rounded"
      >
        프로필 수정
      </button>
      <button
          v-else
          class="absolute right-0 top-[calc(178px)] text-sm text-white bg-[var(--newbitnormal)] px-2 py-1 rounded"
      >
        커피챗 신청
      </button>

      <!-- 직무명 + 활동 상태 -->
      <div class="flex items-center justify-center gap-x-20 mb-6">
        <p class="text-heading3 text-[color:var(--newbittext)]">
          {{ jobName }}
        </p>
        <p v-if="isActive" class="text-heading3 text-[color:var(--newbittext)]">
          활동 중
        </p>
      </div>

      <!-- 멘토 정보 카드 -->
      <div class="bg-[var(--newbitlight)] border border-blue-300 rounded-md p-4 text-left w-full space-y-4 break-words">
        <div>
          <p class="text-heading3 text-[var(--newbittext)]">온도</p>
          <p class="text-16px-regular">{{ temperature }}℃</p>
        </div>
        <div>
          <p class="text-heading3 text-[var(--newbittext)]">커피챗 가격</p>
          <p class="text-16px-regular">{{ price }}💎 · 30분</p>
        </div>
        <div>
          <p class="text-heading3 text-[var(--newbittext)]">선호시간</p>
          <p class="text-16px-regular whitespace-pre-line break-words">{{ preferredTime }}</p>
        </div>
        <div>
          <p class="text-heading3 text-[var(--newbittext)]">멘토 외부 링크</p>
          <a
              v-if="externalLinkUrl"
              :href="externalLinkUrl"
              class="text-16px-regular text-blue-500 underline break-all"
              target="_blank"
              rel="noopener noreferrer"
          >
            {{ externalLinkUrl }}
          </a>
          <p v-else class="text-16px-regular text-[var(--newbitgray)]">링크 없음</p>
        </div>
        <div>
          <p class="text-heading3 text-[var(--newbittext)]">멘토 소개</p>
          <p class="text-16px-regular whitespace-pre-line break-words">{{ introduction }}</p>
        </div>
      </div>
    </div>
  </aside>
</template>