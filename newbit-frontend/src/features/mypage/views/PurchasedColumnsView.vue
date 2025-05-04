<script setup>
import {ref} from 'vue';
import ColumnCard from "@/features/column/components/ColumnCard.vue";
import PagingBar from "@/components/common/PagingBar.vue";

const handlePageChange = (page) => {
  // API 호출 or emit
  console.log('이동할 페이지:', page)
}

const purchasedColumns = ref({
  "success": true,
  "data": {
    "columnPurchases": [
      {
        "columnId": 8,
        "columnTitle": "JPA 성능 최적화 팁",
        "thumbnailUrl": null,
        "price": 1000,
        "purchasedAt": "2025-04-18T11:50:10"
      },
      {
        "columnId": 9,
        "columnTitle": "테크 스타트업에서 살아남기",
        "thumbnailUrl": null,
        "price": 170,
        "purchasedAt": "2025-04-11T10:03:38"
      },
      {
        "columnId": 10,
        "columnTitle": "코딩 테스트 전략 정리",
        "thumbnailUrl": null,
        "price": 110,
        "purchasedAt": "2025-04-11T10:03:38"
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
  "timestamp": "2025-05-04T01:12:26.497062"
})
</script>

<template>
  <div class="w-full max-w-4xl mx-auto p-6">
    <h2 class="text-heading3 mb-4">구매한 칼럼</h2>
    <div class="space-y-6">
      <ColumnCard
          v-for="column in purchasedColumns.data.columnPurchases"
          :key="column.columnId"
          :column="column"
      />
    </div>
    <PagingBar
        :currentPage="purchasedColumns.data.pagination.currentPage"
        :totalPage="purchasedColumns.data.pagination.totalPage"
        @page-change="handlePageChange"
    />
  </div>
</template>

<style scoped>

</style>