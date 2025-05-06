<template>
  <div class="report-detail">
    <div v-if="loading" class="loading-container">
      <p class="text-subtitle">데이터를 불러오는 중...</p>
    </div>
    <div v-else-if="error" class="error-container">
      <p class="text-subtitle">{{ error }}</p>
      <button @click="router.go(-1)" class="back-button text-button">
        뒤로 가기
      </button>
    </div>
    <div v-else class="detail-content">
      <div class="report-info-section">
        <h2 class="text-heading3">게시글 신고 정보</h2>
        <div class="info-grid">
          <div class="info-item">
            <span class="label text-13px-regular">게시글 ID:</span>
            <span class="value text-13px-regular">{{ reportData.postId }}</span>
          </div>
          <div class="info-item">
            <span class="label text-13px-regular">카테고리:</span>
            <span class="value text-13px-regular">{{
              reportData.category
            }}</span>
          </div>
          <div class="info-item">
            <span class="label text-13px-regular">작성자:</span>
            <span class="value text-13px-regular">{{
              reportData.authorId
            }}</span>
          </div>
          <div class="info-item">
            <span class="label text-13px-regular">작성일:</span>
            <span class="value text-13px-regular">{{
              reportData.createdAt
            }}</span>
          </div>
        </div>
      </div>

      <div class="post-content-section">
        <h3 class="text-heading3">신고된 게시글 내용</h3>
        <div class="content-box">
          <div class="post-title text-16px-regular">
            {{ reportData.postTitle }}
          </div>
          <div class="post-content text-13px-regular">
            {{ reportData.postContent }}
          </div>
        </div>
      </div>

      <div class="report-history-section">
        <h3 class="text-heading3">신고 내역 (총 {{ totalReports }}건)</h3>
        <div class="report-status-info">
          <div class="info-row">
            <div class="status-item">
              <span class="label text-13px-regular">신고자:</span>
              <span class="value text-13px-regular">{{
                currentReport.reporterId
              }}</span>
            </div>
            <div class="status-item">
              <span class="label text-13px-regular">신고 유형:</span>
              <span class="value text-13px-regular">{{
                currentReport.reportType
              }}</span>
            </div>
            <div class="status-item">
              <span class="label text-13px-regular">신고일시:</span>
              <span class="value text-13px-regular">{{
                currentReport.reportedAt
              }}</span>
            </div>
          </div>
          <div class="report-content">
            <span class="label text-13px-regular">신고 내용:</span>
            <p class="value content-text text-16px-regular">
              {{ currentReport.content }}
            </p>
          </div>
        </div>

        <div class="history-navigation">
          <button
            class="nav-button prev text-button"
            @click="navigateToPrev"
            :disabled="!hasPrevReport"
          >
            <i class="fas fa-chevron-left"></i> 이전 신고
          </button>
          <div class="current-report text-13px-regular">
            {{ currentReportIndex + 1 }} / {{ totalReports }}
          </div>
          <button
            class="nav-button next text-button"
            @click="navigateToNext"
            :disabled="!hasNextReport"
          >
            다음 신고 <i class="fas fa-chevron-right"></i>
          </button>
        </div>
      </div>

      <div class="admin-action-section">
        <h3 class="text-heading3">관리자 조치</h3>
        <div class="action-form">
          <textarea
            v-model="adminComment"
            placeholder="처리 내용을 입력하세요..."
            rows="4"
            class="text-16px-regular"
          ></textarea>
          <div class="action-buttons mx-auto">
            <button class="btn-hold text-button" @click="handleHold">
              보류
            </button>
            <button class="btn-stop text-button" @click="handleStop">
              정지
            </button>
            <button class="btn-delete text-button" @click="handleDelete">
              삭제
            </button>
            <button class="btn-false text-button" @click="handleFalse">
              허위
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { getReportsByPostId, processReport } from "@/api/report.js";

const route = useRoute();
const router = useRouter();
const adminComment = ref("");
const loading = ref(false);
const error = ref(null);

const reportData = ref({
  postId: "",
  category: "",
  authorId: "",
  createdAt: "",
  postTitle: "",
  postContent: "",
});

const reportHistory = ref([]);
const currentReportIndex = ref(0);

const totalReports = computed(() => reportHistory.value.length);
const currentReport = computed(
  () => reportHistory.value[currentReportIndex.value] || {}
);
const hasPrevReport = computed(() => currentReportIndex.value > 0);
const hasNextReport = computed(
  () => currentReportIndex.value < totalReports.value - 1
);

const handleHold = async () => {
  await processReportAction("IN_REVIEW");
};

const handleStop = async () => {
  await processReportAction("SUSPENDED");
};

const handleDelete = async () => {
  await processReportAction("DELETED");
};

const handleFalse = async () => {
  await processReportAction("FALSE_REPORT");
};

