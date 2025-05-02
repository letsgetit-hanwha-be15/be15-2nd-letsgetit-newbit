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
  <div class="grid grid-cols-3 gap-6">
    <div class="flex flex-col items-center p-6 border rounded-lg shadow-md text-center bg-white">
      <MentorItem
          v-for="mentor in mentors"
          :key="mentor.mentorId"
          :mentor="mentor"
      />
    </div>
  </div>
</template>

<style scoped>

</style>