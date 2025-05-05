<script setup>
const { coffeechat, requestTimes } = defineProps({
  coffeechat: {
    type: Object,
    required: true
  },
  requestTimes: {
    type: Array,
    required: true
  }
})

function formatTime(datetimeString) {
  return datetimeString.replace('T', ' ')
}
</script>

<template>
  <div class="space-y-8">
    <div>
      <div class="text-heading3" >커피챗 진행 시간</div>
      <div class="ml-2 mt-1">{{ coffeechat.purchaseQuantity * 30 }} 분</div>
    </div>
    <div>
      <div class="text-heading3">커피챗 요청 시간</div>
      <ul class="ml-2 mt-1">
        <li
            v-for="requestTime in requestTimes"
            :key="requestTime.requestTimeId">
          • {{ formatTime(requestTime.startTime) }} ~ {{formatTime(requestTime.endTime) }}
        </li>
      </ul>
    </div>
    <div>
      <div class="text-heading3">요청 메시지</div>
      <div class="ml-2 mt-1 min-h-20">{{ coffeechat.requestMessage }}</div>
    </div>
  </div>
</template>

<style scoped>

</style>