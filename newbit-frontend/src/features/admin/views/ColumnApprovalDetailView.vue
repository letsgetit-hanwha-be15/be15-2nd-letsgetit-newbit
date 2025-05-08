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
          <div class="flex items-center gap-1">
            <img :src="diamondIcon" alt="다이아" class="w-4 h-4" />
            <span>{{ column.price ?? 0 }}</span>
          </div>
        </div>
      </div>
    </div>

    <div class="flex gap-6">
      <div class="flex-1 border rounded-lg p-6 whitespace-pre-wrap bg-[var(--newbitlightmode)]">
        {{ column.content }}
      </div>

      <div class="w-[300px] flex flex-col gap-4">
        <!-- 승인/반려 라디오 -->
        <div class="flex gap-4 text-14px-regular">
          <label class="flex items-center gap-2">
            <input type="radio" value="approve" v-model="approvalDecision" />
            <span class="text-[var(--newbitnormal)] font-semibold">승인</span>
          </label>
          <label class="flex items-center gap-2">
            <input type="radio" value="reject" v-model="approvalDecision" />
            <span class="text-[var(--newbitred)] font-semibold">반려</span>
          </label>
        </div>

        <!-- 반려 사유 입력 -->
        <textarea
            v-model="rejectionReason"
            placeholder="반려 사유"
            class="w-full h-[150px] p-3 border rounded resize-none"
        />

        <!-- 제출 버튼 (상태 따라 색상 다르게) -->
        <button
            @click="submitApproval"
            :class="[
              'text-white py-2 rounded',
              approvalDecision === 'reject' ? 'bg-[var(--newbitred)]' : 'bg-[var(--newbitnormal)]'
            ]"
        >
          제출
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { approveCreateColumn, rejectCreateColumn, getAllColumnRequests } from '@/api/column'

const route = useRoute()
const router = useRouter()
const columnId = route.params.columnId
const requestType = route.query.type

const column = ref({})
const approvalDecision = ref(null)
const rejectionReason = ref('')
const fallbackImg = new URL('@/assets/image/product-skeleton.png', import.meta.url).href
const diamondIcon = new URL('@/assets/image/diamond-icon.png', import.meta.url).href

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
  if (!approvalDecision.value) {
    alert('승인 또는 반려를 선택해주세요.')
    return
  }

  if (approvalDecision.value === 'reject' && !rejectionReason.value.trim()) {
    alert('반려 사유를 입력해주세요.')
    return
  }

  try {
    if (approvalDecision.value === 'approve') {
      await approveCreateColumn({ columnRequestId: columnId })
    } else {
      await rejectCreateColumn({ columnRequestId: columnId, reason: rejectionReason.value })
    }

    alert(`요청이 ${approvalDecision.value === 'approve' ? '승인' : '반려'}되었습니다.`)
    router.push({ path: '/admin/columns', query: { reload: 'true' } })
  } catch (err) {
    console.error('승인/반려 요청 실패', err)
  }
}

onMounted(fetchData)
</script>








<!--<script setup>-->
<!--import { ref, onMounted } from 'vue'-->
<!--import { useRoute, useRouter } from 'vue-router'-->
<!--import { getAllColumnRequests } from '@/api/column'-->
<!--import { useToast } from 'vue-toastification'-->

<!--const route = useRoute()-->
<!--const router = useRouter()-->
<!--const toast = useToast()-->

<!--const columnId = route.params.columnId-->

<!--const column = ref({})-->
<!--const approvalDecision = ref(null)-->
<!--const rejectionReason = ref('')-->
<!--const fallbackImg = new URL('@/assets/image/product-skeleton.png', import.meta.url).href-->
<!--const diamondIcon = new URL('@/assets/image/diamond-icon.png', import.meta.url).href-->

<!--const fetchData = async () => {-->
<!--  try {-->
<!--    const res = await getAllColumnRequests({ page: 0, size: 100 })-->
<!--    const all = res.data.data.content-->
<!--    const found = all.find((item) => item.columnRequestId === Number(columnId))-->
<!--    if (found) column.value = found-->
<!--  } catch (e) {-->
<!--    console.error('상세 정보 불러오기 실패', e)-->
<!--  }-->
<!--}-->

<!--const submitApproval = () => {-->
<!--  if (!approvalDecision.value) {-->
<!--    toast.warning('승인 또는 반려를 선택해주세요.')-->
<!--    return-->
<!--  }-->

<!--  if (approvalDecision.value === 'reject' && !rejectionReason.value.trim()) {-->
<!--    toast.warning('반려 사유를 입력해주세요.')-->
<!--    return-->
<!--  }-->

<!--  const message =-->
<!--      approvalDecision.value === 'approve'-->
<!--          ? '요청이 승인되었습니다.'-->
<!--          : '요청이 반려되었습니다.'-->

<!--  toast.success(message)-->
<!--  router.push({ path: '/admin/columns', query: { reload: 'true' } })-->
<!--}-->

<!--onMounted(fetchData)-->
<!--</script>-->

