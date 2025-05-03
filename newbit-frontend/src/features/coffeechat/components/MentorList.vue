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
<div class="flex flex-wrap justify-between gap-4 space-y-5 p-12">
  <MentorItem
      v-for="mentor in mentors"
      :key="mentor.mentorId"
      :mentor="mentor"
      class="w-full sm:w-[48%] md:w-[30%]"
  />
</div>
</template>

<style scoped>

</style>