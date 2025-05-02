import PostListView from './views/PostListView.vue'
import PostDetailView from "@/features/post/views/PostDetailView.vue";

export const postRoutes = [
    {
        path: '/posts',
        name: 'PostList',
        component: PostListView
    },
    {
        path: '/posts/category/:categoryId',
        name: 'PostByCategory',
        component: PostListView,
        props: true
    },
    {
        path: '/posts/:postId',
        name: 'PostDetail',
        component: PostDetailView,
        props: true
    }
]