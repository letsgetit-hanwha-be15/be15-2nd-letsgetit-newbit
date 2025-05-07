<script setup>
import { ref, onMounted } from 'vue'
import { searchPublicColumns } from '@/api/column'

const currentIndex = ref(0)
const columns = ref([])

const fetchColumns = async () => {
  try {
    const res = await searchPublicColumns({}, 0, 5)
    columns.value = res.data.data.content
  } catch (e) {
    console.error('공개 칼럼 조회 실패:', e)
  }
}

const prev = () => {
  if (currentIndex.value > 0) currentIndex.value--
}

const next = () => {
  if (currentIndex.value < columns.value.length - 1) currentIndex.value++
}

onMounted(fetchColumns)
</script>

<template>
  <div class="w-full flex flex-col items-center">
    <!-- 썸네일 이미지 -->
    <div class="w-full max-w-[640px] rounded-lg shadow overflow-hidden">
      <img
          :src="columns[currentIndex]?.thumbnailUrl || '/assets/default-thumbnail.jpg'"
          alt="칼럼 썸네일"
          class="w-full aspect-[16/9] object-cover"
      />
    </div>

    <!-- 제목 + 화살표 -->
    <div class="flex justify-between items-center mt-3 w-full max-w-[640px] px-2">
      <p class="text-heading3 text-newbittext font-bold text-left">
        {{ columns[currentIndex]?.title }}
      </p>
      <div class="flex gap-2">
        <button @click="prev" class="bg-gray-200 rounded px-2 py-1 text-lg">⬅</button>
        <button @click="next" class="bg-gray-200 rounded px-2 py-1 text-lg">➡</button>
      </div>
    </div>
  </div>
</template>
