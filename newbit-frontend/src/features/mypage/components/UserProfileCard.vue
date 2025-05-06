<script setup>
defineProps({
  profileImageUrl: String,
  nickname: String,
  jobName: String,
})
</script>

<template>
  <aside
      class="w-[300px] bg-[var(--newbitlight)] px-8 pt-10 pb-20 border-r border-[var(--newbitdivider)]  rounded-xl drop-shadow-md"
  >
    <!-- 프로필 상단 영역 -->
    <div class="relative flex flex-col items-center text-center space-y-6">
      <img
          :src="profileImageUrl"
          alt="프로필 이미지"
          class="w-[141px] h-[141px] rounded-full object-cover"
      />

      <!-- 닉네임 -->
      <h2
          class="text-[color:var(--newbittext)] text-heading2"
      >
        {{ nickname }}
      </h2>

      <!-- 직무명 -->
      <p
          class="text-[color:var(--newbittext)] text-16px-regular "
      >
        {{ jobName }}
      </p>
    </div>
  </aside>
</template>

<style scoped>

</style>