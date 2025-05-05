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

/* 4. 아이디 찾기 */
export function FindId(data) {
    return api.post('/user/users/find-id', data);
}

/* 4. 비밀번호 찾기 */
export function FindPassword(data) {
    return api.post('/user/users/find-password', data);
}