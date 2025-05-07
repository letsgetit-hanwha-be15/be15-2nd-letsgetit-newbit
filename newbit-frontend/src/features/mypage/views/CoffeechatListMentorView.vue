<script setup>

import {onMounted, reactive, ref} from "vue";
import CoffeechatList from "@/features/mypage/components/CoffeechatList.vue";
import PagingBar from "@/components/common/PagingBar.vue";
import {getMentorCoffeechats} from "@/api/coffeechat.js";

const statuses = [
  '전체',
  '승인대기',
  '결제대기',
  '커피챗대기',
  '이용완료',
  '취소/환불'
];

const selectedStatus = ref('전체');
const coffeechats = ref({});
const requestParams = reactive({
  status: null,
  page: 1,
  size: 10
});
const pagination = reactive({
  currentPage: 1,
  totalPages: 1,
  totalItems: 0
});

const fetchCoffeechats = async (page = 1) => {
  try {
    const {data: wrapper} = await getMentorCoffeechats(JSON.parse(JSON.stringify(requestParams)));
    const respData = wrapper.data;
    coffeechats.value = respData.coffeechats || [];
    Object.assign(pagination, respData.pagination ?? {});

  } catch (e) {
    console.log('커피챗 목록 조회 실패', e);
  }
  console.log('이동할 페이지: ', page)
}

const statusMap = {
  '승인대기': 'IN_PROGRESS',
  '결제대기': 'PAYMENT_WAITING',
  '커피챗대기': 'COFFEECHAT_WAITING',
  '이용완료': 'COMPLETE',
  '취소/환불': 'CANCEL',
};

function getStatusText(text) {
  if (text === '전체') return null;
  return statusMap[text] || null;
}

function filterCoffeechat(stat) {
  selectedStatus.value = stat;
  requestParams.status = getStatusText(stat);
  fetchCoffeechats();
}

onMounted(() => fetchCoffeechats());
</script>

<template>
  <div class="p-6 w-screen">
    <h1 class="text-heading1">커피챗 내역</h1>
    <div class="flex flex-wrap gap-2 mb-6">
      <button
          class="px-4 py-2 rounded-full text-sm font-medium transition bg-gray-100 text-gray-700"
      >
        STATUS
      </button>
      <button
          v-for="stat in statuses"
          :key="stat"
          @click="filterCoffeechat(stat)"
          :class="[
          'px-4 py-2 rounded-full text-sm font-medium transition',
          selectedStatus === stat
            ? 'bg-[var(--newbitnormal-hover)] text-white'
            : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
          ]"
      >
        {{ stat }}
      </button>
    </div>
    <CoffeechatList
        :coffeechats="coffeechats"
        :pagination="pagination"
        :isMentor=true
    />
    <PagingBar
        :currentPage="pagination.currentPage"
        :totalPage="pagination.totalPages"
        @page-changed="fetchCoffeechats"
    />
  </div>

</template>

<style scoped>

</style>