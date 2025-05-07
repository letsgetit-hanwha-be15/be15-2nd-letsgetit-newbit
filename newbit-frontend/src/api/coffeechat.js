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