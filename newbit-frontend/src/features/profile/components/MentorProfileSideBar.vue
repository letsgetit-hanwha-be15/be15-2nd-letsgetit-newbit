<script setup>
import { useRouter } from 'vue-router'

const props = defineProps({
  profileImageUrl: {
    type: String,
    default: ''
  },
  nickname: {
    type: String,
    default: ''
  },
  jobName: {
    type: String,
    default: ''
  },
  isMyProfile: {
    type: Boolean,
    default: false
  },
  temperature: {
    type: Number,
    default: 0
  },
  price: {
    type: Number,
    default: 0
  },
  preferredTime: {
    type: String,
    default: ''
  },
  externalLinkUrl: {
    type: String,
    default: ''
  },
  introduction: {
    type: String,
    default: ''
  },
  isActive: {
    type: Boolean,
    default: ''
  }
})

const router = useRouter()

function goToProfileEdit() {
  router.push('/mypage/profile/edit')
}


function requestCoffeeChat() {
  router.push('/mentors/:id/coffeechat-register')
  console.log('커피챗 신청하기')
}
</script>

<template>
  <aside
      class="w-[406px] min-h-[960px] bg-[var(--newbitlight)] px-8 pt-10 pb-8 border-r border-[var(--newbitdivider)]"
  >
    <!-- 상단 -->
    <div class="flex flex-col items-center text-center mt-[42px]">
      <img
          :src="profileImageUrl"
          alt="프로필 이미지"
          class="w-[141px] h-[141px] rounded-full object-cover mb-[25px]"
      />

      <h2
          class="text-[color:var(--newbittext)] mb-[10px]"
          style="
          font-size: var(--newbit-text-heading1-font-size);
          font-weight: var(--newbit-text-heading1-font-weight);
          font-family: var(--newbit-text-heading1-font-family);
        "
      >
        {{ nickname }}
      </h2>

      <!-- 직무명 + 활동 상태 -->
      <div class="flex items-center justify-center gap-x-2 mb-6">
        <p class="text-heading3 text-[color:var(--newbittext)]">
          {{ jobName }}
        </p>
        <p class="text-heading3 text-[color:var(--newbittext)] ms-20">
          {{ isActive ? '활동 중' : '비활성' }}
        </p>
      </div>

      <button
          v-if="isMyProfile"
          @click="goToProfileEdit"
          class="text-sm text-white bg-[var(--newbitnormal)] px-4 py-2 rounded"
      >
        프로필 수정
      </button>
      <button
          v-else
          @click="requestCoffeeChat"
          class="text-sm text-white bg-[var(--newbitnormal)] px-4 py-2 rounded"
      >
        커피챗 신청
      </button>
    </div>

    <!-- 정보 카드 -->
    <div class="mt-8 bg-white border border-blue-300 rounded-md p-4 space-y-4 break-words">
      <div>
        <p class="text-heading3 text-[var(--newbittext)]">온도</p>
        <p class="text-16px-regular">{{ temperature }}℃</p>
      </div>
      <div>
        <p class="text-heading3 text-[var(--newbittext)]">커피챗 가격</p>
        <p class="text-16px-regular">{{ price }}💎 · 30분</p>
      </div>
      <div>
        <p class="text-heading3 text-[var(--newbittext)]">선호시간</p>
        <p class="text-16px-regular whitespace-pre-line">{{ preferredTime }}</p>
      </div>
      <div>
        <p class="text-heading3 text-[var(--newbittext)]">외부 링크</p>
        <a
            v-if="externalLinkUrl"
            :href="externalLinkUrl"
            class="text-16px-regular text-blue-500 underline break-all"
            target="_blank"
            rel="noopener noreferrer"
        >
          {{ externalLinkUrl }}
        </a>
        <p v-else class="text-16px-regular text-[var(--newbitgray)]">링크 없음</p>
      </div>
      <div>
        <p class="text-heading3 text-[var(--newbittext)]">멘토 소개</p>
        <p class="text-16px-regular whitespace-pre-line">{{ introduction }}</p>
      </div>
    </div>
  </aside>
</template>