const processReportAction = async (status) => {
  if (!currentReport.value.id) return;

  try {
    loading.value = true;
    await processReport(currentReport.value.id, status);

    // 업데이트된 신고 상태를 가져오기 위해 데이터 다시 로드
    await fetchReportData();

    // 관리자 코멘트 초기화
    adminComment.value = "";
  } catch (err) {
    error.value = "처리 중 오류가 발생했습니다: " + err.message;
    console.error("Error processing report:", err);
  } finally {
    loading.value = false;
  }
};

const navigateToPrev = () => {
  if (hasPrevReport.value) {
    currentReportIndex.value--;
  }
};

const navigateToNext = () => {
  if (hasNextReport.value) {
    currentReportIndex.value++;
  }
};

const fetchReportData = async () => {
  const postId = route.params.id;
  if (!postId) return;

  try {
    loading.value = true;
    error.value = null;

    // 게시글 ID로 신고 목록 조회
    const response = await getReportsByPostId(postId);
    const reports = response.data.data.content;

    if (reports && reports.length > 0) {
      // 첫 번째 신고 정보를 기반으로 게시글 데이터 설정
      const firstReport = reports[0];

      reportData.value = {
        postId: firstReport.postId,
        category: firstReport.postCategoryName || "카테고리 없음",
        authorId: firstReport.postUserId,
        createdAt: firstReport.postCreatedAt,
        postTitle: firstReport.postTitle,
        postContent: firstReport.postContent || "내용 없음",
      };

      // 신고 내역 설정
      reportHistory.value = reports.map((report) => ({
        id: report.id,
        reporterId: report.reporterId,
        reportType: report.reportTypeName,
        reportedAt: report.createdAt,
        content: report.content,
      }));

      // 처음 신고 항목을 선택
      currentReportIndex.value = 0;
    } else {
      error.value = "해당 게시글에 대한 신고 내역이 없습니다.";
    }
  } catch (err) {
    error.value = "신고 데이터를 불러오는데 실패했습니다.";
    console.error("Failed to fetch report detail:", err);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchReportData();
});
</script>

<style scoped>
.report-detail {
  max-width: 900px;
  margin: 0 auto;
  padding: 1rem 0.5rem 0.5rem 0.5rem;
  display: flex;
  flex-direction: column;
}

.detail-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.report-info-section,
.post-content-section,
.report-history-section,
.admin-action-section {
  background: #fff;
  padding: 1.5rem;
  border-bottom: 1px solid #e9ecef;
}

.report-info-section:last-child,
.post-content-section:last-child,
.report-history-section:last-child,
.admin-action-section:last-child {
  border-bottom: none;
}

h2 {
  font-size: 1.5rem;
  font-weight: 600;
  margin: 0 0 1.5rem 0;
  color: #333;
}

h3 {
  font-size: 1.25rem;
  font-weight: 600;
  margin: 0 0 1.5rem 0;
  color: #333;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
}

.info-item,
.status-item {
  display: flex;
  gap: 0.5rem;
}

.label {
  color: #666;
  font-size: 0.875rem;
}

.value {
  font-weight: 500;
}

.content-box {
  background: #f8f9fa;
  padding: 1.25rem;
  border-radius: 4px;
}

.post-title {
  font-size: 1.125rem;
  font-weight: 600;
  margin-bottom: 1rem;
  padding-bottom: 0.75rem;
  border-bottom: 1px solid #e9ecef;
}

.post-content {
  white-space: pre-wrap;
  color: #495057;
  font-size: 0.875rem;
  line-height: 1.6;
}

.report-status-info {
  background: #f8f9fa;
  border-radius: 4px;
  padding: 1.25rem;
  margin-bottom: 1rem;
}

.info-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.report-content {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.content-text {
  white-space: pre-wrap;
  background: white;
  padding: 1rem;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  min-height: 80px;
  font-size: 0.875rem;
  line-height: 1.6;
}

.history-navigation {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
  padding: 0.5rem;
}

.current-report {
  font-size: 0.875rem;
  font-weight: 500;
  color: #495057;
}

.nav-button {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: #f8f9fa;
  color: #495057;
  border: 1px solid #e9ecef;
  padding: 0.5rem 1rem;
  font-size: 0.875rem;
  border-radius: 4px;
  cursor: pointer;
}

.nav-button:hover:not(:disabled) {
  background: #e9ecef;
}

.nav-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.action-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #e9ecef;
  border-radius: 4px;
  resize: vertical;
  font-size: 0.875rem;
  line-height: 1.6;
}

.action-buttons {
  display: flex;
  gap: 0.5rem;
}

button {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  font-weight: 500;
  font-size: 0.875rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-hold {
  background: #f59f00;
  color: white;
}

.btn-stop {
  background: #228be6;
  color: white;
}

.btn-delete {
  background: #495057;
  color: white;
}

.btn-false {
  background: #f03e3e;
  color: white;
}

button:hover:not(:disabled) {
  filter: brightness(0.9);
}

.loading-container,
.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  margin: 2rem auto;
  text-align: center;
}

.back-button {
  margin-top: 1rem;
  padding: 0.5rem 1rem;
  background-color: #038ffd;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.back-button:hover {
  background-color: #026bbe;
}
</style>
