<script setup>
import { ref, onMounted } from 'vue';
import { getPurchaseHistory } from '@/api/purchase'; // ✅ 실제 API 경로
import ColumnCard from "@/features/column/components/ColumnCard.vue";
import PagingBar from "@/components/common/PagingBar.vue";

const purchasedColumns = ref(null);
const currentPage = ref(1);

const fetchPurchasedColumns = async (page = 1) => {
  try {
    const response = await getPurchaseHistory(page); // page가 있으면 쿼리 파라미터로 전달 필요
    if (response.data.success) {
      purchasedColumns.value = response.data.data;
    } else {
      console.error('불러오기 실패:', response.data.message);
    }
  } catch (e) {
    console.error('API 호출 오류:', e);
  }
};

const handlePageChange = (page) => {
  currentPage.value = page;
  fetchPurchasedColumns(page);
};

onMounted(() => {
  fetchPurchasedColumns();
});
</script>

<template>
  <div class="w-full max-w-4xl mx-auto p-6">
    <h2 class="text-heading3 mb-4">구매한 칼럼</h2>

    <div v-if="purchasedColumns" class="space-y-6">
      <ColumnCard
          v-for="column in purchasedColumns.columnPurchases"
          :key="column.columnId"
          :column="column"
      />

      <PagingBar
          :currentPage="purchasedColumns.pagination.currentPage"
          :totalPage="purchasedColumns.pagination.totalPage"
          @page-change="handlePageChange"
      />
    </div>

    <div v-else class="text-gray-500 text-sm">구매한 칼럼을 불러오는 중입니다...</div>
  </div>
</template>