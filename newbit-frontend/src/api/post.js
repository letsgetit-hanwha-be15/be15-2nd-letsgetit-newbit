import api from './axios'

export function getPostUserId(userId, page = 0, size = 8) {
    return api.get(`/feature/posts/user/${userId}`, {
        params: {
            page,
            size
        }
    })
}