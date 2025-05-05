/* user 관련 api 호출 */
import api from './axios.js'

/* 1. 회원 가입 */
export function SignUpUser(data) {
    return api.post('/user/users/signup', data);
}

/* 2. 직무 목록 조회 */
export function FetchJobList() {
    return api.get('/user/jobs');
}

/* 3. 기술 스택 목록 조회 */
export function FetchTechstackList() {
    return api.get('/user/techstacks');
}