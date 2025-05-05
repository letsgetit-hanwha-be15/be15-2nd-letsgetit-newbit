<script setup>
import { ref, computed, onMounted } from "vue";
import PostReportList from "../components/PostReportList.vue";
import CommentReportList from "../components/CommentReportList.vue";
import {
  getReportedPosts,
  getReportedComments,
  getReportsByStatusAndType,
} from "@/api/report.js";
import dayjs from "dayjs";

const activeTab = ref("게시글 신고");
const reportTypes = ref([
  "욕설/비하",
  "혐오 표현, 차별",
  "광고, 판매, 스팸",
  "개인정보 노출, 사생활 침해",
  "기타",
]);
const reportTypeMap = {
  "욕설/비하": 1,
  "혐오 표현, 차별": 2,
  "광고, 판매, 스팸": 3,
  "개인정보 노출, 사생활 침해": 4,
  기타: 5,
};
const reportStatusMap = {
  미처리: "PENDING",
  검토중: "IN_REVIEW",
  정지됨: "SUSPENDED",
  삭제됨: "DELETED",
};
const reportStatuses = ref(["미처리", "검토중", "정지됨", "삭제됨"]);
const selectedReportType = ref("");
const selectedReportStatus = ref("");
const loading = ref(false);
const error = ref(null);
const sortType = ref("latest");
const authorId = ref("");
const reporterId = ref("");

const postReports = ref([]);
const commentReports = ref([]);
const currentPage = ref(0);
const pageSize = ref(10);
const totalItems = ref(0);
const totalPages = computed(() => Math.ceil(totalItems.value / pageSize.value));

const currentReports = computed(() => {
  return activeTab.value === "게시글 신고"
    ? postReports.value
    : commentReports.value;
});

const totalReports = computed(() => {
  return totalItems.value;
});

const formatDate = (dateStr) => {
  if (!dateStr) return "-";
  return dayjs(dateStr).format("YYYY.MM.DD HH:mm");
};

const loadReportData = async () => {
  try {
    loading.value = true;
    error.value = null;

    if (activeTab.value === "게시글 신고") {
      const response = await getReportedPosts(
        currentPage.value,
        pageSize.value,
        sortType.value
      );
      const pageData = response.data.data;
      postReports.value = pageData.content.map((post) => ({
        id: post.postId,
        title: post.postTitle,
        lastContent: post.lastReportContent,
        count: post.reportCount,
        lastDate: formatDate(post.lastReportDate),
        status: getStatusInKorean(post.lastReportStatus),
      }));
      totalItems.value = pageData.totalElements;
    } else {
      const response = await getReportedComments(
        currentPage.value,
        pageSize.value,
        sortType.value
      );
      const pageData = response.data.data;
      commentReports.value = pageData.content.map((comment) => ({
        id: comment.commentId,
        content: comment.commentContent,
        lastContent: comment.lastReportContent,
        count: comment.reportCount,
        lastDate: formatDate(comment.lastReportDate),
        status: getStatusInKorean(comment.lastReportStatus),
      }));
      totalItems.value = pageData.totalElements;
    }
  } catch (err) {
    error.value = "신고 데이터를 불러오는 중 오류가 발생했습니다.";
    console.error("Error loading report data:", err);
  } finally {
    loading.value = false;
  }
};

const getStatusInKorean = (status) => {
  const statusMap = {
    PENDING: "미처리",
    IN_REVIEW: "검토중",
    SUSPENDED: "정지됨",
    DELETED: "삭제됨",
  };
  return statusMap[status] || status;
};

const handleTabChange = (tab) => {
  activeTab.value = tab;
  loadReportData();
};

const resetFilters = () => {
  selectedReportType.value = "";
  selectedReportStatus.value = "";
  authorId.value = "";
  reporterId.value = "";
  sortType.value = "latest";
  currentPage.value = 0;
  loadReportData();
};

const applyFilters = async () => {
  try {
    loading.value = true;
    error.value = null;

    // ENUM 값 그대로 전달
    const status = selectedReportStatus.value
      ? reportStatusMap[selectedReportStatus.value]
      : null;
    const reportTypeId = selectedReportType.value
      ? reportTypeMap[selectedReportType.value]
      : null;

    const params = {
      status,
      reportTypeId,
      page: currentPage.value,
      size: pageSize.value,
      sortType: sortType.value,
    };
    if (authorId.value) params.authorId = authorId.value;
    if (reporterId.value) params.reporterId = reporterId.value;

    const response = await getReportsByStatusAndType(
      params.status,
      params.reportTypeId,
      params.page,
      params.size,
      params.sortType,
      params.authorId,
      params.reporterId
    );

    const reportsData = response.data.data.content;

    if (activeTab.value === "게시글 신고") {
      postReports.value = reportsData
        .filter((report) => report.postId)
        .map((report) => ({
          id: report.id,
          title: report.postTitle || "제목 없음",
          lastContent: report.content,
          count: report.reportCount || 1,
          lastDate: formatDate(report.createdAt),
          status: getStatusInKorean(report.status),
        }));
    } else {
      commentReports.value = reportsData
        .filter((report) => report.commentId)
        .map((report) => ({
          id: report.id,
          content: report.commentContent || "내용 없음",
          lastContent: report.content,
          count: report.reportCount || 1,
          lastDate: formatDate(report.createdAt),
          status: getStatusInKorean(report.status),
        }));
    }
    totalItems.value = response.data.data.totalElements;
  } catch (err) {
    error.value = "필터 적용 중 오류가 발생했습니다.";
    console.error("Error applying filters:", err);
  } finally {
    loading.value = false;
  }
};

