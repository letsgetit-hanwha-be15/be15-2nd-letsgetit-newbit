<script setup>
import { ref, defineEmits, defineProps } from 'vue'

const props = defineProps({
  tab: {
    type: String,
    default: '칼럼',
  }
})

const emit = defineEmits(['update:tab'])

const tabs = ['칼럼', '시리즈', '게시글', '리뷰']

function selectTab(tab) {
  emit('update:tab', tab)
}

</script>

<template>
  <!-- 탭 컨테이너: 수직 가운데 정렬 + 높이 확보 -->
  <div class="flex items-end min-h-[100px]">
    <div class="flex space-x-6 border-b border-[var(--newbitdivider)]">
      <button
          v-for="tab in tabs"
          :key="tab"
          @click="selectTab(tab)"
          :class="[
          'pb-2 text-13px-regular text-[var(--newbitgray)]',
          props.tab === tab ? 'border-b-2 border-[var(--newbitnormal)] text-[var(--newbitnormal)] font-bold' : ''
        ]"
      >
        {{ tab }}
      </button>
    </div>
  </div>
</template>

