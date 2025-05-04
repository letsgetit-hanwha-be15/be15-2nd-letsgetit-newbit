<script setup>
import HistoryList from '@/features/mypage/components/HistoryList.vue'
import {ref} from "vue";
import PagingBar from "@/components/common/PagingBar.vue";
const historyType = 'diamond'

const handlePageChange = (page) => {
  // API 호출 or emit
  console.log('이동할 페이지:', page)
}

const historyItems = ref({
      "success": true,
      "data": {
        "histories": [
          {
            "historyId": 23,
            "serviceType": "COFFEECHAT",
            "serviceId": 17,
            "increaseAmount": null,
            "decreaseAmount": 500,
            "balance": 2000,
            "createdAt": "2025-04-27T20:07:38",
            "serviceTitleOrUserNickname": "길동이"
          },
          {
            "historyId": 23,
            "serviceType": "COLUMN",
            "serviceId": 1,
            "increaseAmount": null,
            "decreaseAmount": 40,
            "balance": 2000,
            "createdAt": "2025-04-27T20:07:38",
            "serviceTitleOrUserNickname": "강한 사람이 되는 방법"
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
      "timestamp": "2025-04-30T19:50:53.531543"
    }
)


</script>

<template>
  <div class="w-full max-w-4xl mx-auto p-6">
    <h2 class="text-heading3 mb-4">다이아 내역</h2>
    <HistoryList
        :histories="historyItems.data.histories"
        :pagination="historyItems.data.pagination"
        :type = "historyType"
    />
    <PagingBar
        :currentPage="historyItems.data.pagination.currentPage"
        :totalPage="historyItems.data.pagination.totalPage"
        @page-change="handlePageChange"
    />
  </div>
</template>

<style scoped>
</style>