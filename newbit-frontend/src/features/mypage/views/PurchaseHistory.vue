<script setup>
import { ref, onMounted } from "vue";
import { useAuthStore } from "@/features/stores/auth";
import HistoryList from "@/features/mypage/components/HistoryList.vue";
import PagingBar from "@/components/common/PagingBar.vue";
import api from "@/api/axios";

const authStore = useAuthStore();
const historyItems = ref(null);
const historyType = "payment";
const isLoading = ref(false);
const error = ref(null);

const loadPaymentHistory = async (page = 1) => {
  isLoading.value = true;
  error.value = null;
  try {
    const response = await api.get("/feature/payments/query/users/me", {
      params: {
        page: page - 1,
        size: 10,
        sort: "createdAt,desc",
      },
      headers: {
        Authorization: `Bearer ${authStore.accessToken}`,
      },
    });

    if (response.data?.success) {
      const pageData = response.data.data;
      historyItems.value = {
        histories: (pageData.content || [])
          .filter((payment) => payment.paymentStatus !== "READY")
          .map((payment) => {
            return {
              historyId: payment.paymentId,
              serviceId: payment.orderId,
              createdAt: payment.approvedAt,
              serviceType: payment.orderName,
              serviceTitleOrUserNickname: payment.paymentMethod,
              paymentMethod: payment.paymentMethod,
              amount: payment.amount,
              status: payment.paymentStatus,
            };
          }),
        pagination: {
          currentPage: (pageData.number || 0) + 1,
          totalPage: pageData.totalPages,
          totalItems: pageData.totalElements,
        },
      };
    } else {
      throw new Error("응답 형식 오류");
    }
  } catch (e) {
    console.error("결제 내역 조회 실패:", e);
    error.value = "다이아 구매 내역을 불러오는 데 실패했습니다.";
  } finally {
    isLoading.value = false;
  }
};

const handlePageChange = (page) => {
  loadPaymentHistory(page);
};

onMounted(() => {
  loadPaymentHistory();
});
</script>

<template>
  <div class="w-full max-w-4xl mx-auto p-6">
    <h2 class="text-heading3 mb-4">결제 내역</h2>

    <div v-if="isLoading">로딩 중...</div>
    <div v-else-if="error" class="text-red-500">{{ error }}</div>
    <div v-else-if="historyItems && historyItems.histories.length > 0">
      <HistoryList
        :histories="historyItems.histories"
        :pagination="historyItems.pagination"
        :type="historyType"
      />
      <PagingBar
        :currentPage="historyItems.pagination.currentPage"
        :totalPage="historyItems.pagination.totalPage"
        @page-change="handlePageChange"
      />
    </div>
    <div v-else class="text-gray-400">다이아 구매 내역이 없습니다.</div>
  </div>
</template>
