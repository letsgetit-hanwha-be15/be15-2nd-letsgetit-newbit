<script setup>
import { ref, onMounted } from 'vue'
import { getPublicColumnList } from '@/api/column'
import dayjs from 'dayjs'

const columns = ref([])

onMounted(async () => {
  try {
    const res = await getPublicColumnList(0, 3)
    console.log('칼럼 응답:', res.data)
    columns.value = res.data.content // ✅ 여기로 수정!
  } catch (e) {
    console.error('칼럼 목록 조회 실패:', e)
  }
})

</script>

<template>
  <div class="w-full h-full p-5 rounded-lg border border-gray-200 bg-white">
    <h2 class="text-heading3 text-newbittext font-bold mb-4">칼럼</h2>

    <div class="space-y-4">
      <div
          v-for="column in columns"
          :key="column.columnId"
          class="flex border border-blue-200 rounded-lg overflow-hidden"
      >
        <!-- 왼쪽: 텍스트 -->
        <div class="flex flex-col justify-between p-4 flex-1">
          <p class="text-base text-newbittext font-semibold line-clamp-2">
            {{ column.title }}
          </p>

          <div class="flex items-center text-gray-500 text-sm mt-2 gap-3">
            <span>❤️ {{ column.likeCount }}</span>
            <span>작성일 {{ dayjs(column.createdAt).format('YYYY.MM.DD') }}</span>
          </div>
        </div>

        <!-- 오른쪽: 썸네일 -->
        <img
            :src="column.thumbnailUrl || '/assets/default-thumbnail.jpg'"
            alt="썸네일"
            class="w-[300px] h-[120px] object-cover"
        />
      </div>
    </div>
  </div>
</template>
