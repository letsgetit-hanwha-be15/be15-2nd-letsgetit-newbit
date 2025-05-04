<script setup>

import CoffeechatItem from "@/features/mypage/components/CoffeechatItem.vue";

const {coffeechats} = defineProps({
  coffeechats: {
    type: Array,
    required: true
  }
})
console.log(coffeechats)
</script>

<template>
  <CoffeechatItem
    v-for="coffeechat in coffeechats"
    :key="coffeechat.coffeechatId"
    :coffeechat="coffeechat"
  />
</template>

<style scoped>

</style>