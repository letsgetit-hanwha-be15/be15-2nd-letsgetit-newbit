import api from "./axios.js";

// 관리자: 신고된 게시글 목록 조회
export const getReportedPosts = (page = 0, size = 10, sortType = "latest") => {
  return api.get("/feature/reports/query/reported-posts", {
    params: { page, size, sortType },
  });
};

// 관리자: 신고된 댓글 목록 조회
export const getReportedComments = (
  page = 0,
  size = 10,
  sortType = "latest"
) => {
  return api.get("/feature/reports/query/reported-comments", {
    params: { page, size, sortType },
  });
};

// 관리자: 상태별 신고 목록 조회
export const getReportsByStatus = (status, page = 0, size = 10) => {
  return api.get(`/feature/reports/query`, {
    params: { status, page, size },
  });
};

// 관리자: 게시글 ID로 신고 목록 조회
export const getReportsByPostId = (postId, page = 0, size = 10) => {
  return api.get(`/feature/reports/query/post/${postId}`, {
    params: { page, size },
  });
};

// 관리자: 댓글 ID로 신고 목록 조회
export const getReportsByCommentId = (commentId, page = 0, size = 10) => {
  return api.get(`/feature/reports/query/comment/${commentId}`, {
    params: { page, size },
  });
};

// 관리자: 신고 처리하기
export const processReport = (reportId, status) => {
  return api.patch("/feature/admin/reports/process", { reportId, status });
};

// 관리자: 신고 상태와 유형으로 필터링된 목록 조회
export const getReportsByStatusAndType = (
  status,
  reportTypeId,
  page = 0,
  size = 10
) => {
  return api.get("/feature/reports/query/filter", {
    params: { status, reportTypeId, page, size },
  });
};
