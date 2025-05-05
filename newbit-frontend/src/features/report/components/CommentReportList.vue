<script setup>
import { useRouter } from "vue-router";

const router = useRouter();
const props = defineProps({
  reports: {
    type: Array,
    required: true,
  },
  totalReports: {
    type: Number,
    required: true,
  },
  currentPage: {
    type: Number,
    required: true,
  },
  totalPages: {
    type: Number,
    required: true,
  },
});

const handlePageChange = (page) => {
  emit("update:currentPage", page);
};

const navigateToDetail = (reportId) => {
  router.push(`/admin/comment/${reportId}`);
};
</script>

<template>
  <div class="report-list">
    <table class="report-table">
      <thead>
        <tr>
          <th class="text-13px-bold">ID</th>
          <th class="text-13px-bold">신고 댓글 내용</th>
          <th class="text-13px-bold">마지막 신고내용</th>
          <th class="text-13px-bold">신고수</th>
          <th class="text-13px-bold">마지막 신고 일시</th>
          <th class="text-13px-bold">상태</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="report in reports" :key="report.id">
          <td class="text-13px-regular">{{ report.id }}</td>
          <td>
            <a
              class="report-content text-13px-regular"
              @click="navigateToDetail(report.id)"
            >
              {{ report.content }}
            </a>
          </td>
          <td class="text-13px-regular">{{ report.lastContent }}</td>
          <td class="text-13px-regular">{{ report.count }}</td>
          <td class="text-13px-regular">{{ report.lastDate }}</td>
          <td>
            <span :class="['status', report.status, 'text-13px-bold']">{{
              report.status
            }}</span>
          </td>
        </tr>
      </tbody>
    </table>
    <div class="pagination text-13px-regular">
      <span
        >총 <b>{{ totalReports }}</b
        >건의 댓글 신고가 있습니다.</span
      >
      <div class="page-controls">
        <button
          :disabled="currentPage === 1"
          @click="handlePageChange(currentPage - 1)"
          class="text-button"
        >
          Previous
        </button>
        <button
          v-for="n in totalPages"
          :key="n"
          :class="{ active: n === currentPage, 'text-button': true }"
          v-if="n === 1 || n === totalPages || Math.abs(n - currentPage) <= 1"
          @click="handlePageChange(n)"
        >
          {{ n }}
        </button>
        <span v-if="currentPage < totalPages - 2">...</span>
        <button
          :disabled="currentPage === totalPages"
          @click="handlePageChange(currentPage + 1)"
          class="text-button"
        >
          Next
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.report-list {
  width: 100%;
}

.report-table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 24px;
}

.report-table th,
.report-table td {
  border: 1px solid #e9ecef;
  padding: 12px 16px;
  text-align: center;
}

.report-table th {
  background: #f8f9fa;
  font-weight: 600;
}

.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
}

.page-controls {
  display: flex;
  align-items: center;
  gap: 4px;
}

.page-controls button {
  padding: 4px 10px;
  border: none;
  background: #f5f5f5;
  border-radius: 4px;
  cursor: pointer;
}

.page-controls button.active {
  background: #1976d2;
  color: #fff;
}

.page-controls button:disabled {
  background: #e0e0e0;
  color: #aaa;
  cursor: not-allowed;
}

.report-content {
  color: #000000;
  text-decoration: underline;
  cursor: pointer;
}

.report-content:hover {
  color: #038ffd;
}
</style>
