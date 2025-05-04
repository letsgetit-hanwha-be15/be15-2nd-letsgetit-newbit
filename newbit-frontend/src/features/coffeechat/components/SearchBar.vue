<script setup>
import { ref } from 'vue';

const props = defineProps({
  categories: {
    type: Array,
    default: () => [
      { code: '1', name: '멘토이름' },
      { code: '2', name: '기술스택' },
    ],
  },
});

// 검색 이벤트 emit
const emit = defineEmits(['search']);

// 상태를 ref로 분리
const selectedCategory = ref('1');
const userName = ref('');
const techStack = ref('');
const coffeechatPrice = ref('');
const temperature = ref('');

// 검색 실행 함수
const emitSearch = () => {
  emit('search', {
    userName: userName.value || null,
    techStack: techStack.value || null,
    coffeechatPrice: coffeechatPrice.value || null,
    temperature: temperature.value || null,
  });
};
</script>
<template>
  <div class="bg-[var(--newbitlight)] pt-14 px-6 pb-8 rounded-lg shadow-md max-w-3xl mx-auto">
    <div class="text-center mb-6">
      <p class="text-[var(--newbittext)] text-13px-regular">
        업계 선배 혹은 미래의 동료들과 인사이트를 나눠 보세요.
      </p>
    </div>

    <div class="flex flex-wrap items-center justify-center gap-4">
      <label for="category-select" class="text-13px-bold text-[var(--newbittext)]">검색 옵션</label>

      <!-- 카테고리 선택 -->
      <select
          id="category-select"
          v-model.lazy="selectedCategory"
          @change="emitSearch"
          class="text-13px-regular px-4 py-2 border rounded-lg shadow-sm
               bg-[var(--newbitbackground)] text-[var(--newbittext)]
               border-[var(--newbitdivider)]
               focus:outline-none focus:ring-2 focus:ring-[var(--newbitnormal)]"
      >
        <option
            v-for="cat in props.categories"
            :key="cat.code"
            :value="cat.code"
        >
          {{ cat.name }}
        </option>
      </select>

      <!-- 멘토 이름 입력 -->
      <input
          id="mentor-input"
          v-if="selectedCategory === '1'"
          type="text"
          v-model.trim.lazy="userName"
          placeholder="멘토 이름"
          @keyup.enter="emitSearch"
          class="text-13px-regular px-4 py-2 border rounded-lg shadow-sm
               bg-[var(--newbitbackground)] text-[var(--newbittext)]
               border-[var(--newbitdivider)]
               focus:outline-none focus:ring-2 focus:ring-[var(--newbitnormal)]"
      />

      <!-- 기술 스택 입력 -->
      <input
          id="tech-stack-input"
          v-if="selectedCategory === '2'"
          type="text"
          v-model.trim.lazy="techStack"
          placeholder="기술 스택"
          @keyup.enter="emitSearch"
          class="text-13px-regular px-4 py-2 border rounded-lg shadow-sm
               bg-[var(--newbitbackground)] text-[var(--newbittext)]
               border-[var(--newbitdivider)]
               focus:outline-none focus:ring-2 focus:ring-[var(--newbitnormal)]"
      />

      <!-- 검색 버튼 -->
      <button
          @click="emitSearch"
          aria-label="검색하기"
          class="text-button px-6 py-2 rounded-lg shadow-sm
               bg-[var(--newbitnormal)] text-[var(--newbitlight)]
               hover:bg-[var(--newbitnormal-hover)]
               active:bg-[var(--newbitnormal-active)]
               transition-colors"
      >
        검색
      </button>

      <!-- 정렬 옵션 -->
      <div class="flex items-center justify-center gap-4 text-13px-regular text-[var(--newbitgray)]">
        <select
            v-model="coffeechatPrice"
            @change="emitSearch"
            class="px-3 py-1 border rounded-md
                 bg-[var(--newbitbackground)] text-[var(--newbittext)]
                 border-[var(--newbitdivider)]
                 focus:outline-none focus:ring-1 focus:ring-[var(--newbitnormal)]"
        >
          <option value="">커피챗 가격 정렬</option>
          <option value="ASC">오름차순</option>
          <option value="DESC">내림차순</option>
        </select>

        <select
            v-model="temperature"
            @change="emitSearch"
            class="px-3 py-1 border rounded-md
                 bg-[var(--newbitbackground)] text-[var(--newbittext)]
                 border-[var(--newbitdivider)]
                 focus:outline-none focus:ring-1 focus:ring-[var(--newbitnormal)]"
        >
          <option value="">멘토 온도 정렬</option>
          <option value="ASC">오름차순</option>
          <option value="DESC">내림차순</option>
        </select>
      </div>
    </div>
  </div>
</template>


<style scoped>
/* 추가 스타일 */
</style>
