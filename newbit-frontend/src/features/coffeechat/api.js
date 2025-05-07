import axios from "axios";

const api = axios.create({
    baseURL: 'http://localhost:8080/api/v1'
});


/* 커피챗 등록 api */
export function createCoffeechat(payload) {
    return api.post("/feature/coffeechats", payload)
}