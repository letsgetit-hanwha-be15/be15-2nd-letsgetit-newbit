<script setup>
import { ref, onMounted } from 'vue';
import ColumnCard from '@/features/column/components/ColumnCard.vue';
import PagingBar from '@/components/common/PagingBar.vue';
import { getLikedColumnList } from '@/api/column.js';

// 상태
const likedColumns = ref(null);
const currentPage = ref(0);
const selectedTab = ref('column');

// 데이터 조회
const fetchLikedColumns = async (page = 1) => {
  try {
    const response = await getLikedColumnList(page-1); // 서버는 0-based 페이지
    const data = response.data;

    // 필드 가공
    likedColumns.value = {
      pagination: data.pagination,
      likedColumns: data.likedColumns.map((c) => ({
        id: c.columnId,
        title: c.columnTitle,
        mentorNickname: c.authorNickname,
        thumbnailUrl: c.thumbnailUrl,
        diamondCount: c.price,
        createdAt: c.likedAt,
      })),
    };

    currentPage.value = data.pagination.currentPage + 1; // 보여줄 땐 1-based
  } catch (e) {
    console.error('API 호출 오류:', e);
  }
};

// 페이지 변경 핸들러
const handlePageChange = (page) => {
  fetchLikedColumns(page);
};

// 최초 호출
onMounted(() => {
  fetchLikedColumns();
});
</script>

<template>
  <div class="w-full max-w-4xl mx-auto p-6">

    <div class="flex gap-6 mb-6 border-b border-[var(--newbitdivider)] text-13px-regular">
  <span
      :class="[
      'pb-2 cursor-pointer',
      selectedTab === 'column'
        ? 'border-b-2 border-[var(--newbitnormal)] text-[var(--newbitnormal)] font-bold'
        : 'text-[var(--newbitgray)]'
    ]"
      @click="selectedTab = 'column'"
  >칼럼</span>
      <span
          :class="[
      'pb-2 cursor-pointer',
      selectedTab === 'post'
        ? 'border-b-2 border-[var(--newbitnormal)] text-[var(--newbitnormal)] font-bold'
        : 'text-[var(--newbitgray)]'
    ]"
          @click="selectedTab = 'post'"
      >게시글</span>
    </div>

    <!-- 콘텐츠 -->
    <h2 class="text-heading3 mb-4">
      {{ selectedTab === 'column' ? '좋아요한 칼럼' : '좋아요한 게시글' }}
    </h2>

    <div v-if="selectedTab === 'column'">
      <div v-if="likedColumns" class="space-y-6">
        <ColumnCard
            v-for="column in likedColumns.likedColumns"
            :key="column.id"
            :column="column"
        />

        <PagingBar
            :currentPage="currentPage"
            :totalPages="likedColumns.pagination.totalPage"
            @page-change="handlePageChange"
        />
      </div>
      <div v-else class="text-gray-500 text-sm">
        좋아요한 칼럼을 불러오는 중입니다...
      </div>
    </div>
    <div v-if="selectedTab === 'post'">
      준비 중인 기능입니다.
    </div>
  </div>
</template>