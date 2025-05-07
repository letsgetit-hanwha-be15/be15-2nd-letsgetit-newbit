<script setup>
// 각 컴포넌트는 아래에서 import
import MainColumnBanner from '@/features/main/components/MainColumnBanner.vue'
import MainLoginPromBanner from '@/features/main/components/MainLoginPromBanner.vue'
import MainIntroBanner from '@/features/main/components/MainIntroBanner.vue'
import MainTemplateList from '@/features/main/components/MainTemplateList.vue'
import MainColumnList from '@/features/main/components/MainColumnList.vue'
import MainMentorList from '@/features/main/components/MainMentorList.vue'
import MainSeriesList from '@/features/main/components/MainSeriesList.vue'
</script>

<template>
  <div class="max-w-[1280px] mx-auto px-4 py-8 space-y-10">

    <!-- A. 상단 배너: 50%씩 정확히 나눔 -->
    <div class="flex gap-5">
      <MainColumnBanner class="basis-1/2 h-[410px]" />
      <MainLoginPromBanner class="basis-1/2 h-[410px]" />
    </div>

    <!-- B. 소개 배너 -->
    <MainIntroBanner class="w-full h-[187px]" />

    <!-- C. 문서 템플릿 + 칼럼 -->
    <div class="flex gap-5">
      <MainTemplateList class="basis-1/2 h-[590px]" />
      <MainColumnList class="basis-1/2 h-[778px]" />
    </div>

    <!-- D. 멘토 + 시리즈 -->
    <div class="flex gap-5">
      <MainMentorList class="basis-1/2 h-[575px]" />
      <MainSeriesList class="basis-1/2 h-[648px]" />
    </div>

  </div>
</template>


