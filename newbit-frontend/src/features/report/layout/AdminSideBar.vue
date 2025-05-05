<template>
  <aside
    class="w-[214px] border border-[--newbitnormal] rounded-[8px] p-6 bg-[--newbitbackground] shrink-0"
  >
    <div class="mb-8" v-for="section in sidebarSections" :key="section.title">
      <div class="text-13px-regular text-[--newbitgray] mb-3">
        {{ section.title }}
      </div>
      <ul class="list-none p-0 m-0">
        <li v-for="item in section.items" :key="item.text">
          <router-link
            class="text-13px-regular text-black block py-3 px-4 rounded-[5px] mb-0.5 transition hover:bg-[--newbitlight] hover:text-[--newbitnormal]"
            :to="item.to"
            exact-active-class="active"
          >
            {{ item.text }}
          </router-link>
        </li>
      </ul>
    </div>
  </aside>
</template>

<script setup>
const sidebarSections = [
  {
    title: "신고 관리",
    items: [
      { text: "신고 목록 조회", to: "/admin" },
      { text: "허위 신고 목록 조회", to: "/admin/false-reports" },
    ],
  },
  {
    title: "상품 관리",
    items: [{ text: "상품 목록 조회", to: "/admin/products" }],
  },
  {
    title: "칼럼 관리",
    items: [{ text: "칼럼 승인 관리", to: "/admin/columns" }],
  },
  {
    title: "회원 관리",
    items: [{ text: "회원 관리 페이지", to: "/admin/users" }],
  },
];
</script>

<style>
.active {
  background: var(--newbitlight);
  color: var(--newbitnormal) !important;
}
</style>
