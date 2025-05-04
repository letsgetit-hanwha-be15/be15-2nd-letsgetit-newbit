<script setup>
import HistoryList from '@/features/mypage/components/HistoryList.vue'
import {ref} from "vue";
import PagingBar from "@/components/common/PagingBar.vue";

const handlePageChange = (page) => {
  // API 호출 or emit
  console.log('이동할 페이지:', page)
}

const historyType = 'settlement'
const historyItems = ref({
      "success": true,
      "data": {
        "settlements": [
          {
            "settlementId": 1,
            "settlementYear": 2024,
            "settlementMonth": 3,
            "settlementAmount": 100000.00,
            "settledAt": "2025-05-04T18:35:14"
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
      "timestamp": "2025-05-04T18:35:53.859715"
    }
)


</script>

<template>
  <div class="w-full max-w-4xl mx-auto p-6">
    <h2 class="text-heading3 mb-6">월별 정산 내역</h2>
    <HistoryList
        :histories="historyItems.data.settlements"
        :pagination="historyItems.data.pagination"
        :type="historyType"
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