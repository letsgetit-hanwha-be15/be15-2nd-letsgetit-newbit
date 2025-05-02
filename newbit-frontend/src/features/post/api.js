import axios from 'axios'

export async function fetchPosts() {
    const res = await axios.get('http://localhost:8080/api/posts')
    return res.data
}