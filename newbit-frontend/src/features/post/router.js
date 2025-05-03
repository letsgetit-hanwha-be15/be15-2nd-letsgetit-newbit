import PostListView from './views/PostListView.vue'
import PostDetailView from "@/features/post/views/PostDetailView.vue";
import PostCreateView from "@/features/post/views/PostCreateView.vue";
import PostEditView from "@/features/post/views/PostEditView.vue";

export const postRoutes = [
    {
        path: '/posts',
        name: 'PostList',
        component: PostListView
    },
    {
        path: '/posts/create',
        name: 'PostCreate',
        component: PostCreateView
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
    },
    {
        path: '/posts/:postId/edit',
        name: 'PostEdit',
        component: PostEditView,
        props: true
    }
]
