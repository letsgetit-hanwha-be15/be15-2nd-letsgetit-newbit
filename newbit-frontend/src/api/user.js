/* user 관련 api 호출 */
import api from './axios.js'

/* 1. 회원 가입 */
export function SignUpUser(data) {
    return api.post('/user/users/signup', data);
}


const token = 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwiYXV0aG9yaXR5IjoiTUVOVE9SIiwidXNlcklkIjoxMSwiaWF0IjoxNzQ2NDUwNjc1LCJleHAiOjE3NDY0NTI0NzV9.j4i-ZSNksxJvDFoNrhtV2S8PZWgu9693Yw8eiI7MXGBz3Sd-HHucvjfqBRkc4dc76aRdxOrwdZHWOOHhxgvbIQ'
/* 2. 회원 정보 조회 */
export function getUserInfo(data) {
    return api.get('/user/users/me', {
        headers: {
            Authorization: `Bearer ${token}`
        }
    });
}

/* 3. 회원 정보 수정 */
export function putUserInfo(data) {
    return api.put('/user/users/me/info', data, {
        headers: {
            Authorization: `Bearer ${token}`
        }
    });
}
