<script setup>


import {ref} from "vue";

const isCancelModalOpen = ref(false)

function cancelRegister() {
  isCancelModalOpen.value = true
}

function closeCancelModal() {
  isCancelModalOpen.value = false
}

function confirmCancel() {
  // todo : 취소 API 호출
  isCancelModalOpen.value = false
}
</script>

<template>
  <div>
    <div class="flex flex-wrap gap-2 justify-end pb-10">
      <button type="button"
              @click="cancelRegister"
              class="ml-2 rounded-md px-3 py-1 text-button bg-[var(--newbitred)] text-[var(--newbitlight)]  text-button">
        취소
      </button>
    </div>
  </div>
  <!-- 커피챗 취소 모달 -->
<!--  <div v-if="isCancelModalOpen" class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">-->
  <div v-if=true class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
    <div class="bg-[var(--newbitbackground)] rounded-lg p-6 w-[400px] shadow-lg">
      <h2 class="text-heading3 mb-4">커피챗 취소</h2>
      <p class="mb-6 text-13px-regular">
        커피챗 시작 하루 전 취소할 경우, 환불은 불가합니다.<br />
        정말 취소하시겠습니까?
      </p>
      <div class="flex justify-end gap-2">
        <button @click="closeCancelModal"
                class="bg-[var(--newbitred)] text-[var(--newbitlight)] px-4 py-1 rounded-md font-semibold">
          아니요
        </button>
        <button @click="confirmCancel"
                class="bg-[var(--newbitnormal)] text-[var(--newbitlight)] px-4 py-1 rounded-md font-semibold">
          네
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>