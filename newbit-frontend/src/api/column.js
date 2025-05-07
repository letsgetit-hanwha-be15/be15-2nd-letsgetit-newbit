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

// 멘토 본인 칼럼 목록 조회
export const getMyColumnList = (page = 0, size = 10) =>
    api.get('feature/columns/my', { params: { page, size } })


/* --- 칼럼 요청 관련 --- */

// 칼럼 등록 요청
export const createColumnRequest = (data) =>
    api.post('feature/columns/requests', data)

// 칼럼 수정 요청
export const updateColumnRequest = (columnId, data) =>
    api.post(`feature/columns/requests/${columnId}/edit`, data)

// 칼럼 삭제 요청
export const deleteColumnRequest = (columnId, data) =>
    api.post(`feature/columns/requests/${columnId}/delete`, data)

// 본인 칼럼 요청 목록 조회
export const getMyColumnRequests = (params) =>
    api.get('feature/columns/requests/my', { params })


/* --- 칼럼 관리자 승인/거절 --- */

// 등록 요청 승인/거절
export const approveCreateColumn = (data) =>
    api.post('feature/columns/requests/approve/create', data)

export const rejectCreateColumn = (data) =>
    api.post('feature/columns/requests/reject/create', data)

// 수정 요청 승인/거절
export const approveUpdateColumn = (data) =>
    api.post('feature/columns/requests/approve/update', data)

export const rejectUpdateColumn = (data) =>
    api.post('feature/columns/requests/reject/update', data)

// 삭제 요청 승인/거절
export const approveDeleteColumn = (data) =>
    api.post('feature/columns/requests/approve/delete', data)

export const rejectDeleteColumn = (data) =>
    api.post('feature/columns/requests/reject/delete', data)


/* --- 칼럼 이미지 업로드 --- */

// 썸네일 업로드
export const uploadColumnThumbnail = (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return api.post('feature/columns/thumbnails', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
    })
}

// 본문 이미지 업로드
export const uploadColumnContentImage = (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return api.post('feature/columns/content-images', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
    })
}

/* --- 시리즈 관련 --- */

// 시리즈 생성
export const createSeries = (data) =>
    api.post('feature/series', data)

// 시리즈 수정
export const updateSeries = (seriesId, data) =>
    api.patch(`feature/series/${seriesId}`, data)

// 시리즈 삭제
export const deleteSeries = (seriesId) =>
    api.delete(`feature/series/${seriesId}`)

// 시리즈 상세 조회
export const getSeriesDetail = (seriesId) =>
    api.get(`feature/series/${seriesId}`)

// 본인 시리즈 목록 조회 (멘토)
export const getMySeriesList = (page = 0, size = 10) =>
    api.get('feature/series/my', { params: { page, size } })

// 시리즈에 포함된 칼럼 목록 조회
export const getSeriesColumns = (seriesId, page = 0, size = 10) =>
    api.get(`feature/series/${seriesId}/columns`, { params: { page, size } })

// 공개된 시리즈 목록 조회
export const getPublicSeriesList = (page = 0, size = 10) =>
    api.get('feature/series', { params: { page, size } })

// 공개된 시리즈 검색
export const searchPublicSeriesList = (condition, page = 0, size = 10) =>
    api.get('feature/series/public-list/search', {
        params: { ...condition, page, size },
    })


/* --- 시리즈 이미지 업로드 --- */

// 시리즈 썸네일 업로드
export const uploadSeriesThumbnail = (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return api.post('feature/series/thumbnail', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
    })
}
