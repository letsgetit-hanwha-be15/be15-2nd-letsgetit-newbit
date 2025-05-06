import api from './axios.js'

/* 1. 구매한 칼럼 조회 */
export function getPurchaseHistory(page = 1, size = 5) {
    return api.get('/feature/purchase/column/history', {
        params: {
            page,
            size
        }
    });
}