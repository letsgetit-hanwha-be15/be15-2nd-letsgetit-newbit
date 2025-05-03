<script setup>
import { ref } from 'vue'

// props
const props = defineProps({
  initialLikes: {
    type: Number,
    required: true
  },
  initiallyLiked: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update']) // 좋아요 변경 시 외부로 알림

// 상태
const isLiked = ref(props.initiallyLiked)
const likeCount = ref(props.initialLikes)

// 토글 함수
const toggleLike = () => {
  isLiked.value = !isLiked.value
  likeCount.value += isLiked.value ? 1 : -1

  emit('update', { liked: isLiked.value, likeCount: likeCount.value })
}
</script>

<template>
  <button @click="toggleLike" class="flex items-center gap-1 px-2 py-1 border rounded">
    <img
        :src="isLiked ? '/src/assets/image/heart-active.png' : '/src/assets/image/heart-default.png'"
        alt="좋아요"
        class="w-4 h-4"
    />
    <span class="text-sm">{{ likeCount }}</span>
  </button>
</template>

<style scoped>
button {
  transition: all 0.2s ease;
}
button:hover {
  opacity: 0.8;
}
</style>
