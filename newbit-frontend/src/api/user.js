/* user 관련 api 호출 */
import api from './axios.js'

/* 1. 회원 가입 */
export function SignUpUser(data) {
    return api.post('/user/users/signup', data);
}


const token = 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwiYXV0aG9yaXR5IjoiTUVOVE9SIiwidXNlcklkIjoxMSwiaWF0IjoxNzQ2NDQzMTU1LCJleHAiOjE3NDY0NDQ5NTV9.eF9IbsNCrvXoL6bZ2uVCJVe6-7Wy-QC3HtbmOMSyiJXq2YfB82v_mr-nw2hO64qHB_ReEnE5CrB-UBQRoDCnIg'
/* 2. 회원 정보 조회 */
export function getUserInfo(data) {
    return api.get('/user/users/me', {
        headers: {
            Authorization: `Bearer ${token}`
        }
    });
}
