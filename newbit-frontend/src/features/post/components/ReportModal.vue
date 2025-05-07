<script setup>
import { ref } from 'vue'

const props = defineProps({
  title: {
    type: String,
    default: '신고'
  }
})

const emit = defineEmits(['close', 'submit'])

const selectedReason = ref('')
const reportContent = ref('')

const close = () => emit('close')

const submitReport = () => {
  if (!selectedReason.value || !reportContent.value.trim()) {
    alert('신고 유형과 내용을 모두 입력해주세요.')
    return
  }

  emit('submit', {
    reason: selectedReason.value,
    content: reportContent.value.trim()
  })
}
</script>

<template>
  <div class="fixed inset-0 bg-black/30 flex items-center justify-center z-50" @click.self="close">
    <div class="bg-white rounded-xl p-6 w-[400px] text-left shadow">
      <h2 class="text-xl font-bold mb-4">{{ title }}</h2>

      <!-- 신고 유형 선택 -->
      <select v-model="selectedReason" class="w-full border rounded px-3 py-2 mb-4">
        <option disabled value="">신고유형 선택</option>
        <option>욕설, 비하</option>
        <option>혐오 표현, 차별</option>
        <option>음란물, 선정적 표현</option>
        <option>사기, 사칭</option>
        <option>광고, 판매, 스팸</option>
        <option>불법 정보, 범죄 조장</option>
        <option>개인정보 노출, 사생활 침해</option>
        <option>저작권 침해</option>
        <option>도배 행위</option>
        <option>타인 명예 훼손</option>
        <option>기타</option>
      </select>

      <!-- 신고 내용 작성 -->
      <textarea v-model="reportContent"
                placeholder="신고 내용을 작성하세요"
                class="w-full border rounded px-3 py-2 h-24 mb-6 resize-none"></textarea>

      <div class="flex justify-end gap-2">
        <button @click="close" class="bg-[var(--newbitred)] text-white px-4 py-2 rounded">취소</button>
        <button @click="submitReport" class="bg-blue-500 text-white px-4 py-2 rounded">신고</button>
      </div>
    </div>
  </div>
</template>


