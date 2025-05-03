<script setup>

import MentorItem from "@/features/coffeechat/components/MentorItem.vue";

const {mentors} = defineProps({
  mentors: {
    type: Array,
    required: true
  }
})
</script>

<template>
  <MentorItem
      v-for="mentor in mentors"
      :key="mentor.mentorId"
      :mentor="mentor"
  />
</template>

<style scoped>

</style>