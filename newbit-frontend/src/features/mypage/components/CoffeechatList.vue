<script setup>

import CoffeechatItem from "@/features/mypage/components/CoffeechatItem.vue";

const {coffeechats} = defineProps({
  coffeechats: {
    type: Array,
    required: true
  }
})
</script>

<template>
  <div>
    <CoffeechatItem
        v-for="coffeechat in coffeechats"
        :key="coffeechat.coffeechatId"
        :coffeechat="coffeechat"
    />
  </div>
</template>

<style scoped>

</style>