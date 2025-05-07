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

/* 5. 비밀번호 찾기 */
export function FindPassword(data) {
    return api.post('/user/users/find-password', data);
}

/* 6. 회원 탈퇴 */
export const deleteUser = (data) => {
    return api.delete('/user/users/me');
};


/* 7. 회원 정보 조회 */
export function getUserInfo(data) {
    return api.get('/user/users/me');
}

/* 8. 회원 정보 수정 */
export function putUserInfo(data) {
    return api.put('/user/users/me/info', data);
}

/* 9. 로그인 */
export function loginUser(data) {
    return api.post('user/auth/login', data);
}

/* 10. 로그아웃 */
export function logoutUser() {
    const accessToken = localStorage.getItem('accessToken');
    const refreshToken = localStorage.getItem('refreshToken'); // ✅ 반드시 존재해야 함

    return api.post('/user/auth/logout', {
        refreshToken: refreshToken // ✅ 정확히 이 key로 보내야 함
    }, {
        headers: {
            Authorization: `Bearer ${accessToken}`
        }
    });
}

/* 11. 리프레시 토큰으로 토큰 재발급 */
export function refreshUserToken() {
    return api.post(`user/auth/refresh`,{})
}

/* 12. 유저 프로필 조회 */
export function getUserProfile(userId) {
    return api.get(`user/users/${userId}/profile`);
}

/* 13. 멘토 프로필 조회 */
export function getMentorProfile(mentorId) {
    return api.get(`user/users/mentor/${mentorId}/profile`);
}

/* 14. 회원 포인트, 다이아 잔여량 조회 */
export function getUserBalance() {
    return api.get('/user/users/me/balance');
}