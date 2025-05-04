<script setup>
import { computed } from 'vue'

const props = defineProps({
  series: {
    type: Object,
    required: true
  }
})

const fallbackImg = new URL('@/assets/image/product-skeleton.png', import.meta.url).href

const displayThumbnail = computed(() =>
    props.series.thumbnailUrl && props.series.thumbnailUrl.trim() !== ''
        ? props.series.thumbnailUrl
        : fallbackImg
)

const toggleSubscription = () => {
  props.series.subscribed = !props.series.subscribed
}
</script>

<template>
  <div class="w-full">
    <div class="relative rounded overflow-hidden mb-2">
      <img
          :src="displayThumbnail"
          alt="시리즈 썸네일"
          class="w-full h-[180px] object-cover rounded"
      />
      <button
          @click="toggleSubscription"
          class="absolute top-2 right-2 px-2 py-1 text-12px-bold rounded text-white"
          :class="series.subscribed ? 'bg-[var(--newbitred)]' : 'bg-blue-500'"
      >
        {{ series.subscribed ? '구독 취소' : '구독하기' }}
      </button>
    </div>
    <p class="text-12px-regular text-[var(--newbitgray)] mb-1">
      {{ series.mentorNickname }} | {{ series.columnCount }}개의 칼럼
    </p>
    <p class="text-14px-bold text-[var(--newbittext)] mt-1">
      {{ series.title }}
    </p>
  </div>
</template>