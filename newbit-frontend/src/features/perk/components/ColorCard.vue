<template>
  <div
    class="relative aspect-square rounded-xl overflow-hidden cursor-pointer transition-all duration-300 transform"
    :style="{
      backgroundColor: color,
      boxShadow: isSelected
        ? `0 10px 25px -5px ${color}80, 0 8px 10px -6px ${color}40`
        : isHovered
        ? `0 10px 15px -3px ${color}40, 0 4px 6px -4px ${color}30`
        : `0 4px 6px -1px ${color}30, 0 2px 4px -2px ${color}20`,
    }"
    :class="{
      'scale-105 z-10': isHovered && !isSelected,
      'scale-100': !isHovered && !isSelected,
      'scale-110 z-20 ring-4 ring-white/30 ring-offset-2 ring-offset-gray-100':
        isSelected,
    }"
    @mouseenter="isHovered = true"
    @mouseleave="isHovered = false"
    @click="handleClick"
  >
    <!-- 선택 효과 -->
    <div
      v-if="isSelected"
      class="absolute inset-0 bg-white/10 backdrop-blur-sm"
    ></div>

    <!-- 호버 효과 -->
    <div
      class="absolute inset-0 bg-black/0 transition-all duration-300"
      :class="{ 'bg-black/5 backdrop-blur-sm': isHovered && !isSelected }"
    ></div>

    <!-- 선택 아이콘 -->
    <div
      v-if="isSelected"
      class="absolute top-2 right-2 w-6 h-6 rounded-full bg-white flex items-center justify-center animate-scaleIn"
    >
      <svg
        xmlns="http://www.w3.org/2000/svg"
        class="h-4 w-4 text-green-500"
        viewBox="0 0 20 20"
        fill="currentColor"
      >
        <path
          fill-rule="evenodd"
          d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
          clip-rule="evenodd"
        />
      </svg>
    </div>

    <!-- 색상 반짝임 효과 -->
    <div
      class="absolute inset-0 opacity-0 transition-opacity duration-500"
      :class="{ 'opacity-100': isSelected }"
      :style="{
        background: `radial-gradient(circle at center, ${color}30 0%, transparent 70%)`,
      }"
    ></div>

    <!-- 색상 이름 -->
    <div
      class="absolute bottom-0 left-0 right-0 py-3 px-2 text-white text-center flex justify-center items-center transition-all duration-300"
      :style="{
        backgroundColor: 'rgba(0, 0, 0, 0.6)',
        backdropFilter: 'blur(8px)',
        transform:
          isHovered || isSelected ? 'translateY(0)' : 'translateY(5px)',
        opacity: isHovered || isSelected ? 1 : 0.9,
      }"
    >
      <p class="font-bold text-base tracking-wide">{{ colorName }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";

const props = defineProps({
  color: {
    type: String,
    required: true,
  },
  colorName: {
    type: String,
    required: true,
  },
  isSelected: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(["select"]);
const isHovered = ref(false);

const handleClick = () => {
  emit("select");
};
</script>

<style scoped>
div {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  transform-origin: center;
  will-change: transform, box-shadow;
}

div:active {
  transform: scale(0.95);
}

.animate-scaleIn {
  animation: scaleIn 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

@keyframes scaleIn {
  from {
    transform: scale(0);
    opacity: 0;
  }
  to {
    transform: scale(1);
    opacity: 1;
  }
}
</style>
