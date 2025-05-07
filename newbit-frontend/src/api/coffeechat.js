import axios from "axios";
import api from "@/api/axios.js";


/* 1. 커피챗 등록 */
export function createCoffeechat(payload) {
    return api.post("/feature/coffeechats", payload)
}

/* 2. 멘티가 커피챗 조회 */
export function getMenteeCoffeechats(params) {
    return api.get("/feature/coffeechats/mentees/me", {
        params: params
    })
}

/* 3. 멘토가 커피챗 조회 */
export function getMentorCoffeechats(params) {
    return api.get("/feature/coffeechats/mentors/me", {
        params: params
    })
}

/* 4. 커피챗 상세 조회 */
export function getCoffeechatById(coffeechatId) {
    return api.get(`/feature/coffeechats/${coffeechatId}`)
}

/* 5. 커피챗별 요청시간 조회 */
export function getRequestTimes(coffeechatId) {
    return api.get(`/feature/coffeechats/${coffeechatId}/request-times`)
}

/* 6. 멘토 리뷰 조회(프로필) */
export const getMentorReviews = (mentorId, page = 0, size = 5) => {
    return api.get(`/feature/reviews/mentors/${mentorId}`, {
        params: {
            page,
            size
        }
    })
}