<script setup>
import { ref, onMounted } from "vue"
import { useRoute } from "vue-router"
import { getMentorReviews } from "@/api/coffeechat"
import PagingBar from "@/components/common/PagingBar.vue"

const route = useRoute()
const mentorId = Number(route.params.id)

const reviews = ref([])
const currentPage = ref(1)
const totalPages = ref(1)
const pageSize = 5

const fetchReviews = async (page = 1) => {
  try {
    const res = await getMentorReviews(mentorId, page - 1, pageSize)
    const data = res.data.data

    reviews.value = data.reviews
    currentPage.value = data.pagination.currentPage
    totalPages.value = data.pagination.totalPage
  } catch (e) {
    console.error('리뷰 불러오기 실패:', e)
  }
}

const handlePageChange = (page) => {
  fetchReviews(page)
}

onMounted(() => {
  fetchReviews()
})
</script>

<template>
  <div class="space-y-6">
    <div
        class="space-y-2"
        v-for="review in reviews"
        :key="review.reviewId"
    >
      <div class="flex flex-wrap items-center space-x-2">
        <img
            :src="review.profileImageUrl || '/default-profile.png'"
            :alt="review.nickname"
            class="w-14 h-14 rounded-full object-cover"
        />
        <div class="text-13px-regular font-semibold">{{ review.nickname }}</div>
        <div class="flex ml-2">
          <svg
              v-for="star in 5"
              :key="star"
              xmlns="http://www.w3.org/2000/svg"
              class="w-5 h-5"
              :class="{
              'text-yellow-400': star <= review.rating,
              'text-gray-300': star > review.rating
            }"
              fill="currentColor"
              viewBox="0 0 20 20"
          >
            <path
                d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.286 3.975a1 1 0 00.95.69h4.188c.969 0
              1.371 1.24.588 1.81l-3.39 2.463a1 1 0 00-.364 1.118l1.286
              3.975c.3.921-.755 1.688-1.538
              1.118l-3.39-2.463a1 1 0 00-1.175
              0l-3.39 2.463c-.783.57-1.838-.197-1.538-1.118l1.286-3.975a1
              1 0 00-.364-1.118L2.044 9.402c-.783-.57-.38-1.81.588-1.81h4.188a1
              1 0 00.95-.69l1.286-3.975z"
            />
          </svg>
        </div>
      </div>

      <div class="border border-gray-200 rounded-xl p-3 bg-white">
        <p class="text-13px-regular text-gray-800">{{ review.comment }}</p>
      </div>
    </div>

    <PagingBar
        class="mt-6"
        :current-page="currentPage"
        :total-pages="totalPages"
        @page-change="handlePageChange"
    />
  </div>
</template>

<style scoped>
.text-13px-regular {
  font-size: 13px;
}
</style>
