import axios from "axios";

const api = axios.create({
    baseURL : 'http://localhost:8080/api/v1'
});

/* 멘토 목록 조회 api */
export const getMentors = params => api.get("/user/users/mentors", { params })
