<script setup>

import CoffeechatItem from "@/features/mypage/components/CoffeechatItem.vue";

const props = defineProps({
  coffeechats: {
    type: Array,
    required: true
  },
  isMentor: {
    type: Boolean,
    required: true
  },
  pagination: {
    type: Object,
    required: true
  }
})
</script>

<template>
  <div>
    <CoffeechatItem
        v-for="coffeechat in props.coffeechats"
        :key="coffeechat.coffeechatId"
        :coffeechat="coffeechat"
        :isMentor="props.isMentor"
    />
  </div>
</template>

<style scoped>

</style>