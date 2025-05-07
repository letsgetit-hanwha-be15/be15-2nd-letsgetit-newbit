<script setup>
import { ref, onMounted } from 'vue';
import { getPurchaseHistory } from '@/api/purchase';
import ColumnCard from "@/features/column/components/ColumnCard.vue";
import PagingBar from "@/components/common/PagingBar.vue";

const purchasedColumns = ref([]);
const pagination = ref(null);
const currentPage = ref(1);

const fetchPurchasedColumns = async (page = 1) => {
  try {
    const res = await getPurchaseHistory(page); // 서버는 0-based
    if (res.data.success) {
      const data = res.data.data;

      purchasedColumns.value = data.columnPurchases.map((c) => ({
        id: c.columnId,
        title: c.columnTitle,
        thumbnailUrl: c.thumbnailUrl,
        diamondCount: c.price,
        purchasedAt: c.purchasedAt,
      }));

      pagination.value = data.pagination;
      currentPage.value = data.pagination.currentPage;
    }
  } catch (e) {
    console.error('구매한 칼럼 조회 실패:', e);
  }
};

const handlePageChange = (page) => {
  fetchPurchasedColumns(page);
};

onMounted(() => {
  fetchPurchasedColumns();
});
</script>

<template>
  <div class="w-full max-w-4xl mx-auto p-6">
    <h2 class="text-heading3 mb-4">구매한 칼럼</h2>

    <div v-if="purchasedColumns.length > 0" class="space-y-6">
      <ColumnCard
          v-for="column in purchasedColumns"
          :key="column.id"
          :column="column"
      />

      <PagingBar
          v-if="pagination"
          :currentPage="currentPage"
          :totalPages="pagination.totalPage"
          @page-change="handlePageChange"
      />
    </div>

    <div v-else class="text-gray-500 text-sm">구매한 칼럼이 없습니다.</div>
  </div>
</template>