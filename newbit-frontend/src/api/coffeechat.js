import axios from "axios";
import api from "@/api/axios.js";


/* 1. 커피챗 등록 */
export function createCoffeechat(payload) {
    return api.post("/feature/coffeechats", payload)
}