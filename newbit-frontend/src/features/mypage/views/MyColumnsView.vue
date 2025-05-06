<script setup>
import { ref, onMounted } from 'vue';
import { getPurchaseHistory } from '@/api/purchase';
import ColumnCard from "@/features/column/components/ColumnCard.vue";
import PagingBar from "@/components/common/PagingBar.vue";

// const myColumns = ref(null);
const currentPage = ref(1);

// const getMyColumns = async (page = 1) => {
//   try {
//     const response = await fetchMyColumns(page); // page가 있으면 쿼리 파라미터로 전달 필요
//     if (response.data.success) {
//       myColumns.value = response.data.data;
//     } else {
//       console.error('불러오기 실패:', response.data.message);
//     }
//   } catch (e) {
//     console.error('API 호출 오류:', e);
//   }
// };

const myColumns = ref(
    {
      "success": true,
      "data": {
        "content": [
          {
            "columnId": 4,
            "title": "CS 전공지식 핵심 요약",
            "thumbnailUrl": null,
            "price": 0,
            "createdAt": "2025-03-11T09:00:00"
          }
        ],
        "pageable": {
          "pageNumber": 0,
          "pageSize": 10,
          "sort": [],
          "offset": 0,
          "unpaged": false,
          "paged": true
        },
        "last": true,
        "totalPages": 1,
        "totalElements": 1,
        "first": true,
        "size": 10,
        "number": 0,
        "sort": [],
        "numberOfElements": 1,
        "empty": false
      },
      "errorCode": null,
      "message": null,
      "timestamp": "2025-05-06T16:27:26.001644"
    }
)

//
//
//
// const handlePageChange = (page) => {
//   currentPage.value = page;
//   fetchPurchasedColumns(page);
// };
//
// onMounted(() => {
//   fetchPurchasedColumns();
// });
</script>

<template>
  <div class="w-full max-w-4xl mx-auto p-6">
    <h2 class="text-heading3 mb-4">작성한 칼럼</h2>

    <div class="space-y-6">
      <ColumnCard
          v-for="column in myColumns.data.content"
          :key="column.columnId"
          :column="column"
      />

<!--      <PagingBar-->
<!--          :currentPage="purchasedColumns.pagination.currentPage"-->
<!--          :totalPage="purchasedColumns.pagination.totalPage"-->
<!--          @page-change="handlePageChange"-->
<!--      />-->
    </div>

<!--    <div v-else class="text-gray-500 text-sm">작성한 칼럼을 불러오는 중입니다...</div>-->
  </div>
</template>