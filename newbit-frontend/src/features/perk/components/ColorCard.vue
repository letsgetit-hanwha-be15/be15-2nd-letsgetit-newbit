<template>
  <div
      class="relative aspect-square rounded-lg overflow-hidden cursor-pointer"
      :style="{ backgroundColor: color }"
      :class="{
      'scale-105 shadow-lg': isHovered,
      'scale-100 shadow-md': !isHovered && !isSelected,
      'scale-110 shadow-xl border-2 border-white/80': isSelected,
    }"
      @mouseenter="isHovered = true"
      @mouseleave="isHovered = false"
      @click="handleClick"
  >
    <transition
        enter-active-class="transition-all duration-300"
        leave-active-class="transition-all duration-300"
    >
      <div
          v-if="isSelected"
          class="absolute inset-0 rounded-lg"
          :style="{
          background: `radial-gradient(circle, transparent, ${color})`,
        }"
      />
    </transition>
    <div
        class="absolute bottom-0 left-0 right-0 p-2 text-white text-center flex justify-center items-center"
        :style="{
        backgroundColor: 'rgba(0, 0, 0, 0.5)',
        backdropFilter: 'blur(4px)',
        minHeight: '2.5rem',
      }"
    >
      <p class="font-bold text-base">{{ colorName }}</p>
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
}

div:active {
  transform: scale(0.95);
}
</style>
