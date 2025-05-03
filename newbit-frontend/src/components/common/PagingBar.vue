<script setup>
import { computed } from 'vue';

// 부모 컴포넌트로부터 전달받는 props
const props = defineProps({
  currentPage: {
    type: Number,
    required: true
  },
  totalPages: {
    type: Number,
    required: true
  },
  totalItems: {
    type: Number,
    required: true
  }
});

// 페이지 변경 시 부모 컴포넌트로 이벤트를 전달
const emit = defineEmits(['page-changed']);

// 페이지 변경 함수
const changePage = (page) => {
  if (page >= 1 && page <= props.totalPages) {
    emit('page-changed', page); // 부모에게 페이지 변경 이벤트 전달
  }
};

// 현재 페이지 주변의 페이지 번호를 계산
const visiblePages = computed(() => {
  const pages = [];
  const range = 5; // 현재 페이지를 기준으로 몇 개의 페이지를 표시할지 결정
  const start = Math.max(1, props.currentPage - range);
  const end = Math.min(props.totalPages, props.currentPage + range);

  for (let i = start; i <= end; i++) {
    pages.push(i);
  }

  return pages;
});
</script>

<template>
  <div class="flex items-center justify-center gap-2 my-4 p-10">
    <!-- 이전 페이지 버튼 -->
    <button
        :disabled="currentPage === 1"
        @click="changePage(currentPage - 1)"
        class="text-button px-4 py-2 rounded-md transition-colors
             bg-[var(--newbitlight)] text-[var(--newbittext)]
             hover:bg-[var(--newbitlightmode-hover)]
             active:bg-[var(--newbitlight-active)]
             disabled:bg-[var(--newbitdivider)] disabled:cursor-not-allowed"
    >
      Previous
    </button>

    <!-- 페이지 번호 버튼들 -->
    <span v-for="page in visiblePages" :key="page">
      <button
          @click="changePage(page)"
          :class="[
          'px-4 py-2 rounded-md text-button transition-colors',
          page === currentPage
            ? 'bg-[var(--newbitnormal)] text-[var(--newbitlight)] active:bg-[var(--newbitnormal-active)] hover:bg-[var(--newbitnormal-hover)]'
            : 'bg-[var(--newbitlight)] text-[var(--newbittext)] hover:bg-[var(--newbitlightmode-hover)] active:bg-[var(--newbitlight-active)]'
        ]"
      >
        {{ page }}
      </button>
    </span>

    <!-- 다음 페이지 버튼 -->
    <button
        :disabled="currentPage === totalPages"
        @click="changePage(currentPage + 1)"
        class="text-button px-4 py-2 rounded-md transition-colors
             bg-[var(--newbitlight)] text-[var(--newbittext)]
             hover:bg-[var(--newbitlightmode-hover)]
             active:bg-[var(--newbitlight-active)]
             disabled:bg-[var(--newbitdivider)] disabled:cursor-not-allowed"
    >
      Next
    </button>

    <!-- 페이지 정보 -->
    <div class="ml-4 text-10px-regular text-[var(--newbittext)]">
      Page {{ currentPage }} of {{ totalPages }} ({{ totalItems }} items)
    </div>
  </div>
</template>

<style scoped>

</style>