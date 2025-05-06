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
  }
})

console.log('✅ isMyProfile:', props.isMyProfile)

const router = useRouter()

function goToProfileEdit() {
  router.push('/mypage/profile/edit')
}
</script>

<template>
  <aside
      class="w-[406px] min-h-[960px] bg-[var(--newbitlight)] px-8 pt-10 pb-8 border-r border-[var(--newbitdivider)]"
  >
    <div class="flex flex-col items-center text-center mt-[42px]">
      <img
          :src="profileImageUrl"
          alt="프로필 이미지"
          class="w-[141px] h-[141px] rounded-full object-cover mb-[25px]"
      />

      <h2
          class="text-[color:var(--newbittext)] mb-[25px]"
          style="
            font-size: var(--newbit-text-heading1-font-size);
            font-weight: var(--newbit-text-heading1-font-weight);
            font-family: var(--newbit-text-heading1-font-family);
          "
      >
        {{ nickname }}
      </h2>

      <p
          class="text-[color:var(--newbittext)] mb-4"
          style="
            font-size: var(--newbit-text-heading3-font-size);
            font-weight: var(--newbit-text-heading3-font-weight);
            font-family: var(--newbit-text-heading3-font-family);
          "
      >
        {{ jobName }}
      </p>

      <!-- ✅ 수정 버튼 (absolute 제거, 무조건 보이게) -->
      <button
          v-if="isMyProfile === true"
          @click="goToProfileEdit"
          class="text-sm text-white bg-[var(--newbitnormal)] px-4 py-2 rounded"
      >
        프로필 수정
      </button>
    </div>
  </aside>
</template>
