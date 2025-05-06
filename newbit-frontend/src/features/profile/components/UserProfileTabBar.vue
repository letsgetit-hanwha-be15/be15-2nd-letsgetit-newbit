<script setup>
import { ref } from 'vue'

const tabs = ['게시글']
const activeTab = ref(tabs[0])
</script>

<template>
  <!-- 탭 컨테이너: 수직 가운데 정렬 + 높이 확보 -->
  <div class="flex items-center min-h-[200px]">
    <div class="flex space-x-6 border-b border-[var(--newbitdivider)]">
      <button
          v-for="tab in tabs"
          :key="tab"
          @click="activeTab = tab"
          :class="[
          'pb-2 text-13px-regular text-[var(--newbitgray)]',
          activeTab === tab ? 'border-b-2 border-[var(--newbitnormal)] text-[var(--newbitnormal)] font-bold' : ''
        ]"
      >
        {{ tab }}
      </button>
    </div>
  </div>
</template>
