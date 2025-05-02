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
  <div class="flex flex-col">
    <MentorItem
        v-for="mentor in mentors"
        :key="mentor.mentorId"
        :mentor="mentor"
    />
  </div>
</template>

<style scoped>

</style>