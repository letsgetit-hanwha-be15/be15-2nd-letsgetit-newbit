<script setup>
import HistoryList from '@/features/mypage/components/HistoryList.vue'
import {ref} from "vue";

const historyType = 'sale'
const historyItems = ref({
      "success": true,
      "data": {
        "saleHistories": [
          {
            "settlementHistoryId": null,
            "settledAt": null,
            "saleAmount": 100000.00,
            "serviceType": "COFFEECHAT",
            "serviceId": 15,
            "createdAt": "2025-04-27T01:09:40",
            "updatedAt": "2025-04-27T01:09:40",
            "mentorId": 6,
            "serviceTitleOrUserNickname": "길동이1",
            "settled": false
          },
          {
            "settlementHistoryId": null,
            "settledAt": "2025-04-27T01:09:40",
            "saleAmount": 500.00,
            "serviceType": "COLUMN",
            "serviceId": 1,
            "createdAt": "2025-04-27T01:09:40",
            "updatedAt": "2025-04-27T01:09:40",
            "mentorId": 6,
            "serviceTitleOrUserNickname": "길동이1",
            "settled": false
          },
          {
            "settlementHistoryId": null,
            "settledAt": "2025-04-27T01:09:40",
            "saleAmount": 500.00,
            "serviceType": "COLUMN",
            "serviceId": 1,
            "createdAt": "2025-04-27T01:09:40",
            "updatedAt": "2025-04-27T01:09:40",
            "mentorId": 6,
            "serviceTitleOrUserNickname": "길동이1",
            "settled": false
          }
        ],
        "pagination": {
          "currentPage": 1,
          "totalPage": 1,
          "totalItems": 1
        }
      },
      "errorCode": null,
      "message": null,
      "timestamp": "2025-04-30T19:56:42.284372"
    }
)


</script>

<template>
  <div class="w-full max-w-4xl mx-auto p-6">
    <h2 class="text-heading3 mb-6">내 판매 내역</h2>
    <HistoryList
        :histories="historyItems.data.saleHistories"
        :pagination="historyItems.data.pagination"
        :type="historyType"
    />
  </div>
</template>

<style scoped>
</style>