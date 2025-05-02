<script setup>

import MentorList from "@/features/coffeechat/components/MentorList.vue";
import {onMounted, reactive, ref} from "vue";
import {getMentors} from "@/features/coffeechat/api.js";
import SearchBar from "@/features/coffeechat/components/SearchBar.vue";
import PagingBar from "@/components/common/PagingBar.vue";

const mentors = ref([
      {
        "mentorId": 1,
        "userName": "김멘토",
        "nickname": "열정멘토",
        "profileImageUrl": "https://devmentor.s3.ap-northeast-2.amazonaws.com/profile/mentor1.png",
        "price": 1000,
        "temperature": 75.0,
        "techstackName": "HTML"
      },
      {
        "mentorId": 2,
        "userName": "이멘토",
        "nickname": "코딩마스터",
        "profileImageUrl": "https://devmentor.s3.ap-northeast-2.amazonaws.com/profile/mentor2.png",
        "price": 2000,
        "temperature": 60.0,
        "techstackName": "Python"
      },
      {
        "mentorId": 3,
        "userName": "멘토",
        "nickname": "mentor",
        "profileImageUrl": null,
        "price": 3000,
        "temperature": 90.0,
        "techstackName": "Java"
      },
      {
        "mentorId": 4,
        "userName": "박코치",
        "nickname": "디버깅왕",
        "profileImageUrl": "https://devmentor.s3.ap-northeast-2.amazonaws.com/profile/mentor4.png",
        "price": 1500,
        "temperature": 72.5,
        "techstackName": "C"
      },
      {
        "mentorId": 5,
        "userName": "정개발",
        "nickname": "백엔드전문가",
        "profileImageUrl": "https://devmentor.s3.ap-northeast-2.amazonaws.com/profile/mentor5.png",
        "price": 2500,
        "temperature": 85.0,
        "techstackName": "Spring Boot"
      },
      {
        "mentorId": 6,
        "userName": "최디자이너",
        "nickname": "UX마스터",
        "profileImageUrl": null,
        "price": 1800,
        "temperature": 68.0,
        "techstackName": "Figma"
      },
      {
        "mentorId": 7,
        "userName": "하PM",
        "nickname": "기획짱",
        "profileImageUrl": "https://devmentor.s3.ap-northeast-2.amazonaws.com/profile/mentor7.png",
        "price": 2100,
        "temperature": 73.0,
        "techstackName": "Notion"
      },
      {
        "mentorId": 8,
        "userName": "조멘토",
        "nickname": "알고리즘박사",
        "profileImageUrl": "https://devmentor.s3.ap-northeast-2.amazonaws.com/profile/mentor8.png",
        "price": 2700,
        "temperature": 95.0,
        "techstackName": "C++"
      },
      {
        "mentorId": 9,
        "userName": "윤코치",
        "nickname": "풀스택꿈나무",
        "profileImageUrl": null,
        "price": 1300,
        "temperature": 65.5,
        "techstackName": "Node.js"
      },
      {
        "mentorId": 10,
        "userName": "송멘토",
        "nickname": "프론트짱",
        "profileImageUrl": "https://devmentor.s3.ap-northeast-2.amazonaws.com/profile/mentor10.png",
        "price": 2200,
        "temperature": 78.0,
        "techstackName": "React"
      },
      {
        "mentorId": 11,
        "userName": "문개발",
        "nickname": "API마스터",
        "profileImageUrl": "https://devmentor.s3.ap-northeast-2.amazonaws.com/profile/mentor11.png",
        "price": 1950,
        "temperature": 66.0,
        "techstackName": "REST API"
      },
      {
        "mentorId": 12,
        "userName": "서멘토",
        "nickname": "DB장인",
        "profileImageUrl": null,
        "price": 1600,
        "temperature": 70.5,
        "techstackName": "MySQL"
      },
      {
        "mentorId": 13,
        "userName": "오엔지니어",
        "nickname": "서버지킴이",
        "profileImageUrl": "https://devmentor.s3.ap-northeast-2.amazonaws.com/profile/mentor13.png",
        "price": 2900,
        "temperature": 88.0,
        "techstackName": "Linux"
      }
    ]);
const pagination = reactive({
  currentPage : 1,
  totalPages : 1,
  totalItems : 0
});
const searchParams = reactive({
  userName : '',
  techStack : '',
  coffeechatPrice : null,
  temperature : null
});

const fetchMentors = async (page = 1) => {
/*  try {
    const { data : wrapper } = await getMentors({
      page,
      userName : searchParams.userName,
      techStack : searchParams.techStack,
      coffeechatPrice : searchParams.coffeechatPrice,
      temperature : searchParams.temperature
    });
    const respData = wrapper.data;
    mentors.value = respData.mentors || [];
    Object.assign(pagination, respData.pagination ?? {}); /!* 페이지네이션 추가 필요 *!/
  } catch(e) {
    console.log('멘토 목록 로드 실패', e);
  }*/
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