import api from './axios.js'

/* 1. 멘토 정보 조회 */
export const getMentorById = (mentorId) => {
    return api.get(`/user/mentor/${mentorId}`);
}

/* 2. 멘토 커피챗 정보 수정 */
export const patchMentorCoffeechatInfo = (data) => {
    return api.patch(
        '/user/users/me/coffeechat-info',
        data
    );
};

/* 3. 멘토 소개 정보 수정 */
export const patchMentorIntroduction = (data) => {
    return api.patch(
        '/user/users/me/introduction-info',
        data
    );
};

/* 4. 멘토 목록 조회 */
export const getMentors = (data) => {
    return api.get(
        '/user/users/mentors',
        {
            params: data,
            headers: {
                Authorization: `Bearer ${token}`
            }
        }
    );
};