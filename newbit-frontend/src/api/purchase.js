import api from './axios.js'

/* 1. 구매한 칼럼 조회 */
export function getPurchaseHistory() {
    return api.get('/feature/purchase/column/history');
}
