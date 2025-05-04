<script setup>
import HistoryList from '@/features/mypage/components/HistoryList.vue'
import {ref} from "vue";
import PagingBar from "@/components/common/PagingBar.vue";

const handlePageChange = (page) => {
  // API 호출 or emit
  console.log('이동할 페이지:', page)
}

const historyType = 'point'
const historyItems = ref({
  "success": true,
  "data": {
    "histories": [
      {
        "historyId": 117,
        "serviceType": "첫 로그인 적립",
        "serviceId": null,
        "increaseAmount": 10,
        "decreaseAmount": null,
        "balance": 43,
        "createdAt": "2025-05-03T23:55:40",
        "serviceTitleOrUserNickname": null
      },
      {
        "historyId": 116,
        "serviceType": "첫 로그인 적립",
        "serviceId": null,
        "increaseAmount": 10,
        "decreaseAmount": null,
        "balance": 33,
        "createdAt": "2025-05-02T16:59:06",
        "serviceTitleOrUserNickname": null
      },
      {
        "historyId": 115,
        "serviceType": "첫 로그인 적립",
        "serviceId": null,
        "increaseAmount": 10,
        "decreaseAmount": null,
        "balance": 8218,
        "createdAt": "2025-05-02T16:48:19",
        "serviceTitleOrUserNickname": null
      },
      {
        "historyId": 114,
        "serviceType": "게시글 적립",
        "serviceId": 46,
        "increaseAmount": 3,
        "decreaseAmount": null,
        "balance": 23,
        "createdAt": "2025-04-30T19:15:05",
        "serviceTitleOrUserNickname": null
      },
      {
        "historyId": 113,
        "serviceType": "첫 로그인 적립",
        "serviceId": null,
        "increaseAmount": 10,
        "decreaseAmount": null,
        "balance": 8208,
        "createdAt": "2025-04-30T15:59:33",
        "serviceTitleOrUserNickname": null
      },
      {
        "historyId": 112,
        "serviceType": "첫 로그인 적립",
        "serviceId": null,
        "increaseAmount": 10,
        "decreaseAmount": null,
        "balance": 20,
        "createdAt": "2025-04-30T15:55:03",
        "serviceTitleOrUserNickname": null
      },
      {
        "historyId": 109,
        "serviceType": "첫 로그인 적립",
        "serviceId": null,
        "increaseAmount": 10,
        "decreaseAmount": null,
        "balance": 10,
        "createdAt": "2025-04-27T19:59:03",
        "serviceTitleOrUserNickname": null
      },
      {
        "historyId": 104,
        "serviceType": "팁 60제공",
        "serviceId": 15,
        "increaseAmount": null,
        "decreaseAmount": 60,
        "balance": 689,
        "createdAt": "2025-04-27T01:17:10",
        "serviceTitleOrUserNickname": null
      },
      {
        "historyId": 105,
        "serviceType": "팁 60수령",
        "serviceId": 15,
        "increaseAmount": 60,
        "decreaseAmount": null,
        "balance": 8190,
        "createdAt": "2025-04-27T01:17:10",
        "serviceTitleOrUserNickname": null
      },
      {
        "historyId": 106,
        "serviceType": "커피챗 리뷰 적립",
        "serviceId": 8,
        "increaseAmount": 50,
        "decreaseAmount": null,
        "balance": 739,
        "createdAt": "2025-04-27T01:17:10",
        "serviceTitleOrUserNickname": null
      }
    ],
    "pagination": {
      "currentPage": 11,
      "totalPage": 13,
      "totalItems": 140
    }
  },
  "errorCode": null,
  "message": null,
  "timestamp": "2025-05-03T23:57:57.90961"
})


</script>

<template>
  <div class="w-full max-w-4xl mx-auto p-6">
    <h2 class="text-heading3 mb-4">포인트 내역</h2>
    <HistoryList
        :histories="historyItems.data.histories"
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