<script setup>
import HistoryList from '@/features/mypage/components/HistoryList.vue'
import {ref} from "vue";

const historyItems = ref({
  "success": true,
  "data": {
    "histories": [
      {
        "historyId": 114,
        "serviceType": "게시글 적립",
        "serviceId": 46,
        "increaseAmount": 3,
        "decreaseAmount": null,
        "balance": 23,
        "createdAt": "2025-04-30T19:15:05"
      },
      {
        "historyId": 112,
        "serviceType": "첫 로그인 적립",
        "serviceId": null,
        "increaseAmount": 10,
        "decreaseAmount": null,
        "balance": 20,
        "createdAt": "2025-04-30T15:55:03"
      },
      {
        "historyId": 109,
        "serviceType": "첫 로그인 적립",
        "serviceId": null,
        "increaseAmount": 10,
        "decreaseAmount": null,
        "balance": 10,
        "createdAt": "2025-04-27T19:59:03"
      }
    ],
    "pagination": {
      "currentPage": 1,
      "totalPage": 1,
      "totalItems": 3
    }
  },
  "errorCode": null,
  "message": null,
  "timestamp": "2025-04-30T19:16:30.542604"
})


</script>

<template>
  <div class="w-full max-w-4xl mx-auto">
    <h2 class="text-heading3 mb-4">포인트 내역</h2>
    <HistoryList
        :histories="historyItems.data.histories"
        :pagination="historyItems.data.pagination"
    />
  </div>
</template>

<style scoped>
</style>