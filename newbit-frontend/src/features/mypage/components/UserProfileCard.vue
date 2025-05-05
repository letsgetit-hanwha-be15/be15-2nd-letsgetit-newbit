<script setup>
defineProps({
  profileImageUrl: String,
  nickname: String,
  jobName: String,
  isMyProfile: Boolean  // ✅ 추가
})
</script>

<template>
  <aside
      class="w-[406px] min-h-[960px] bg-[var(--newbitlight)] px-8 pt-10 pb-8 border-r border-[var(--newbitdivider)]"
  >
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

      <!-- 직무명 -->
      <p
          class="text-[color:var(--newbittext)]"
          style="
      font-size: var(--newbit-text-heading3-font-size);
      font-weight: var(--newbit-text-heading3-font-weight);
      font-family: var(--newbit-text-heading3-font-family);
    "
      >
        {{ jobName }}
      </p>
    </div>
  </aside>
</template>

<style scoped>

</style>