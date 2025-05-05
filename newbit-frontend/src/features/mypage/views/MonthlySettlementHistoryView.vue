<script setup>
import { ref, onMounted } from 'vue';
import HistoryList from '@/features/mypage/components/HistoryList.vue';
import PagingBar from '@/components/common/PagingBar.vue';
import { fetchSettlementHistory } from '@/api/history.js';

const historyItems = ref(null);
const historyType = 'settlement';
const isLoading = ref(false);
const error = ref(null);

const loadSettlementHistory = async (page = 1) => {
  isLoading.value = true;
  error.value = null;
  try {
    const { histories, pagination } = await fetchSettlementHistory(page);
    historyItems.value = {
      histories,
      pagination
    };
  } catch (e) {
    error.value = '정산 내역을 불러오는 데 실패했습니다.';
  } finally {
    isLoading.value = false;
  }
};

const handlePageChange = (page) => {
  loadSettlementHistory(page);
};

onMounted(() => {
  loadSettlementHistory();
});
</script>

<template>
  <div class="w-full max-w-4xl mx-auto p-6">
    <h2 class="text-heading3 mb-4">월별 정산 내역</h2>

    <div v-if="isLoading">로딩 중...</div>
    <div v-else-if="error" class="text-red-500">{{ error }}</div>
    <div v-else-if="historyItems && historyItems.histories.length > 0
">
      <HistoryList
          :histories="historyItems.histories"
          :pagination="historyItems.pagination"
          :type="historyType"
      />
      <PagingBar
          :currentPage="historyItems.pagination.currentPage"
          :totalPage="historyItems.pagination.totalPage"
          @page-change="handlePageChange"
      />
    </div>
    <div v-else class="text-gray-400">정산 내역이 없습니다.</div>
  </div>
</template>

