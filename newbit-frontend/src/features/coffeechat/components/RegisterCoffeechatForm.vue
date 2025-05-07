<script setup>

import {computed, reactive, ref, watch} from "vue";
import {useRoute, useRouter} from 'vue-router';
import {useToast} from "vue-toastification";

const {submitLabel} = defineProps({
  submitLabel: {type: String, default: '등록'}
});

const emit = defineEmits(['submit']);
const route = useRoute();
const router = useRouter();
const newTime = ref('');
const duration = ref(30);
const toast =  useToast();

const formData = reactive({
  requestMessage: '',
  purchaseQuantity: 1,
  mentorId: Number(route.params.id),
  requestTimes: []
})

function addTime() {
  if (formData.requestTimes.length >= 3) {
    alert('최대 3개의 시간만 추가할 수 있습니다.')
    return
  }

  if (newTime.value) {
    const withSeconds = newTime.value.length === 16
        ? newTime.value + ':00'
        : newTime.value

    formData.requestTimes.push(withSeconds)
    newTime.value = ''
  }
}

function removeTime(index) {
  formData.requestTimes.splice(index, 1)
}

function formatTime(datetimeString) {
  return datetimeString.replace('T', ' ')
}

watch(duration, (newDuration) => {
  formData.purchaseQuantity = Math.floor(newDuration / 30);
});

const isFormValid = computed(() => {
  const f = formData;
  return (
      f.purchaseQuantity &&
      f.requestMessage.length > 0 &&
      f.requestTimes.length > 0
  );
});

function submitForm() {
  emit('submit', {payload: formData});
}

function cancelRegister() {
  toast.info('커피챗 취소되었습니다');
  router.push(`/profile/mentor/${route.params.id}`);
}
</script>

<template>
  <div class="space-y-12 border rounded mx-2 my-5 p-4">
    <div class="space-y-4">
      <div>커피챗 진행 시간</div>
      <label class="block">
        <input type="number" id="duration" name="duration" value="30" min="30" step="30" class="border w-13 p-1 ml-2"
               v-model="duration">
        분
      </label>
    </div>
    <div class="space-y-4">
      <div>커피챗 요청 시간(시작시간)</div>
      <input class="ml-2 border p-1" type="datetime-local" v-model="newTime"/>
      <button @click="addTime"
              class="ml-2 rounded-md px-3 py-1 text-button text-[var(--newbitlight-active)] bg-[var(--newbitnormal)]"
              :disabled="formData.requestTimes.length >= 3">
        추가
      </button>

      <ul class="ml-2 text-13px-regular">
        <li v-for="(time, index) in (formData.requestTimes || [])"
            :key="index"
            class="flex items-center p-1">
          • {{ formatTime(time) }}
          <button @click="removeTime(index)"
                  class="flex items-center justify-center ml-2 px-3 py-0.5 rounded-md bg-[var(--newbitred)] text-[var(--newbitlight)]  text-button">
            삭제
          </button>
        </li>
      </ul>
    </div>
    <div class="space-y-4">
      <label for="request-message">요청 메시지</label>
      <textarea
          id="request-message"
          v-model="formData.requestMessage"
          rows="4"
          placeholder="요청 내용을 입력하세요"
          class="w-full border rounded p-2"
      />
    </div>
    <div class="flex flex-wrap gap-2 justify-end">
      <button type="submit"
              class="ml-2 rounded-md px-3 py-1 text-button text-[var(--newbitlight)] bg-[var(--newbitnormal)]"
              :disabled="!isFormValid"
              @click="submitForm">
        {{ submitLabel }}
      </button>
      <button type="button"
              @click="cancelRegister"
              class="flex items-center justify-center ml-2 px-3 py-0.5 rounded-md bg-[var(--newbitred)] text-[var(--newbitlight)]  text-button">
        취소
      </button>
    </div>
  </div>
</template>

<style scoped>

</style>