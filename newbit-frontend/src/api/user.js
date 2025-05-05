/* user 관련 api 호출 */
import api from './axios.js'

// 테스트용 임시 토큰
const token = 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwiYXV0aG9yaXR5IjoiVVNFUiIsInVzZXJJZCI6MTIsImlhdCI6MTc0NjQ1OTY4NSwiZXhwIjoxNzQ2NDYxNDg1fQ.G8x87QggENeVumWTsmpxIGZEVZ8LwZIap6845c2bKz9HFzSTBXF1BOaJKABxzhviG6Of_ZWbFlr6SZJMiL2KXw'

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

/* 5. 회원 탈퇴 */
export const deleteUser = (data) => {
    return api.delete('/user/users/me', {
        headers: {
            Authorization: `Bearer ${token}`
        },
        data: data
    });
};