<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
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
const originalPosts = ref([
  { id: 1, title: '[필독] Newbit 게시판 이용 안내', writerNickname: '관리자', categoryId: 1, createdAt: '2025.05.01', likeCount: 365, serialNumber: '공지사항', isNotice: true },
  { id: 2, title: '생산성을 높여주는 개발 툴 추천 리스트', writerNickname: '일산 김기홍', categoryId: 3, createdAt: '2025.05.01', likeCount: 2, serialNumber: 1013 },
  { id: 3, title: '아스날 챔스 우승 축하드립니다. 감사합니다', writerNickname: '서산 김기홍', categoryId: 2, createdAt: '2025.05.01', likeCount: 884, serialNumber: 1012 },
  { id: 4, title:  '저녁 메뉴 추천 받습니다.', writerNickname: '안산 김기홍', categoryId: 1, createdAt: '2025.05.01', likeCount: 0, serialNumber: 1011 },
  { id: 5, title: '오늘 근로자의 날입니다', writerNickname: '부산 김기홍', categoryId: 4, createdAt: '2025.05.01', likeCount: 1, serialNumber: 1010 },
  { id: 6, title: '오버워치 1대1 신청 받습니다.', writerNickname: '마산 김기홍', categoryId: 1, createdAt: '2025.05.01', likeCount: 0, serialNumber: 1009 },
  { id: 7, title: '루미큐브 한판 하실분', writerNickname: '군산 김기홍', categoryId: 2, createdAt: '2025.05.01', likeCount: 0, serialNumber: 1008 },
  { id: 8, title: '요즘 할만한 게임 추천해주세요', writerNickname: '서울 김기홍', categoryId: 1, createdAt: '2025.05.01', likeCount: 3, serialNumber: 1007 },
  { id: 9, title: '회사에서 개발 공부할 수 있는 방법?', writerNickname: '대전 김기홍', categoryId: 5, createdAt: '2025.05.01', likeCount: 5, serialNumber: 1006 },
  { id: 10, title: '카카오 코딩테스트 후기 공유합니다', writerNickname: '인천 김기홍', categoryId: 1, createdAt: '2025.05.01', likeCount: 7, serialNumber: 1005 }
])

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

const totalPages = 10
const totalItems = 100
</script>

<template>
  <div class="flex max-w-screen-lg mx-auto px-4 gap-6">
    <CategorySidebar />

    <section class="flex-1 p-6">
      <div class="text-right mb-4">
        <button
            @click="$router.push('/posts/create')"
            class="bg-blue-500 text-white px-4 py-2 rounded"
        >
          글 작성
        </button>
      </div>
      <PostSearchBar @search="onSearch" />
      <SortTabs :selected="sortOption" @change="onSortChanged" />
      <PostTable :posts="posts" />
      <PagingBar
          :currentPage="currentPage"
          :totalPage="totalPages"
          :totalItems="totalItems"
          @page-change="onPageChanged"
      />


    </section>
  </div>
</template>