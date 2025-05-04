<script setup>
const props = defineProps({
  reportData: {
    type: Object,
    required: false,
    default: () => ({
      commentId: "987",
      postId: "123",
      postTitle: "부적절한 내용의 게시글입니다.",
      authorId: "999",
      createdAt: "2025-04-20 10:00:00",
      commentContent: "이런 댓글은 정말 부적절하다고 생각합니다.",
      reporterId: "8888",
      reportType: "비방, 욕설",
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
          <span class="label">댓글 ID:</span>
          <span class="value">{{ reportData.commentId }}</span>
        </div>
        <div class="info-group">
          <span class="label">원본 게시글:</span>
          <span class="value">{{ reportData.postId }}</span>
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
      <div class="post-preview">
        <h4>원본 게시글</h4>
        <p>{{ reportData.postTitle }}</p>
      </div>
      <div class="comment-content">
        <h4>댓글 내용</h4>
        <p>{{ reportData.commentContent }}</p>
      </div>
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

.post-preview {
  margin-bottom: 1rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #dee2e6;
}

.post-preview h4,
.comment-content h4 {
  margin: 0 0 0.5rem 0;
  font-size: 1rem;
  color: #495057;
}

.post-preview p,
.comment-content p {
  margin: 0;
  color: #666;
}

.comment-content {
  border-left: 4px solid #228be6;
  padding-left: 1rem;
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