const handleSortChange = (e) => {
  sortType.value = e.target.value;
  loadReportData();
};

const handlePageChange = (page) => {
  currentPage.value = page - 1; // API는 0-based indexing 사용
  loadReportData();
};

const handleAction = ({ reportId, action }) => {
  console.log(`Action ${action} for report ${reportId}`);
};

onMounted(() => {
  loadReportData();
});
</script>

<template>
  <section class="report-admin-container">
    <!-- 탭 -->
    <div class="tabs">
      <span
        :class="['tab', { active: activeTab === '게시글 신고' }]"
        @click="handleTabChange('게시글 신고')"
      >
        게시글 신고
      </span>
      <span
        :class="['tab', { active: activeTab === '댓글 신고' }]"
        @click="handleTabChange('댓글 신고')"
      >
        댓글 신고
      </span>
    </div>
    <!-- 필터 -->
    <div class="filters text-13px-regular">
      <select v-model="selectedReportType">
        <option value="">신고 유형</option>
        <option v-for="type in reportTypes" :key="type" :value="type">
          {{ type }}
        </option>
      </select>
      <select v-model="selectedReportStatus">
        <option value="">신고 상태</option>
        <option v-for="status in reportStatuses" :key="status" :value="status">
          {{ status }}
        </option>
      </select>
      <input v-model="authorId" placeholder="작성자 ID" />
      <input v-model="reporterId" placeholder="신고자 ID" />
      <select v-model="sortType">
        <option value="latest">최신순</option>
        <option value="count">신고 많은 순</option>
      </select>
      <div class="filter-btns">
        <button class="btn-search" @click="applyFilters">조회</button>
        <button class="btn-reset" @click="resetFilters">초기화</button>
      </div>
    </div>
    <!-- 리스트 -->
    <div v-if="loading" class="loading">불러오는 중...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else>
      <PostReportList
        v-if="activeTab === '게시글 신고'"
        :reports="postReports"
        :total-reports="totalReports"
        :current-page="currentPage + 1"
        :total-pages="totalPages"
        @update:current-page="handlePageChange"
        @action="handleAction"
      />
      <CommentReportList
        v-else
        :reports="commentReports"
        :total-reports="totalReports"
        :current-page="currentPage + 1"
        :total-pages="totalPages"
        @update:current-page="handlePageChange"
        @action="handleAction"
      />
    </div>
  </section>
</template>

<style scoped>
.report-admin-container {
  margin: 0 auto;
  padding: 8px 16px 0 16px;
  max-width: 1100px;
  background: #fff;
}

.title {
  color: #000;
  margin-bottom: 24px;
}

.tabs {
  display: flex;
  gap: 24px;
  border-bottom: 1px solid #dfdfde;
  margin-bottom: 0;
  padding-left: 0px;
  padding-top: 0;
}
.tab {
  padding: 12px 0 10px 0;
  font-size: 1.1rem;
  font-weight: 500;
  color: #a0a0a0;
  border-bottom: 2px solid transparent;
  cursor: pointer;
  font-family: "Noto Sans", sans-serif;
  font-weight: 700;
  font-size: 19px;
  letter-spacing: 0px;
  line-height: normal;
  font-style: normal;
}
.tab.active {
  color: #038ffd;
  font-weight: bold;
  border-bottom: 2.5px solid #038ffd;
  background: none;
}
.filters {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin: 24px auto 20px auto;
  align-items: center;
  background: #f8f9fa;
  padding: 18px 20px 12px 20px;
  border-radius: 10px;
  border: 1px solid #dfdfde;
  max-width: 900px;
  justify-content: center;
}
.filters input,
.filters select {
  padding: 6px 12px;
  border: 1px solid #dfdfde;
  border-radius: 4px;
  color: #000;
  flex-grow: 1;
  flex-basis: 150px;
  min-width: 120px;
  font-family: "Noto Sans", sans-serif;
  font-weight: 400;
  font-size: 13px;
  letter-spacing: 0px;
  line-height: normal;
  font-style: normal;
}
.filters select {
  background-color: #fff;
  cursor: pointer;
}
.filter-btns {
  display: flex;
  gap: 8px;
  justify-content: center;
  align-items: center;
}
.btn-search {
  background: #1976d2;
  color: #fff;
  border: none;
  border-radius: 4px;
  padding: 6px 18px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}
.btn-search:hover {
  background: #1565c0;
}
.btn-reset {
  background: #f1f3f5;
  color: #333;
  border: none;
  border-radius: 4px;
  padding: 6px 18px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}
.btn-reset:hover {
  background: #e9ecef;
}
.status {
  display: inline-block;
  min-width: 64px;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 0.95rem;
  font-weight: 600;
  text-align: center;
  background: #f1f3f5;
  color: #1976d2;
  border: 1.5px solid #b3d8fd;
}
.status.미처리 {
  background: #fff3cd;
  color: #856404;
  border-color: #ffe082;
}
.status.검토중 {
  background: #e3f2fd;
  color: #1976d2;
  border-color: #90caf9;
}
.status.정지됨 {
  background: #ffeaea;
  color: #d32f2f;
  border-color: #ffcdd2;
}
.status.삭제됨 {
  background: #e0e0e0;
  color: #616161;
  border-color: #bdbdbd;
}
.loading,
.error {
  text-align: center;
  padding: 20px;
  color: #000;
}
.error {
  color: #ff4d4f;
}
</style>
