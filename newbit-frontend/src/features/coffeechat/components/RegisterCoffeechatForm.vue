<script setup>

import {computed, ref, watch} from "vue";
import {useRoute} from 'vue-router';

const {submitLabel} = defineProps({
  submitLabel: {type: String, default: '등록'}
});

const emit = defineEmits(['submit']);
const route = useRoute();
const newTime = ref('');
const duration = ref(30);

const formData = ref({
  requestMessage: '',
  purchaseQuantity: 1,
  mentorId: Number(route.params.id),
  requestTimes: []
})

function addTime() {
  if (formData.value.requestTimes.length >= 3) {
    alert('최대 3개의 시간만 추가할 수 있습니다.')
    return
  }

  if (newTime.value) {
    const withSeconds = newTime.value.length === 16
        ? newTime.value + ':00'
        : newTime.value

    formData.value.requestTimes.push(withSeconds)
    newTime.value = ''
  }
}

function removeTime(index) {
  formData.value.requestTimes.splice(index, 1)
}

function formatTime(datetimeString) {
  return datetimeString.replace('T', ' ')
}

watch(duration, (newDuration) => {
  formData.value.purchaseQuantity = Math.floor(newDuration / 30);
  console.log(formData.value.purchaseQuantity);
});

const isFormValid = computed(() => {
  const f = formData.value;
  return (
      f.purchaseQuantity &&
      f.requestMessage.length > 0 &&
      f.requestTimes.length > 0
  );
});

function submitForm() {
  emit('submit', {payload: formData.value});
}
</script>

<template>
  <div>
    <p>커피챗 진행 시간</p>
    <label>
      <input type="number" id="duration" name="duration" value="30" min="30" step="30" class="w-60"
             v-model="duration">
      분
    </label>
  </div>
  <div>
    <p>커피챗 요청 시간(시작시간)</p>
    <input type="datetime-local" v-model="newTime"/>
    <button @click="addTime" class="ml-2 text-button" :disabled="formData.requestTimes.length >= 3">
      추가
    </button>

    <ul>
      <li v-for="(time, index) in (formData.requestTimes || [])"
          :key="index"
          class="flex items-center">
        {{ formatTime(time) }}
        <button @click="removeTime(index)"
                class="flex items-center justify-center ml-2 w-4 h-4 rounded-full bg-[var(--newbitred)] text-button">
          -
        </button>
      </li>
    </ul>
  </div>
  <div>
    <label for="request-message">요청 메시지</label>
    <textarea
        id="request-message"
        v-model="formData.requestMessage"
        rows="4"
        placeholder="요청 내용을 입력하세요"
        class="w-full border rounded p-2"
    />
  </div>
  <button type="submit" class="text-button" :disabled="!isFormValid" @click="submitForm">{{ submitLabel }}</button>
  <!--  <button type="button" class="text-button">{{ cancelLabel }}</button>-->
</template>

<style scoped>

</style>