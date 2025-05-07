<script setup>

import MentorList from "@/features/coffeechat/components/MentorList.vue";
import {computed, onMounted, reactive, ref} from "vue";
import SearchBar from "@/features/coffeechat/components/SearchBar.vue";
import PagingBar from "@/components/common/PagingBar.vue";
import {getMentors} from "@/api/mentor.js";

const mentors = ref([]);
const pagination = reactive({
  currentPage: 1,
  totalPages: 1,
  totalItems: 0
});
const searchParams = reactive({
  userName: null,
  techStack: null,
  coffeechatPrice: null,
  temperature: null
});

const fetchMentors = async (page = 1) => {
    try {
      const { data : wrapper } = await getMentors({
        techstackName : searchParams.techStack,
        userName : searchParams.userName,
        coffeechatPrice : searchParams.coffeechatPrice,
        temperature : searchParams.temperature,
        page : page,
        size : 9
      });
      const respData = wrapper.data;
      mentors.value = respData.mentors || [];
      Object.assign(pagination, respData.pagination ?? {}); /* 페이지네이션 추가 필요 */
    } catch(e) {
      console.log('멘토 목록 로드 실패', e);
    }
}

const onSearch = params => {
    Object.assign(searchParams, params);
    pagination.currentPage = 1;
    fetchMentors();
}

onMounted(() => fetchMentors());
</script>

<template>
  <SearchBar @search="onSearch"/>
  <MentorList :mentors="mentors"/>
  <PagingBar
      v-bind="pagination"
      @page-changed="fetchMentors"
  />
</template>

<style scoped>

</style>