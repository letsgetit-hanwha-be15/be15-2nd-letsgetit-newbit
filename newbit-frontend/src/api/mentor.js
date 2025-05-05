import api from './axios.js'

// 테스트용 임시 토큰
const token = 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwiYXV0aG9yaXR5IjoiTUVOVE9SIiwidXNlcklkIjoxMiwiaWF0IjoxNzQ2NDY0NTMyLCJleHAiOjE3NDY0NjYzMzJ9.4facK42RHIrea7B-6czCLNnR2Wuuzy6nlyWwdUg61BgzPifSs2YXlP4gGwATIRn9uCJoKybvZPxaXb1W5AiSzA'

export const getMentorById = (mentorId) => {
    // const token = localStorage.getItem('accessToken') // 또는 Pinia 등에서 가져오기

    return api.get(`/user/mentor/${mentorId}`, {
        headers: {
            Authorization: `Bearer ${token}`
        }
    });
}