<script setup>
import { ref, computed } from "vue";
import PostReportList from "../components/PostReportList.vue";
import CommentReportList from "../components/CommentReportList.vue";

const activeTab = ref("게시글 신고");
const reportTypes = ref([
  "욕설/비하",
  "혐오 표현, 차별",
  "광고, 판매, 스팸",
  "개인정보 노출, 사생활 침해",
  "기타",
]);
const reportStatuses = ref(["미처리", "검토중", "정지됨", "삭제됨"]);
const selectedReportType = ref("");
const selectedReportStatus = ref("");

// TODO : 테스트용 하드코딩, 백엔드 연결 후 삭제제
const postReports = ref([
  {
    id: 1,
    title: "바보들에게 하는 수업",
    lastContent: "저한테 바보라고 욕했습니다. 아주 혼쭐을 내주세요.",
    count: 43,
    lastDate: "2025-04-22 14:30",
    status: "미처리",
  },
  {
    id: 2,
    title: "소리 지르는 nigger, 음악에 미치는 nigger",
    lastContent: "혐오 표현, 차별이 있음",
    count: 12,
    lastDate: "2025-04-22 14:30",
    status: "검토중",
  },
  {
    id: 3,
    title: "맛도 좋고 몸에도 좋은 뱀 팝니다.",
    lastContent: "판매글을 올렸습니다.",
    count: 33,
    lastDate: "2025-04-22 14:30",
    status: "정지됨",
  },
  {
    id: 4,
    title: "이 칼럼 쓴 사람 전화번호 010-1234-3942",
    lastContent: "개인정보 노출",
    count: 13,
    lastDate: "2025-04-22 14:30",
    status: "삭제됨",
  },
]);

const commentReports = ref([
  {
    id: 1,
    content: "이런 쓰레기 같은 글은 삭제해야 합니다. 작성자는 바보입니다.",
    lastContent: "작성자를 비하하는 발언",
    count: 5,
    lastDate: "2025-04-22 14:30",
    status: "미처리",
  },
  {
    id: 2,
    content: "이런 종자들은 다 죽여야 해",
    lastContent: "혐오 표현, 차별입니다. 처벌해야 합니다.",
    count: 8,
    lastDate: "2025-04-22 14:30",
    status: "검토중",
  },
  {
    id: 3,
    content: "제가 만든 앱입니다. 다운로드 링크: https://...",
    lastContent: "광고를 올렸습니다.",
    count: 12,
    lastDate: "2025-04-22 14:30",
    status: "정지됨",
  },
]);

const currentReports = computed(() => {
  return activeTab.value === "게시글 신고"
    ? postReports.value
    : commentReports.value;
});

const totalReports = computed(() => {
  return activeTab.value === "게시글 신고" ? 124 : 25;
});

const currentPage = ref(1);
const totalPages = 10;

const handlePageChange = (page) => {
  currentPage.value = page;
};

const handleAction = ({ reportId, action }) => {
  console.log(`Action ${action} for report ${reportId}`);
};
</script>

<template>
  <div class="report-admin-container">
    <h1 class="title text-heading1">신고 관리</h1>
    <div class="tabs text-heading3">
      <button
        :class="{ active: activeTab === '게시글 신고' }"
        @click="activeTab = '게시글 신고'"
      >
        게시글 신고
      </button>
      <button
        :class="{ active: activeTab === '댓글 신고' }"
        @click="activeTab = '댓글 신고'"
      >
        댓글 신고
      </button>
    </div>
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
      <input placeholder="작성자 ID" />
      <input placeholder="신고자 ID" />
      <input placeholder="모든 상태" />
    </div>
    <PostReportList
      v-if="activeTab === '게시글 신고'"
      :reports="postReports"
      :total-reports="124"
      :current-page="currentPage"
      :total-pages="totalPages"
      @update:current-page="handlePageChange"
      @action="handleAction"
    />
    <CommentReportList
      v-else
      :reports="commentReports"
      :total-reports="25"
      :current-page="currentPage"
      :total-pages="totalPages"
      @update:current-page="handlePageChange"
      @action="handleAction"
    />
  </div>
</template>

<style scoped>
.report-admin-container {
  margin: 0 auto;
  padding: 32px 16px;
}

.title {
  color: var(--newbittext);
  margin-bottom: 24px;
}

.tabs {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
}

.tabs button {
  padding: 8px 24px;
  border: none;
  background: var(--newbitlightmode);
  border-radius: 6px 6px 0 0;
  color: var(--newbitgray);
  cursor: pointer;
  font-family: var(--newbit-text-heading3-font-family), sans-serif;
  font-weight: var(--newbit-text-heading3-font-weight);
  font-size: var(--newbit-text-heading3-font-size);
  letter-spacing: var(--newbit-text-heading3-letter-spacing);
  line-height: var(--newbit-text-heading3-line-height);
  font-style: var(--newbit-text-heading3-font-style);
}

.tabs button:hover {
  background: var(--newbitlightmode-hover);
}

.tabs button.active {
  background: var(--newbitbackground);
  border-bottom: 2px solid var(--newbitnormal);
  font-weight: bold;
  color: var(--newbittext);
}

.filters {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 20px;
}

.filters input,
.filters select {
  padding: 6px 12px;
  border: 1px solid var(--newbitdivider);
  border-radius: 4px;
  color: var(--newbittext);
  flex-grow: 1;
  flex-basis: 150px;
  min-width: 120px;
  font-family: var(--newbit-text-13px-regular-font-family), sans-serif;
  font-weight: var(--newbit-text-13px-regular-font-weight);
  font-size: var(--newbit-text-13px-regular-font-size);
  letter-spacing: var(--newbit-text-13px-regular-letter-spacing);
  line-height: var(--newbit-text-13px-regular-line-height);
  font-style: var(--newbit-text-13px-regular-font-style);
}

.filters select {
  background-color: var(--newbitbackground);
  cursor: pointer;
}
</style>
