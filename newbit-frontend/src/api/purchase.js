import api from "./axios.js";

/* 1. 구매한 칼럼 조회 */
export function getPurchaseHistory(page = 1, size = 5) {
  return api.get("/feature/purchase/column/history", {
    params: {
      page,
      size,
    },
  });
}

/* 2. 포인트로 심리테스트 구매 */
export function purchasePerkByPoint({ userId, serviceId }) {
  return api.post("feature/point/by-type", null, {
    params: {
      userId,
      pointTypeName: "심리테스트",
    },
  });
}

export const purchaseColumn = (payload) =>
    api.post('/feature/purchase/column', payload)