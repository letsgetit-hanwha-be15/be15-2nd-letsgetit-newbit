import api from './axios.js'

// 테스트용 임시 토큰
const token = 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwiYXV0aG9yaXR5IjoiTUVOVE9SIiwidXNlcklkIjoxMiwiaWF0IjoxNzQ2NDY1Njg5LCJleHAiOjE3NDY0Njc0ODl9.Q7GfPgiNB7G7SaQUw12WUqid4bs2oPktS1ALRBoBe-rY_d47HAX-eYNEjf1n-060fDPbE_U1UM4BhOxYReroTQ'

/* 1. 멘토 정보 조회 */
export const getMentorById = (mentorId) => {
    // const token = localStorage.getItem('accessToken') // 또는 Pinia 등에서 가져오기

    return api.get(`/user/mentor/${mentorId}`, {
        headers: {
            Authorization: `Bearer ${token}`
        }
    });
}

/* 2. 멘토 커피챗 정보 수정 */
export const patchMentorCoffeechatInfo = (data) => {
    return api.patch(
        '/user/users/me/coffeechat-info',
        data,
        {
            headers: {
                Authorization: `Bearer ${token}`
            }
        }
    );
};

/* 3. 멘토 소개 정보 수정 */
export const patchMentorIntroduction = (data) => {
    return api.patch(
        '/user/users/me/introduction-info',
        data,
        {
            headers: {
                Authorization: `Bearer ${token}`
            }
        }
    );
};