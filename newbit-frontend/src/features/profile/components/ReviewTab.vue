<script setup>

import {ref, defineProps, defineEmits, onMounted} from "vue";

const props = defineProps({
  pagination: Object
})

const emit = defineEmits(['update:pagination'])

const originalReview = ref({
  "success": true,
  "data": {
    "reviews": [
      {
        "reviewId": 1,
        "comment": "너무 좋았어요! 커피챗 가격도 비싼데 커피챗이 안좋으면 말이 안돼죠 자료를 10장 넘게 가지고 오셨어요! 온도가 100도이신분은 역시 🤓 뭔가 달라도 달라요 역시 네이버와 토스를 동시에 다니시는 분 짱짱",
        "profileImageUrl": "/src/assets/image/profile-mentee.png",
        "nickname": "dooley",
        "rating": 4,
        "userId": 3
      },
      {
        "reviewId": 2,
        "comment": "너무 좋았어요! 커피챗 가격도 비싼데 커피챗이 안좋으면 말이 안돼죠 자료를 10장 넘게 가지고 오셨어요! 온도가 100도이신분은 역시 🤓 뭔가 달라도 달라요 역시 네이버와 토스를 동시에 다니시는 분 짱짱",
        "profileImageUrl": "/src/assets/image/profile-mentee.png",
        "nickname": "dooley",
        "rating": 4,
        "userId": 3
      },
      {
        "reviewId": 3,
        "comment": "너무 좋았어요! 커피챗 가격도 비싼데 커피챗이 안좋으면 말이 안돼죠 자료를 10장 넘게 가지고 오셨어요! 온도가 100도이신분은 역시 🤓 뭔가 달라도 달라요 역시 네이버와 토스를 동시에 다니시는 분 짱짱",
        "profileImageUrl": "/src/assets/image/profile-mentee.png",
        "nickname": "dooley",
        "rating": 4,
        "userId": 3
      },
    ],
    "pagination": {
      "currentPage": 1,
      "totalPage": 1,
      "totalItems": 3
    },
    "errorCode": null,
    "message": null,
    "timestamp": "2025-05-04T22:55:16.9462967"
  }
});

const fetchReviews = (page = 1) => {
  // todo : API 호출 or emit
  console.log('이동할 페이지: ', page)
}

onMounted(() => {
  emit('update:pagination', originalReview.value.data.pagination)
});

console.log(originalReview.value.data.reviews)
</script>

<template>
  <div
      class="space-y-2"
      v-for="review in originalReview.data.reviews" :key="review.reviewId">
    <div class="flex flex-wrap items-center space-x-2">
      <img
          :src="review.profileImageUrl"
          :alt="review.nickname"
          class="w-14 h-14 rounded-full"
      />
      <div class="text-13px-regular ">{{ review.nickname }}</div>
      <div class="flex flex-wrap">
        <div
            v-for="star in 5"
            :key="star"
        >
          <svg
              xmlns="http://www.w3.org/2000/svg"
              class="w-6 h-6"
              :class="{
            'text-yellow-400': star <= review.rating,
            'text-gray-300': star > review.rating
          }"
              fill="currentColor"
              viewBox="0 0 20 20"
          >
            <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.286 3.975a1 1 0 00.95.69h4.188c.969 0 1.371 1.24.588 1.81l-3.39 2.463a1 1 0 00-.364 1.118l1.286 3.975c.3.921-.755 1.688-1.538 1.118l-3.39-2.463a1 1 0 00-1.175 0l-3.39 2.463c-.783.57-1.838-.197-1.538-1.118l1.286-3.975a1 1 0 00-.364-1.118L2.044 9.402c-.783-.57-.38-1.81.588-1.81h4.188a1 1 0 00.95-.69l1.286-3.975z" />
          </svg>
        </div>
      </div>
    </div>
    <div class="border rounded p-2">
      <p class="text-13px-regular">{{ review.comment }}</p>
    </div>
  </div>
</template>

<style scoped>

</style>