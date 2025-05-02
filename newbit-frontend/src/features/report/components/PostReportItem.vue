<script setup>
const props = defineProps({
  reportData: {
    type: Object,
    required: false,
    default: () => ({
      postId: "123",
      category: "자유게시판",
      authorId: "321",
      createdAt: "2025-04-20 09:15:30",
      postTitle: "부적절한 내용의 게시글입니다.",
      postContent: "이 게시글은 부적절한 내용을 포함하고 있습니다.",
      reporterId: "1231",
      reportType: "혐오 표현, 차별",
      status: "처리중",
    }),
  },
});
</script>

<template>
  <div class="report-item">
    <div class="report-info">
      <div class="info-row">
        <div class="info-group">
          <span class="label">게시글 ID:</span>
          <span class="value">{{ reportData.postId }}</span>
        </div>
        <div class="info-group">
          <span class="label">카테고리:</span>
          <span class="value">{{ reportData.category }}</span>
        </div>
        <div class="info-group">
          <span class="label">작성자:</span>
          <span class="value">{{ reportData.authorId }}</span>
        </div>
        <div class="info-group">
          <span class="label">작성일:</span>
          <span class="value">{{ reportData.createdAt }}</span>
        </div>
      </div>
    </div>
    <div class="report-content">
      <h3>{{ reportData.postTitle }}</h3>
      <p>{{ reportData.postContent }}</p>
    </div>
    <div class="report-status">
      <div class="status-group">
        <span class="label">신고자:</span>
        <span class="value">{{ reportData.reporterId }}</span>
      </div>
      <div class="status-group">
        <span class="label">신고 유형:</span>
        <span class="value">{{ reportData.reportType }}</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.report-item {
  background: #fff;
  border-radius: 8px;
  padding: 1.5rem;
  margin-bottom: 1rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.report-info {
  margin-bottom: 1rem;
}

.info-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
}

.info-group,
.status-group {
  display: flex;
  gap: 0.5rem;
}

.label {
  color: #666;
  font-size: 0.9rem;
}

.value {
  font-weight: 500;
}

.report-content {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 4px;
  margin-bottom: 1rem;
}

.report-content h3 {
  margin: 0 0 0.5rem 0;
  font-size: 1.1rem;
}

.report-content p {
  margin: 0;
  color: #666;
}

.report-status {
  display: flex;
  justify-content: space-between;
  padding-top: 1rem;
  border-top: 1px solid #dee2e6;
}

.status-처리중 {
  color: #f59f00;
}

.status-완료 {
  color: #37b24d;
}

.status-반려 {
  color: #f03e3e;
}
</style>
