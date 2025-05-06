<template>
  <div class="w-full max-w-4xl mx-auto">
    <!-- 결제 내역 전용 필터 버튼 -->
    <div v-if="type === 'payment'" class="flex gap-2 mb-4">
      <button
        v-for="label in ['전체', '결제', '환불']"
        :key="label"
        @click="selectedPaymentFilter = label"
        :class="[
          'px-4 py-2 rounded-full text-13px-regular',
          selectedPaymentFilter === label
            ? 'bg-[var(--newbitnormal)] text-white'
            : 'bg-gray-100 text-gray-700',
        ]"
      >
        {{ label }}
      </button>
    </div>
    <!-- 기존 포인트/다이아 필터 버튼 -->
    <div
      v-else-if="type === 'diamond' || type === 'point'"
      class="flex gap-2 mb-4"
    >
      <button
        v-for="label in ['전체', '적립', '사용']"
        :key="label"
        @click="selectedFilter = label"
        :class="[
          'px-4 py-2 rounded-full text-13px-regular',
          selectedFilter === label
            ? 'bg-[var(--newbitnormal)] text-white'
            : 'bg-gray-100 text-gray-700',
        ]"
      >
        {{ label }}
      </button>
    </div>
    <!-- 기존 헤더/리스트 영역은 그대로 -->
    <div
      v-if="type === 'sale'"
      class="flex flex-row justify-between py-2 text-[var(--newbitgray)] text-sm font-medium border-b"
    >
      <div class="ps-24">판매 콘텐츠</div>
      <div class="flex flex-row gap-2">
        <div class="text-center">정산 일시</div>
        <div class="text-right min-w-[64px]">판매액(원)</div>
      </div>
    </div>
    <div
      v-if="type === 'settlement'"
      class="flex flex-row justify-between py-2 text-[var(--newbitgray)] text-sm font-medium border-b"
    >
      <div class="ps-24">정산 내역</div>
      <div class="flex flex-row gap-2">
        <div class="text-center">정산 일시</div>
        <div class="text-right min-w-[64px]">정산액(원)</div>
      </div>
    </div>
    <!-- 결제 내역 헤더 -->
    <div
      v-if="type === 'payment'"
      class="flex flex-row justify-between py-2 text-[var(--newbitgray)] text-sm font-medium border-b"
    >
      <div class="ps-24">결제 내역</div>
      <div class="flex flex-row gap-2">
        <!-- <div class="text-center">결제 일시</div> -->
        <div class="text-right min-w-[64px]">결제액(원)</div>
      </div>
    </div>
    <!-- 리스트 -->
    <div class="flex flex-col divide-y divide-[var(--newbitlightgray)]">
      <HistoryItem
        v-for="(item, index) in computedHistories"
        :key="item.historyId || item.id"
        :item="normalizeItem(item)"
        :prevItem="
          index > 0 ? normalizeItem(computedHistories[index - 1]) : null
        "
        :type="type"
      />
    </div>
  </div>
</template>

<script setup>
import HistoryItem from "./HistoryItem.vue";
import { ref, computed } from "vue";

const props = defineProps({
  histories: Array,
  type: String, // 'point' or 'diamond' or 'sale' or 'settlement' or 'payment'
});

const selectedFilter = ref("전체"); // 기존 포인트/다이아
const selectedPaymentFilter = ref("전체"); // 결제 내역 전용

// 결제 내역 전용 필터
const computedHistories = computed(() => {
  if (props.type === "payment") {
    if (selectedPaymentFilter.value === "결제") {
      return props.histories.filter((h) => h.status === "DONE");
    }
    if (selectedPaymentFilter.value === "환불") {
      return props.histories.filter(
        (h) => h.status === "CANCELED" || h.status === "PARTIAL_CANCELED"
      );
    }
    return props.histories;
  }
  // 기존 포인트/다이아 필터
  if (props.type === "diamond" || props.type === "point") {
    if (selectedFilter.value === "적립") {
      return props.histories.filter((h) => h.increaseAmount);
    }
    if (selectedFilter.value === "사용") {
      return props.histories.filter((h) => h.decreaseAmount);
    }
    return props.histories;
  }
  // 나머지 타입은 필터 없이 그대로
  return props.histories;
});

function normalizeItem(item) {
  if (props.type === "payment") {
    return {
      id: item.serviceId,
      createdAt: item.createdAt,
      amount: item.amount,
      serviceType: item.productName || item.serviceType,
      relatedInfo: item.paymentMethod || item.serviceTitleOrUserNickname,
      status: item.status,
    };
  }
  // 기존 포인트/다이아/판매/정산 로직은 그대로
  let readableType = item.serviceType;
  let readableInfo = item.serviceTitleOrUserNickname;
  let amount = item.saleAmount;
  if (item.serviceType === "COFFEECHAT") {
    if (props.type === "sale") readableType = "커피챗 판매";
    else if (props.type === "diamond") readableType = "커피챗 구매";
    readableInfo = readableInfo.concat("님과의 커피챗");
  } else if (item.serviceType === "COLUMN") {
    if (props.type === "sale") readableType = "칼럼 판매";
    else if (props.type === "diamond") readableType = "칼럼 구매";
  }
  if (props.type === "point" || props.type === "diamond") {
    amount = item.increaseAmount ?? -item.decreaseAmount;
  } else if (props.type === "settlement") {
    amount = item.settlementAmount;
  }
  return {
    id: item.serviceId,
    createdAt: item.createdAt,
    amount: amount,
    serviceType: readableType,
    relatedInfo: readableInfo,
    settledAt: item.settledAt,
    settlementMonth: item.settlementMonth,
    status: item.status,
  };
}
</script>

<style scoped></style>
