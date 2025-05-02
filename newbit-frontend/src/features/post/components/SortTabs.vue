<script setup>
const props = defineProps({
  selected: String
})
const emit = defineEmits(['change'])

const setTab = (tab) => {
  emit('change', tab)
}
</script>

<template>
  <div class="flex gap-4 border-b mb-4 text-sm font-medium">
    <span
        :class="selected === 'latest'
        ? 'text-blue-600 border-b-2 border-blue-600 pb-1'
        : 'text-gray-600 cursor-pointer'"
        @click="setTab('latest')"
    >
      전체
    </span>
    <span
        :class="selected === 'popular'
        ? 'text-blue-600 border-b-2 border-blue-600 pb-1'
        : 'text-gray-600 cursor-pointer'"
        @click="setTab('popular')"
    >
      인기
    </span>
  </div>
</template>
