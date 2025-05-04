<template>
  <div class="max-w-[900px] mx-auto py-8 px-4">
    <!-- 목록으로 버튼 -->
    <router-link
        to="/admin/columns"
        class="inline-flex items-center gap-2 text-[var(--newbittext)] text-13px-regular bg-[var(--newbitlightmode)] border border-[var(--newbitdivider)] px-4 py-2 rounded-lg shadow-sm hover:bg-[var(--newbitlightmode-hover)] transition mb-6"
    >
      <span class="text-xl">←</span>
      <span>목록으로</span>
    </router-link>

    <!-- 썸네일 + 텍스트 -->
    <div class="flex gap-6 mb-6">
      <!-- 썸네일 -->
      <img
          :src="column.thumbnailUrl || fallbackImg"
          @error="(e) => (e.target.src = fallbackImg)"
          alt="썸네일"
          class="w-[280px] h-[180px] rounded-lg object-cover bg-gray-100"
      />

      <!-- 텍스트 정보 -->
      <div class="flex flex-col justify-between h-[180px] flex-1">
        <!-- 제목 -->
        <h1 class="text-heading2">{{ column.title }}</h1>

        <!-- 작성자/날짜/좋아요 -->
        <div class="flex flex-col gap-2.5 text-13px-regular text-[var(--newbitgray)]">
          <span>멘토 {{ column.writer }}</span>
          <span>작성일 {{ column.date }}</span>
          <button
              @click="toggleLike"
              class="flex items-center gap-1 px-3 py-1 w-fit border border-[var(--newbitdivider)] rounded-md text-13px-regular text-[var(--newbittext)] hover:bg-[var(--newbitlightmode-hover)] transition"
          >
            <img :src="isLiked ? heartActive : heartDefault" class="w-5 h-4.5" alt="하트" />
            <span>{{ column.likeCount }}</span>
          </button>
        </div>
      </div>
    </div>

    <!-- 본문 + 승인/반려 영역 -->
    <div class="flex justify-between gap-6 mt-6">
      <!-- 본문 -->
      <div class="flex-1 bg-[var(--newbitlightmode)] p-6 rounded-lg whitespace-pre-wrap text-16px-regular leading-relaxed border border-[var(--newbitdivider)]">
        {{ column.content }}
      </div>

      <!-- 승인/반려 폼 -->
      <div class="w-[300px] flex flex-col gap-4">
        <!-- 라디오 버튼 -->
        <div class="flex gap-4 text-14px-regular text-[var(--newbittext)]">
          <label class="flex items-center gap-1">
            <input type="radio" name="approval" value="approve" v-model="approvalDecision" />
            <span class="text-[var(--newbitnormal)] font-bold">승인</span>
          </label>
          <label class="flex items-center gap-1">
            <input type="radio" name="approval" value="reject" v-model="approvalDecision" />
            <span class="text-[var(--newbitred)] font-bold">반려</span>
          </label>
        </div>

        <!-- 반려 사유 -->
        <textarea
            v-model="rejectionReason"
            placeholder="반려 사유를 적어주세요."
            class="w-full h-[150px] p-3 text-14px-regular border border-[var(--newbitdivider)] rounded bg-[var(--newbitlightmode)] resize-none"
        />

        <!-- 제출 버튼 -->
        <button
            @click="submitApproval"
            class="bg-[var(--newbitnormal)] text-white px-4 py-2 rounded w-full"
        >
          제출
        </button>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const columnId = route.params.id

// 더미 칼럼 데이터 (API 연동 예정)
const column = ref({
  title: '강한 사람이 되는 방법',
  writer: '유관순',
  date: '2025.04.02',
  likeCount: 10,
  thumbnailUrl: '',
  content: `💪 1. 자기 자신을 이해하고 다스리는 힘

감정 조절 능력 키우기 : 화나 좌절 같은 감정을 억누르는 게 아니라, 인식하고 조절하는 것이 중요합니다.
자존감 기르기 : 남과 비교하지 않고 자신의 가치를 믿는 것.
실패를 견디는 힘 : 실패를 두려워하지 말고, 배움의 기회로 받아들이는 자세가 필요합니다.

✅ 추천 툴 목록

1. Postman
2. Notion`
})

// 좋아요 상태 관리
const isLiked = ref(false)
const toggleLike = () => {
  isLiked.value = !isLiked.value
  column.value.likeCount += isLiked.value ? 1 : -1
}

// 승인/반려 상태
const approvalDecision = ref(null)
const rejectionReason = ref('')

// 제출 처리
const submitApproval = () => {
  if (!approvalDecision.value) {
    alert('승인 또는 반려를 선택해주세요.')
    return
  }

  if (approvalDecision.value === 'reject' && !rejectionReason.value.trim()) {
    alert('반려 사유를 입력해주세요.')
    return
  }

  alert(`요청이 ${approvalDecision.value === 'approve' ? '승인' : '반려'}되었습니다.`)
  // TODO: 실제 API 요청
}

// 기본 이미지 경로
const fallbackImg = new URL('@/assets/image/product-skeleton.png', import.meta.url).href
const heartDefault = new URL('@/assets/image/heart-default.png', import.meta.url).href
const heartActive = new URL('@/assets/image/heart-active.png', import.meta.url).href
</script>

<style scoped></style>
