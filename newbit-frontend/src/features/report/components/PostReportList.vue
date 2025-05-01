<script setup>
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

const emit = defineEmits(["update:currentPage", "action"]);

const handlePageChange = (page) => {
  emit("update:currentPage", page);
};

const handleAction = (reportId, action) => {
  emit("action", { reportId, action });
};
</script>

<template>
  <div class="report-list">
    <table class="report-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>신고 게시글 제목</th>
          <th>마지막 신고내용</th>
          <th>신고수</th>
          <th>마지막 신고 일시</th>
          <th>상태</th>
          <th>관리</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="report in reports" :key="report.id">
          <td>{{ report.id }}</td>
          <td>{{ report.title }}</td>
          <td>{{ report.lastContent }}</td>
          <td>{{ report.count }}</td>
          <td>{{ report.lastDate }}</td>
          <td>
            <span :class="['status', report.status]">{{ report.status }}</span>
          </td>
          <td>
            <button
              v-for="action in report.actions"
              :key="action"
              :class="['action-btn', action]"
              @click="handleAction(report.id, action)"
            >
              {{ action }}
            </button>
          </td>
        </tr>
      </tbody>
    </table>
    <div class="pagination">
      <span
        >총 <b>{{ totalReports }}</b
        >건의 게시글 신고가 있습니다.</span
      >
      <div class="page-controls">
        <button
          :disabled="currentPage === 1"
          @click="handlePageChange(currentPage - 1)"
        >
          Previous
        </button>
        <button
          v-for="n in totalPages"
          :key="n"
          :class="{ active: n === currentPage }"
          v-if="n === 1 || n === totalPages || Math.abs(n - currentPage) <= 1"
          @click="handlePageChange(n)"
        >
          {{ n }}
        </button>
        <span v-if="currentPage < totalPages - 2">...</span>
        <button
          :disabled="currentPage === totalPages"
          @click="handlePageChange(currentPage + 1)"
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
  border: 1px solid #e0e0e0;
  padding: 10px 8px;
  text-align: center;
  font-family: var(--newbit-text-13px-regular-font-family);
  font-size: var(--newbit-text-13px-regular-font-size);
}

.report-table th {
  background: #f8f9fa;
  font-weight: var(--newbit-text-13px-bold-font-weight);
}

.status {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: var(--newbit-text-10px-regular-font-size);
  color: #fff;
  display: inline-block;
  font-weight: var(--newbit-text-10px-regular-font-weight);
}

.status.미처리 {
  background: #bdbdbd;
}

.status.검토중 {
  background: #42a5f5;
}

.status.정지됨 {
  background: #ef5350;
}

.status.삭제됨 {
  background: #d32f2f;
}

.action-btn {
  margin: 0 2px;
  padding: 4px 10px;
  border: none;
  border-radius: 4px;
  font-size: var(--newbit-text-10px-regular-font-size);
  font-weight: var(--newbit-text-10px-regular-font-weight);
  cursor: pointer;
  color: #fff;
}

.action-btn.무고 {
  background: #42a5f5;
}

.action-btn.허위 {
  background: #42a5f5;
}

.action-btn.보류 {
  background: #42a5f5;
}

.action-btn.삭제 {
  background: #ef5350;
}

.action-btn.정지 {
  background: #d32f2f;
}

.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
  font-family: var(--newbit-text-13px-regular-font-family);
  font-size: var(--newbit-text-13px-regular-font-size);
}

.pagination b {
  font-weight: var(--newbit-text-13px-bold-font-weight);
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
  font-family: var(--newbit-text-13px-regular-font-family);
  font-size: var(--newbit-text-13px-regular-font-size);
}

.page-controls button:hover {
  /* No change */
}

.page-controls button.active {
  background: #1976d2;
  color: #fff;
  font-weight: var(--newbit-text-13px-bold-font-weight);
}

.page-controls button:disabled {
  background: #e0e0e0;
  color: #aaa;
  cursor: not-allowed;
}
</style>
