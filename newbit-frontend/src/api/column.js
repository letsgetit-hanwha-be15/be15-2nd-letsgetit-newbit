import api from './axios'

/* --- 칼럼 관련 --- */

// 공개된 칼럼 목록 조회 (페이징)
export const getPublicColumnList = (page = 0, size = 10) =>
    api.get('feature/columns/public-list', { params: { page, size } })

// 공개 칼럼 검색
export const searchPublicColumns = (condition, page = 0, size = 10) =>
    api.get('feature/columns/public-list/search', {
        params: { ...condition, page, size },
    })

// 칼럼 상세 조회 (구매자용)
export const getColumnDetail = (columnId, userId) =>
    api.get(`feature/columns/${columnId}/user/${userId}`)

// 멘토 컬럼 목록 조회 요청
export const getMentorColumnList = (mentorId, page = 0, size = 10) => {
    return api.get(`feature/columns/${mentorId}`, {
        params: {
            page,
            size
        }
    })
}

// 좋아요한 칼럼 조회
export const getLikedColumnList = (page = 1, size = 10) =>
    api.get('feature/users/likes/columns', { params: { page, size } })


/* --- 칼럼 요청 관련 --- */

// 칼럼 등록 요청
export const createColumnRequest = (data) =>
    api.post('feature/columns/requests', data)

// 칼럼 수정 요청
export const updateColumnRequest = (columnId, data) =>
    api.post(`feature/columns/requests/${columnId}/edit`, data)

// 본인 칼럼 요청 목록 조회 (멘토용)
export const getMyColumnRequests = (params) =>
    api.get('feature/columns/requests/my', { params })

// 전체 칼럼 요청 목록 조회 (관리자용)
export const getAllColumnRequests = (params) =>
    api.get('feature/columns/admin', { params })


/* --- 칼럼 관리자 승인/거절 --- */

// 등록 요청 승인/거절
export const approveCreateColumn = (data) =>
    api.post('feature/columns/requests/approve/create', data)

export const rejectCreateColumn = (data) =>
    api.post('feature/columns/requests/reject/create', data)


/* --- 시리즈 관련 --- */

// 공개된 시리즈 목록 조회
export const getPublicSeriesList = (page = 0, size = 10) =>
    api.get('feature/series', { params: { page, size } })

// 공개된 시리즈 검색
export const searchPublicSeriesList = (condition, page = 0, size = 10) =>
    api.get('feature/series/public-list/search', {
        params: { ...condition, page, size },
    })

// 구독한 시리즈 조회
export function getSubscribedSeries() {
    return api.get('feature/subscriptions/user/list');
}
// 멘토 리시즈 검색
export const getMentorSeriesList = (mentorId, page = 0, size = 10) => {
    return api.get(`feature/series/mentor/${mentorId}`, {
        params: {
            page,
            size
        }
    })
}

