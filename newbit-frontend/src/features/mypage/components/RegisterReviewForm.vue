<script setup>

import {ref} from "vue";
import {useRoute, useRouter} from "vue-router";

const {submitLabel} = defineProps({
  submitLabel: {type: String, default: '등록'}
});
const route = useRoute();
const router = useRouter();
const emit = defineEmits(['submit']);

const formData = ref({
  rating: 1,
  comment: null,
  tip: null,
  coffeechatId: Number(route.params.id)
})

const user = { // todo : 현재 로그인한 유저의 포인트 가져오기
  "point" : 100
}

function submitForm() {
  emit('submit', {formData: formData.value});
}

function cancelRegister() {

  toast.info('리뷰 등록 취소');
  router.back();
}

function setRating(value) {
  formData.value.rating = value
}

</script>

<template>
  <div class="space-y-12 border rounded mx-2 my-5 p-4">
    <div class="space-y-2">
      <div>별점</div>
      <div class="flex space-x-1">
        <button
            v-for="star in 5"
            :key="star"
            @click="setRating(star)"
            type="button"
            class="focus:outline-none"
        >
          <svg
              xmlns="http://www.w3.org/2000/svg"
              class="w-6 h-6"
              :class="{
            'text-yellow-400': star <= formData.rating,
            'text-gray-300': star > formData.rating
          }"
              fill="currentColor"
              viewBox="0 0 20 20"
          >
            <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.286 3.975a1 1 0 00.95.69h4.188c.969 0 1.371 1.24.588 1.81l-3.39 2.463a1 1 0 00-.364 1.118l1.286 3.975c.3.921-.755 1.688-1.538 1.118l-3.39-2.463a1 1 0 00-1.175 0l-3.39 2.463c-.783.57-1.838-.197-1.538-1.118l1.286-3.975a1 1 0 00-.364-1.118L2.044 9.402c-.783-.57-.38-1.81.588-1.81h4.188a1 1 0 00.95-.69l1.286-3.975z" />
          </svg>
        </button>
      </div>
    </div>
    <div class="space-y-4">
      <label for="request-message">리뷰 내용(옵션, 50자 이상)</label>
      <textarea
          id="request-message"
          v-model="formData.comment"
          rows="4"
          placeholder="50자 이상 입력 시 포인트를 드립니다"
          class="w-full border rounded p-2"
      />
    </div>
    <div class="space-y-4">
      <div>팁(옵션)</div>
      <label class="block">
        <select v-model="formData.tip" class="border w-20 p-1 ml-2">
          <option :value="null">선택 안 함</option>
          <option v-for="n in [20, 40, 60, 80, 100]" :key="n" :value="n">{{ n }} 개</option>
        </select>
        / {{ user.point }} 개
      </label>
    </div>


    <div class="flex flex-wrap gap-2 justify-end">
      <button type="submit"
              class="ml-2 rounded-md px-4 py-2 text-button text-[var(--newbitlight)] bg-[var(--newbitnormal)]"

              @click="submitForm">
        {{ submitLabel }}
      </button>
      <button type="button"
              @click="cancelRegister"
              class="flex items-center justify-center ml-2 px-4 py-2 rounded-md bg-[var(--newbitred)] text-[var(--newbitlight)]  text-button">
        취소
      </button>
    </div>
  </div>
</template>

<style scoped>

</style>