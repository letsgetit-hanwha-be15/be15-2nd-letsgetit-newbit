<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { fetchPostList } from '@/api/post'
import CategorySidebar from '../components/CategorySidebar.vue'
import SortTabs from '../components/SortTabs.vue'
import PostSearchBar from '../components/PostSearchBar.vue'
import PostTable from '../components/PostTable.vue'
import PagingBar from '@/components/common/PagingBar.vue'

// 1. 라우트에서 categoryId 읽기
const route = useRoute()
const selectedCategoryId = ref(route.params.categoryId ? Number(route.params.categoryId) : null)

// 2. 라우트 변경 감지 → categoryId 반영
watch(
    () => route.params.categoryId,
    (newVal) => {
      selectedCategoryId.value = newVal ? Number(newVal) : null
    }
)

const sortOption = ref('latest')
const searchKeyword = ref('')
const currentPage = ref(1)

const onSortChanged = (tab) => sortOption.value = tab
const onSearch = (keyword) => searchKeyword.value = keyword
const onPageChanged = (page) => currentPage.value = page

// 전체 게시글 (카테고리 포함)
const originalPosts = ref([])
const totalPages = ref(1)
const totalItems = ref(0)

async function loadPosts() {
  try {
    const page = currentPage.value - 1 // Spring은 0부터 시작
    const res = await fetchPostList(page, 10, sortOption.value, searchKeyword.value, selectedCategoryId.value)
    originalPosts.value = res.data.content
    totalPages.value = res.data.totalPages
    totalItems.value = res.data.totalElements
  } catch (err) {
    console.error('게시글 불러오기 실패:', err)
  }
}

onMounted(() => {
  loadPosts()
})

watch(currentPage, () => {
  loadPosts()
})

// 3. 검색 + 정렬 + 카테고리 필터 적용
const posts = computed(() => {
  // 공지사항은 항상 보여줌
  const notices = originalPosts.value.filter(post => post.isNotice)

  // 일반 게시글만 필터링
  let filtered = originalPosts.value.filter(post => !post.isNotice)

  if (selectedCategoryId.value !== null) {
    filtered = filtered.filter(post => post.categoryId === selectedCategoryId.value)
  }

  filtered = filtered.filter(
      (post) =>
          post.title.includes(searchKeyword.value) ||
          post.writerNickname.includes(searchKeyword.value)
  )

  const sorted = [...filtered].sort((a, b) => {
    if (sortOption.value === 'popular') {
      return b.likeCount - a.likeCount
    }
    return Number(b.serialNumber) - Number(a.serialNumber)
  })

  return [...notices, ...sorted]
})
</script>

<template>
  <div class="flex max-w-screen-lg mx-auto px-4 gap-6">
    <CategorySidebar />

    <section class="flex-1 p-6">
      <PostSearchBar @search="onSearch" />
      <SortTabs :selected="sortOption" @change="onSortChanged" />
      <PostTable :posts="posts" />
      <PagingBar
          :currentPage="currentPage"
          :totalPages="totalPages"
          :totalItems="totalItems"
          @page-change="onPageChanged"
      />


    </section>
  </div>
</template>