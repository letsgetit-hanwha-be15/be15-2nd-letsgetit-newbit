<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { approveCreateColumn, rejectCreateColumn } from '@/api/column'
import { getAllColumnRequests } from '@/api/column'

const route = useRoute()
const router = useRouter()
const columnId = route.params.columnId
const requestType = route.query.type

const column = ref({})
const approvalDecision = ref(null)
const rejectionReason = ref('')
const fallbackImg = new URL('@/assets/image/product-skeleton.png', import.meta.url).href

const fetchData = async () => {
  try {
    const res = await getAllColumnRequests({ page: 0, size: 100 })
    const all = res.data.data.content
    const found = all.find((item) => item.columnRequestId === Number(columnId))
    if (found) column.value = found
  } catch (e) {
    console.error('상세 정보 불러오기 실패', e)
  }
}

const submitApproval = async () => {
  if (!approvalDecision.value) return alert('승인 또는 반려를 선택해주세요.')
  if (approvalDecision.value === 'reject' && !rejectionReason.value.trim()) {
    return alert('반려 사유를 입력해주세요.')
  }

  const dto = {
    columnRequestId: Number(columnId),
    ...(approvalDecision.value === 'reject' && { reason: rejectionReason.value })
  }

  try {
    if (approvalDecision.value === 'approve') {
      await approveCreateColumn(dto)
    } else {
      await rejectCreateColumn(dto)
    }
    alert('처리 완료')
    router.push('/admin/columns')
  } catch (e) {
    console.error('승인/반려 요청 실패', e)
    alert('요청 처리 중 오류 발생')
  }
}

onMounted(fetchData)
</script>

<template>
  <div class="max-w-[900px] mx-auto py-8 px-4">
    <router-link to="/admin/columns" class="inline-flex gap-2 mb-6">
      <span class="text-xl">←</span> <span>목록으로</span>
    </router-link>

    <div class="flex gap-6 mb-6">
      <img :src="column.thumbnailUrl || fallbackImg" @error="(e) => (e.target.src = fallbackImg)" class="w-[280px] h-[180px] object-cover rounded-lg" />
      <div class="flex flex-col justify-between h-[180px] flex-1">
        <h1 class="text-heading2">{{ column.title }}</h1>
        <div class="text-[var(--newbitgray)] text-13px-regular">
          <p>멘토: {{ column.mentorNickname }}</p>
          <p>작성일: {{ column.createdAt?.slice(0, 10) }}</p>
          <p>가격: {{ column.price }}</p>
        </div>
      </div>
    </div>

    <div class="flex gap-6">
      <div class="flex-1 border rounded-lg p-6 whitespace-pre-wrap bg-[var(--newbitlightmode)]">{{ column.content }}</div>

      <div class="w-[300px] flex flex-col gap-4">
        <div class="flex gap-4 text-14px-regular">
          <label><input type="radio" value="approve" v-model="approvalDecision" /> <span>승인</span></label>
          <label><input type="radio" value="reject" v-model="approvalDecision" /> <span class="text-red-500">반려</span></label>
        </div>

        <textarea
            v-model="rejectionReason"
            placeholder="반려 사유"
            class="w-full h-[150px] p-3 border rounded resize-none"
        />

        <button @click="submitApproval" class="bg-[var(--newbitnormal)] text-white py-2 rounded">제출</button>
      </div>
    </div>
  </div>
</template>

<style scoped></style>