import api from './axios'

export function getPostUserId(userId, page = 0, size = 8) {
    return api.get(`/feature/posts/user/${userId}`, {
        params: {
            page,
            size
        }
    })
}

export async function fetchPostList(page = 0, size = 10) {
    return await api.get(`/feature/posts?page=${page}&size=${size}`)
}

export function fetchPostCategories() {
    return api.get('/feature/posts/categories')
}

export function postPost(formData) {
    return api.post('/feature/posts', formData)
}
